package com.phoenixpen.game.data.biome

import WaterKey
import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.map
import java.util.concurrent.ThreadLocalRandom

/**
 * A class aggregating data on how water tiles should appear in the biome.
 */
class WaterDataSet(resources: ResourceProvider, cfg: BiomeGenerationConfiguration)
{
    /**
     * The water key used to translate colours in the biome template into actual water tile types
     */
    val waterKey: WaterKey = Json.indented.parse(
            (Color.serializer()).to(WaterKeyEntry.serializer()).map,
            resources.json(cfg.waterInfoId)
    )

    /**
     * The water tile template
     */
    val waterTemplate: BiomeTemplate = BiomeTemplate.fromBitmaps(resources, *(cfg.waterLayerIds.toTypedArray()))

    /**
     * Actually apply the decoration template to the current simulation state
     */
    fun apply(simulation: Simulation)
    {
        // Retrieve dimensions of the layers
        val layerDimensions = this.waterTemplate.dimensions()

        // Fill it with data
        for((iy, layer) in this.waterTemplate.layers.withIndex())
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
                    val keyEntry = this.waterKey[entry] ?: throw IllegalStateException("Key entry missing for color ${entry.r}:${entry.g}:${entry.b}")

                    // Create water tile
                    simulation.waterSystem.addWaterTile(mapPosition, keyEntry.type, keyEntry.animOffset)
                }
            }
        }
    }
}