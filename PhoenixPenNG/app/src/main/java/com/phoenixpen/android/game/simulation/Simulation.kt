package com.phoenixpen.android.game.simulation

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.core.Updateable
import com.phoenixpen.android.game.data.*
import com.phoenixpen.android.game.map.Map
import com.phoenixpen.android.game.map.TestMapGenerator
import kotlin.random.Random

/**
 * The main simulation class which aggregates all game subsystems and data holding objects
 *
 * @property context The android application context
 */
class Simulation(val context: Context): Updateable
{
    /**
     * The main game map.
     */
    var map: Map

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
     * Manager for all covering types.
     */
    val coveringManager = CoveringManager()

    /**
     * Holder for all simple structures. For testing purposes.
     */
    val simpleStructureHolder = SimpleStructureHolder()

    /**
     * Tree system
     */
    val treeHolder = TreeSystem(this.context)

    /**
     * Snow system
     */
    val snowSystem: SnowSystem

    /**
     * Water system
     */
    val waterSystem: WaterSystem

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

        // Load covering types
        this.coveringManager.loadCoverings(this.context, R.raw.coverings)

        // Load map. In this case, a test map is regenerated on each app launch.
        this.map = Map.load(TestMapGenerator(materialManager))

        // Add a test structure
        this.simpleStructureHolder.structureCollection.add(
                SimpleStructure(this.simpleStructureManager.lookupSimpleStructureSafe("boulder"),
                        Position3D(6, 1, 6))
        )

        this.waterSystem = WaterSystem(this.context)

        // Connect map to structure and covering holders
        this.map.registerHolder(this.treeHolder)
        this.map.registerHolder(this.simpleStructureHolder)
        this.map.registerHolder(this.waterSystem)

        // Add a tree
        /*this.treeHolder.generateTree(Position3D(14, 2, 3), "test_tree")
        this.treeHolder.generateTree(Position3D(12, 2, 12), "test_tree")
        this.treeHolder.generateTree(Position3D(5, 2, 1), "test_tree")
        this.treeHolder.generateTree(Position3D(1, 2, 8), "test_tree")*/


        for(x in 1 .. 30)
        {
            Random.nextInt(4)
            Random.nextInt(4)
            Random.nextInt(4)
            Random.nextInt(4)
            Random.nextInt(4)


            val pos = Position3D(Random.nextInt(0, 27), 2, Random.nextInt(0, 50))
            this.treeHolder.generateTree(pos, "test_tree")
        }

        this.map.updateDatastructures()

        // Cover everything in snow
        this.snowSystem = SnowSystem(this)
        this.map.registerHolder(this.snowSystem)
    }

    /**
     * Update simulation state by given amount of elapsed ticks.
     *
     * @param elapsedTicks The number of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // Update map (acceleration structures inside map class)
        // THIS HAS TO BE DONE FIRST
        this.map.update(elapsedTicks)

        // Update water system
        this.waterSystem.update(elapsedTicks)
    }
}