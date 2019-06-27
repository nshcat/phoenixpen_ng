package com.phoenixpen.game.data.biome

import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import java.util.*

/**
 * A class containing all data required to setup a biome scene in the current simulation instance
 *
 * @property resources Resource manager used to retrieve biome resources
 * @property biomeConfigId The resource ID pointing to a JSON document containing biome configuration info
 * @param mapInfoId The resource ID pointing to a JSON document containing the map information
 * @param mapTemplateIds Resource IDs describing the different map template layers
 * @property treeIds Resource IDs describing the tree data set
 * @property decorationIds Resource IDs describing the decorations data set
 */
class BiomeDataSet(
        val resources: ResourceProvider,
        val biomeConfigId: Optional<String>,
        mapInfoId: String, mapTemplateIds: List<String>,
        val treeIds: Optional<TreeDataSetIds> = Optional.empty(),
        val decorationIds: Optional<DecorationDataSetIds> = Optional.empty()
)
{
    /**
     * The map data used to create the map instance
     */
    val mapDataSet = MapDataSet(this.resources, mapInfoId, mapTemplateIds)

    /**
     * Apply this biome data set, making the biome that it describes the current biome
     */
    fun apply(simulation: Simulation)
    {
        // Retrieve biome config
        if(this.biomeConfigId.isPresent)
            simulation.biomeConfiguration = Json.parse(BiomeConfiguration.serializer(), this.resources.json(this.biomeConfigId.get()))

        // Generate map
        simulation.map = this.mapDataSet.load(simulation)

        // Generate trees if requested
        if (treeIds.isPresent)
        {
            TreeDataSet(this.resources, treeIds.get()).apply(simulation)
        }

        // Generate decorations if requested
        if (decorationIds.isPresent)
        {
            DecorationDataSet(this.resources, decorationIds.get()).apply(simulation)
        }
    }
}