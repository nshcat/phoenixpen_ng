package com.phoenixpen.android.game.data.biome

import android.content.Context
import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.Position
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.simulation.Simulation
import kotlinx.serialization.json.Json
import kotlinx.serialization.map
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ThreadLocalRandom

/**
 * A simple class holding all the resource IDs needed to construct a decoration data set.
 */
data class DecorationDataSetIds(val keyId: Int, val templateIds: List<Int>)

/**
 * A class aggregating data on how map decorations should appear in the biome.
 */
class DecorationDataSet(context: Context, ids: DecorationDataSetIds)
{
    /**
     * The decoration key used to translate colours in the biome template into actual tree types
     */
    val decorationKey: TypeKey = Json.indented.parse(
            (Color.serializer()).to(TypeKeyEntry.serializer()).map,
            BufferedReader(InputStreamReader(context.resources.openRawResource(ids.keyId))).readText()
    )

    /**
     * The decoration template
     */
    val decorationTemplate: BiomeTemplate = BiomeTemplate.fromBitmaps(context, *(ids.templateIds.toIntArray()))

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