package com.phoenixpen.game.data.biome

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.data.Material
import com.phoenixpen.game.map.Map
import com.phoenixpen.game.map.MapDimensions
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json

/**
 * A class containing all the data that is needed to create the map terrain.
 *
 * @param resources The resource provider to retrieve data from
 * @param mapInfoId The resource id of the JSON document containing general map info and key
 * @param mapTemplateIds All ids of map template layer bitmaps
 */
class MapDataSet(resources: ResourceProvider, mapInfoId: String, mapTemplateIds: List<String>)
{
    /**
     * The map information needed to interpret the map template
     */
    val mapInfo: MapInfo = Json.indented.parse(
            MapInfo.serializer(),
            resources.json(mapInfoId)
    )

    /**
     * The map template
     */
    val mapTemplate: BiomeTemplate = BiomeTemplate.fromBitmaps(resources, *mapTemplateIds.toTypedArray())

    /**
     * Actually load the map
     */
    fun load(simulation: Simulation): Map
    {
        // Retrieve dimensions of the layers
        val layerDimensions = this.mapTemplate.dimensions()

        // Construct map dimensions
        val mapDimensions = MapDimensions(layerDimensions.width, this.mapInfo.height, layerDimensions.height)

        // Construct blank map instance
        val map = Map(mapDimensions)

        // Fill it with data
        for((iy, layer) in this.mapTemplate.layers.withIndex())
        {
            // Iterate through all entries in the layer and populate map
            for(ix in 0 until layerDimensions.width)
            {
                for(iz in 0 until layerDimensions.height)
                {
                    // Build absolute map position
                    val mapPosition = Position3D(ix, iy, iz)

                    // Retrieve layer entry
                    val entry = layer.entryAt(Position(ix, iz))

                    // Ignore if its black
                    if(entry == Color.black)
                        continue

                    // Translate
                    val keyEntry = this.mapInfo.key[entry] ?: throw IllegalStateException("Key entry missing for color ${entry.r}:${entry.g}:${entry.b}")

                    // Store in map
                    val cell = map.cellAt(mapPosition)
                    cell.material = Material.create(simulation.materialManager.lookupMaterial(keyEntry.material))
                    cell.state = keyEntry.cellType
                }
            }
        }

        return map
    }
}