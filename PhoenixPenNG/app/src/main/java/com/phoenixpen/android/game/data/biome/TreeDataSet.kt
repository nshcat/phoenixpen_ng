package com.phoenixpen.android.game.data.biome

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.Position
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.simulation.Simulation
import com.phoenixpen.android.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.map
import java.util.concurrent.ThreadLocalRandom


/**
 * A simple class holding all the resource IDs needed to construct a tree data set.
 */
data class TreeDataSetIds(val keyId: String, val templateIds: List<String>)

/**
 * A class aggregating data on how trees should appear in the biome.
 */
class TreeDataSet(resources: ResourceProvider, ids: TreeDataSetIds)
{
    /**
     * The tree key used to translate colours in the biome template into actual tree types
     */
    val treeKey: TypeKey = Json.indented.parse(
            (Color.serializer()).to(TypeKeyEntry.serializer()).map,
            resources.json(ids.keyId)
    )

    /**
     * The tree template
     */
    val treeTemplate: BiomeTemplate = BiomeTemplate.fromBitmaps(resources, *(ids.templateIds.toTypedArray()))

    /**
     * Actually apply the tree template to the current simulation state
     */
    fun apply(simulation: Simulation)
    {
        // Retrieve dimensions of the layers
        val layerDimensions = this.treeTemplate.dimensions()

        // Fill it with data
        for((iy, layer) in this.treeTemplate.layers.withIndex())
        {
            // Iterate through all entries in the layer and populate map
            for(ix in 0 until layerDimensions.width)
            {
                for(iz in 0 until layerDimensions.height)
                {
                    // Build absolute map position for the possible tree
                    val mapPosition = Position3D(ix, iy, iz)

                    // Retrieve layer entry
                    val entry = layer.entryAt(Position(ix, iz))

                    // Ignore if its black
                    if(entry == Color.black)
                        continue

                    // Translate
                    val keyEntry = this.treeKey[entry] ?: throw IllegalStateException("Key entry missing for color ${entry.r}:${entry.g}:${entry.b}")

                    // If density is not 1, check if we need to generate here.
                    if(keyEntry.density < 1.0)
                    {
                        val randomVal = ThreadLocalRandom.current().nextDouble(1.0)

                        // If the generated number is bigger than our density, we failed the check and continue
                        // with the next layer entry
                        if(randomVal > keyEntry.density)
                            continue
                    }

                    // Either way, we know we need to generate a tree now.
                    // Pick a tree type identifier.
                    val treeTypeId = keyEntry.types.drawElement()

                    // Generate the tree
                    simulation.treeHolder.generateTree(mapPosition, treeTypeId)
                }
            }
        }
    }
}