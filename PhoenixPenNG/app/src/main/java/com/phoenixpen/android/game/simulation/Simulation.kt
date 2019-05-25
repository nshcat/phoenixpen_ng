package com.phoenixpen.android.game.simulation

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.data.ItemManager
import com.phoenixpen.android.game.data.MaterialManager
import com.phoenixpen.android.game.data.SimpleStructure
import com.phoenixpen.android.game.data.SimpleStructureManager
import com.phoenixpen.android.game.map.Map
import com.phoenixpen.android.game.map.TestMapGenerator

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
    val itemManager = ItemManager()

    /**
     * Manager for all simple structure types. For testing purposes
     */
    val simpleStructureManager = SimpleStructureManager()

    /**
     * Holder for all simple structures. For testing purposes.
     */
    val simpleStructureHolder = SimpleStructureHolder()

    /**
     * Tree system
     */
    val treeHolder = TreeHolder(this.context)

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

        // Load simple structure types
        this.simpleStructureManager.loadSimpleStructures(this.context, R.raw.simple_structures)

        // Load map. In this case, a test map is regenerated on each app launch.
        this.map = Map.load(TestMapGenerator(materialManager))

        // Add a test structure
        this.simpleStructureHolder.structureCollection.add(
                SimpleStructure(this.simpleStructureManager.lookupSimpleStructureSafe("boulder"),
                        Position3D(6, 1, 6))
        )

        // Add a tree
        this.treeHolder.generateTree(Position3D(12, 2, 12), "test_tree")
        this.treeHolder.generateTree(Position3D(5, 2, 1), "test_tree")
        this.treeHolder.generateTree(Position3D(1, 2, 8), "test_tree")
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