package com.phoenixpen.game.data.biome

import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import java.util.*

/**
 * A class used to generate a map based on a biome description
 *
 * @property resources Resource manager used to retrieve biome resources
 * @property biomeConfigId The resource ID pointing to a JSON document containing biome configuration info
 */
class BiomeGenerator(
        val resources: ResourceProvider,
        val biomeConfigId: String
)
{
    /**
     * Apply this biome data set, making the biome that it describes the current biome
     */
    fun apply(simulation: Simulation)
    {
        // Retrieve biome config
        val config = Json.parse(BiomeConfiguration.serializer(), this.resources.json(this.biomeConfigId))
        simulation.biomeConfiguration = config
        val genConfig = config.generationInfo

        // Generate terrain
        simulation.map = MapDataSet(this.resources, genConfig.mapInfoId, genConfig.mapLayerIds).load(simulation)

        // Generate trees if any layers where given
        if (genConfig.treeLayerIds.isNotEmpty())
        {
            TreeComponent(this.resources, genConfig).apply(simulation)
        }

        // Generate decorations if requested
        if (genConfig.decorationLayerIds.isNotEmpty())
        {
            DecorationDataSet(this.resources, genConfig).apply(simulation)
        }

        // Generate water tiles if requested
        if (genConfig.waterLayerIds.isNotEmpty())
        {
            WaterDataSet(this.resources, genConfig).apply(simulation)
        }
    }
}