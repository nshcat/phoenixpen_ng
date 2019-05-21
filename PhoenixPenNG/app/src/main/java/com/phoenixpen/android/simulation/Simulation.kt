package com.phoenixpen.android.simulation

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.data.ItemManager
import com.phoenixpen.android.data.MaterialManager
import com.phoenixpen.android.data.StructureManager
import com.phoenixpen.android.map.Map
import com.phoenixpen.android.map.TestMapGenerator

/**
 * The main simulation class which aggregates all game subsystems and data holding objects
 *
 * @property context The android application context
 */
class Simulation(val context: Context)
{
    /**
     * The main game map.
     */
    val map: Map

    /**
     * The material manager for map cells
     */
    val materialManager: MaterialManager = MaterialManager()

    /**
     * The item manager containing all known item types
     */
    val itemManager: ItemManager = ItemManager()

    /**
     * The structure manager containing all known structure types
     */
    val structureManager: StructureManager = StructureManager()

    /**
     * Simulation state initialization procedure
     */
    init
    {
        // The material manager needs to be initialized and materials loaded before
        // the map can be loaded
        this.materialManager.loadMaterials(this.context, R.raw.materials)

        // Load item types from JSON resource
        this.itemManager.loadItems(this.context, R.raw.items)

        // Load structure types
        this.structureManager.loadStructures(this.context, R.raw.structures)

        // Load map. In this case, a test map is regenerated on each app launch.
        this.map = Map.load(TestMapGenerator(materialManager))
    }

    /**
     * Update simulation state by given amount of elapsed ticks.
     *
     * @param elapsedTicks The number of elapsed ticks since last update
     */
    fun update(elapsedTicks: Int)
    {

    }
}