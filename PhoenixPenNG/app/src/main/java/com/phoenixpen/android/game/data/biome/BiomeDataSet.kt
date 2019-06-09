package com.phoenixpen.android.game.data.biome

import android.content.Context
import com.phoenixpen.android.game.simulation.Simulation

/**
 * A class containing all data required to setup a biome scene in the current simulation instance
 */
class BiomeDataSet(context: Context, mapInfoId: Int, mapTemplateIds: List<Int>)
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
        simulation.map = this.mapDataSet.load(simulation)
    }
}