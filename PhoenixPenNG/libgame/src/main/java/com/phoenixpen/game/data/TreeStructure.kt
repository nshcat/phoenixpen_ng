package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.map.MapDimensions
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.JsonInput

/**
 * Type for storing the dimensions of a structure layer
 */
typealias LayerDimensions = ScreenDimensions

/**
 * Type for storing the dimensions of a whole structure
 */
typealias StructureDimensions = MapDimensions

/**
 * A single layer of the tree structure. Note that all layers in a structure need to have the same
 * dimensions.
 */
class StructureLayer(val dimensions: LayerDimensions)
{
    /**
     * All entries in this layer
     */
    val entries = ArrayList<PlaceholderType>(dimensions.width * dimensions.height)

    /**
     * Fill entries with "empty" value
     */
    init
    {
        for(i in 0 until dimensions.width * dimensions.height)
            this.entries.add(PlaceholderType.Empty)
    }

    /**
     * Retrieve placeholder entry at given 2d position inside this layer.
     *
     * @param position Position to retrieve entry from
     * @return Entry at given position, if inside bounds
     */
    fun entryAt(position: Position): PlaceholderType
    {
        // Perform bounds check
        if(!this.isInBounds(position))
            throw IllegalArgumentException("StructureLayer::entryAt: Position out of layer bounds")

        return this.entries[this.indexFor(position)]
    }

    /**
     * Calculate linear index into [entries] list for given two dimensional position
     *
     * @param position Position to calculate linear index for
     * @return Linear index for given position
     */
    fun indexFor(position: Position): Int
    {
        return (position.y * this.dimensions.width) + position.x
    }

    /**
     * Check whether the given position is inside layer bounds
     *
     * @param position Position to check
     * @return Flag indicating whether given position is inside layer bounds
     */
    fun isInBounds(position: Position): Boolean
    {
        return (position.x >= 0 && position.x < this.dimensions.width) && (position.y >= 0 && position.y < this.dimensions.height)
    }
}

/**
 * A class managing a single tree structure. Note that no real logic like actually creating the tree
 * part structures is done in this class, it is all done by class [Tree].
 * Note that all layers are required to have the exact same [LayerDimensions].
 */
@Serializable(with=TreeStructureSerializer::class)
class TreeStructure
{
    /**
     * All layers in this structure
     */
    val layers = ArrayList<StructureLayer>()

    /**
     * Retrieve total height of the structure. This is based entirely on the number of layers, so
     * empty layers do count here aswell.
     *
     * @return Total height of the structure
     */
    fun height(): Int = this.layers.size

    /**
     * Determine dimensions of whole structure. Note that this is only possible if there is at least
     * one layer present.
     *
     * @return Dimensions of the whole structure, if at least one layer is present.
     */
    fun dimensions(): StructureDimensions
    {
        // We cant determine the dimensions if there is not at least one layer present
        if(this.layers.size <= 0)
            throw IllegalStateException("Cant determine dimensions of empty TreeStructure")

        // Otherwise just use the dimensions of the first layer, since they are required to be the same
        // for all the layers in this structure.
        val layer = this.layers.first()
        return StructureDimensions(layer.dimensions.width, this.height(), layer.dimensions.height)
    }
}

/**
 * Custom serializer for tree structures
 */
@Serializer(forClass = TreeStructure::class)
object TreeStructureSerializer: KSerializer<TreeStructure>
{
    override val descriptor: SerialDescriptor =
            StringDescriptor.withName("TreeStructure")

    override fun serialize(output: Encoder, obj: TreeStructure)
    {
        throw NotImplementedError("Serialization not implemented for class TreeStructure")
    }

    override fun deserialize(input: Decoder): TreeStructure
    {
        // Our tree structure instance which will be filled with data during deserialization
        val treeStructure = TreeStructure()

        // The JSON AST only supports unchecked casts, so we need to be prepared for any
        // type mismatch exceptions here
        try
        {
            // Retrieve JSON input
            val jsonInput = input as JsonInput

            // Decode JSON to AST and interpret as JSON array
            val root = jsonInput.decodeJson().jsonArray

            // We expect a JSON array for each layer
            for(entry in root)
            {
                // Cast to array
                val entryArray = entry.jsonArray

                // Retrieve layer lines
                val lines = entryArray.content.map{ x -> x.primitive.content }

                // Check that they are all the same length
                if(lines.map(String::length).distinct().size != 1)
                {
                    throw IllegalStateException("Error while parsing TreeStructure: Layer width not consistent.\nLayer in question: $entry")
                }

                // Create layer instance
                val layer = StructureLayer(LayerDimensions(lines.first().length, lines.size))

                // Parse all lines
                for((iz, line) in lines.withIndex())
                {
                    for((ix, c) in line.withIndex())
                    {
                        // Try to convert placerholder character to placeholder enumeration value
                        layer.entries[layer.indexFor(Position(ix, iz))] = PlaceholderType.fromCharacter(c)
                    }
                }

                // Add layer to structure
                treeStructure.layers.add(layer)
            }

            // Check that all layers have the same dimensions
            if(treeStructure.layers.map(StructureLayer::dimensions).distinct().size == 1)
            {
                throw IllegalStateException("Error while parsing TreeStructure: Either zero layers or layer size not consistent")
            }

            return treeStructure
        }
        catch(e: Exception)
        {
            throw IllegalStateException("Failed to deserialize TreeStructure: ${e.message}")
        }
    }
}