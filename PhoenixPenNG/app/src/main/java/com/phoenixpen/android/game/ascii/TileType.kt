package com.phoenixpen.android.game.ascii

import com.phoenixpen.android.game.core.WeightedTileList
import com.phoenixpen.android.game.core.WeightedTileListSerializer
import com.phoenixpen.android.game.core.WeightedTilePair
import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumSerializer
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonInput

/**
 * Enumeration describing the various ways the graphical representation of a game object can be specified.
 */
enum class TileTypeMode
{
    /**
     * A single, fixed tile
     */
    Static,

    /**
     * A selection of tiles combined with probabilities.
     */
    Varied,

    /*Animated*/ // Not yet supported
}

/**
 * An opaque handle class used to store information about the instantiation of a tile type as part of
 * a game object. This class, for example, stores the index into the weighted tile list, if varied tiles
 * are activated.
 */
data class TileInstance(val tileIndex: Int = -1)

/**
 * A class encapsulating the different ways a graphical representation of a game object can be specified.
 * It supports static, varied and animated tiles. It is supposed to be stored inside type class instances.
 * It can create instances of type [TileInstance] which acts like an opaque handle to be saved in object
 * instances in order to save e.g. the chosen varied tile.
 *
 * @property mode The current tile type mode. Per default, this is set to static.
 * @property glyph Static glyph, only used in static mode.
 * @property foreground Static foreground color, only used in static mode.
 * @property background Static background color, only used in static mode.
 */
@Serializable
class TileType(
        val mode: TileTypeMode = TileTypeMode.Static,
        val staticTile: DrawInfo = DrawInfo(),
        val variedTiles: WeightedTileList = WeightedTileList(listOf())
)
{

    /**
     * Create a new tile instance based on this tile type. In the case of varied tiles,
     * this will pick a graphical representation.
     *
     * @return New tile instance based on this tile type.
     */
    fun createInstance(): TileInstance
    {
        return when(this.mode)
        {
            TileTypeMode.Static -> TileInstance()
            TileTypeMode.Varied -> TileInstance(this.variedTiles.drawIndex())
        }
    }

    /**
     * Retrieve graphical representation based on this tile type and the given tile instance.
     *
     * @param instance Instance used to retrieve tile draw info
     * @return Draw information describing how to draw this tile type
     */
    fun tile(instance: TileInstance): DrawInfo
    {
        return when(this.mode)
        {
            TileTypeMode.Static -> this.staticTile
            TileTypeMode.Varied -> this.variedTiles.elementAt(instance.tileIndex)
        }
    }
}


/**
 * Serializer implementation for [TileType].
 */
@Serializer(forClass=TileType::class)
class TileTypeSerializer : KSerializer<TileType>
{
    override val descriptor: SerialDescriptor
        get() = StringDescriptor.withName("TileType")

    @ImplicitReflectionSerializer
    override fun deserialize(input: Decoder): TileType
    {
        // The JSON AST only supports unchecked casts, so we need to be prepared for any
        // type mismatch exceptions here
        try {
            // Retrieve JSON input
            val jsonInput = input as JsonInput

            // Decode JSON to AST and interpret as JSON object
            val root = jsonInput.decodeJson().jsonObject

            // Assume static glyph mode per default
            var mode = TileTypeMode.Static

            // Retrieve mode entry
            if(root.containsKey("mode"))
            {
                // Parse mode entry
                val modeEntry = root.getPrimitive("mode")
                mode = Json.parse(EnumSerializer(TileTypeMode::class), modeEntry.content)
            }

            // The data entry definitly has to exist.
            if(!root.containsKey("data"))
                throw RuntimeException("missing \"data\" entry")

            // Decide what to do depending on the mode value
            return when (mode)
            {
                TileTypeMode.Static ->
                {
                    // Interpret data entry as draw info
                    val entry = root.getObject("data")
                    TileType(mode, staticTile = Json.parse(DrawInfo.serializer(), entry.toString()))
                }
                TileTypeMode.Varied ->
                {
                    // Interpret data entry as array
                    val entry = root.getArray("data")
                    TileType(mode, variedTiles = Json.parse(WeightedTileListSerializer(), entry.toString()))
                }

                else -> throw RuntimeException("Invalid tile type mode value")
            }
        }
        catch(ex: Exception)
        {
            throw IllegalStateException("Failed to deserialize TileType: ${ex.message}")
        }
    }

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, obj: TileType)
    {
        throw NotImplementedError("Serialization for TileType is not supported")
    }
}