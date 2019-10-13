package com.phoenixpen.game.data.biome

import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.graphics.Color
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.map
import java.util.concurrent.ThreadLocalRandom

/**
 * A class aggregating data on how map decorations should appear in the biome.
 */
class DecorationDataSet(resources: ResourceProvider, cfg: BiomeGenerationConfiguration)
{
    /**
     * The decoration key used to translate colours in the biome template into actual decoration structure types
     */
    val decorationKey: TypeKey = Json.indented.parse(
            (Color.serializer()).to(TypeKeyEntry.serializer()).map,
            resources.json(cfg.decorationInfoId)
    )

    /**
     * The decoration template
     */
    val decorationTemplate: BiomeTemplate = BiomeTemplate.fromBitmaps(resources, *(cfg.decorationLayerIds.toTypedArray()))

    /**
     * Actually apply the decoration template to the current simulation state
     */
    fun apply(simulation: Simulation)
    {
        // Retrieve dimensions of the layers
        val layerDimensions = this.decorationTemplate.dimensions()

        // Fill it with data
        for((iy, layer) in this.decorationTemplate.layers.withIndex())
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
                    val keyEntry = this.decorationKey[entry] ?: throw IllegalStateException("Key entry missing for color ${entry.r}:${entry.g}:${entry.b}")

                    // If density is not 1, check if we need to generate here.
                    if(keyEntry.density < 1.0)
                    {
                        val randomVal = ThreadLocalRandom.current().nextDouble(1.0)

                        // If the generated number is bigger than our density, we failed the check and continue
                        // with the next layer entry
                        if(randomVal > keyEntry.density)
                            continue
                    }

                    // Either way, we know we need to generate a decoration now.
                    // Pick a decoration type identifier.
                    val decorationTypeId = keyEntry.types.drawElement()

                    // Generate the decoration
                    simulation.mapDecorationSystem.addDecoration(mapPosition, decorationTypeId)
                }
            }
        }
    }
}