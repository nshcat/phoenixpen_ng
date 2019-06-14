package com.phoenixpen.android.game.data.biome

import android.content.Context
import com.phoenixpen.android.game.simulation.Simulation
import java.util.*

/**
 * A class containing all data required to setup a biome scene in the current simulation instance
 *
 * @property context The Android application context. Used to extract resources.
 * @param mapInfoId The resource ID pointing to a JSON document containing the map information
 * @param mapTemplateIds Resource IDs describing the different map template layers
 * @property treeIds Resource IDs describing the tree data set
 * @property decorationIds Resource IDs describing the decorations data set
 */
class BiomeDataSet(
        val context: Context,
        mapInfoId: Int, mapTemplateIds: List<Int>,
        val treeIds: Optional<TreeDataSetIds> = Optional.empty(),
        val decorationIds: Optional<DecorationDataSetIds> = Optional.empty()
)
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

        // Generate decorations if requested
        if (decorationIds.isPresent)
        {
            DecorationDataSet(this.context, decorationIds.get()).apply(simulation)
        }
    }
}