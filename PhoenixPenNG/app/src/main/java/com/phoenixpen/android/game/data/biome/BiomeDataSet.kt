package com.phoenixpen.android.game.data.biome

import android.content.Context
import com.phoenixpen.android.game.simulation.Simulation
import java.util.*

/**
 * A class containing all data required to setup a biome scene in the current simulation instance
 */
class BiomeDataSet(val context: Context, mapInfoId: Int, mapTemplateIds: List<Int>, val treeIds: Optional<TreeDataSetIds> = Optional.empty())
{
    /**
     * The map data used to create the map instance
     */
    val mapDataSet = MapDataSet(context, mapInfoId, mapTemplateIds)

    /**
     * Apply this biome data set, making the biome that it describes the current biome
     */
    fun apply(simulation: Simulation)
    {
        // Generate map
        simulation.map = this.mapDataSet.load(simulation)

        // Generate trees if requested
        if (treeIds.isPresent)
        {
            TreeDataSet(this.context, treeIds.get()).apply(simulation)
        }
    }
}