package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.core.Updateable
import com.phoenixpen.game.data.*
import com.phoenixpen.game.data.biome.BiomeDataSet
import com.phoenixpen.game.data.biome.TreeDataSetIds
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.Map
import com.phoenixpen.game.resources.ResourceProvider
import java.util.*

/**
 * The main simulation class which aggregates all game subsystems and data holding objects
 *
 * @property resources The resource provider to retrieve game resources from
 */
class Simulation(val resources: ResourceProvider): Updateable
{
    /**
     * The main game map.
     */
    lateinit var map: Map

    /**
     * The material manager for map cells
     */
    val materialManager: MaterialManager = MaterialManager()

    /**
     * The item manager containing all known item types
     */
    val itemManager = ItemManager()

    /**
     * Manager for all covering types.
     */
    val coveringManager = CoveringManager()

    /**
     * System managing all map decorations.
     */
    val mapDecorationSystem: MapDecorationSystem

    /**
     * Tree system
     */
    val treeHolder = TreeSystem(this.resources)

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
        GlobalLogger.d("Simulation", "Initializing simulation")

        // The material manager needs to be initialized and materials loaded before
        // the map can be loaded
        this.materialManager.loadMaterials(this.resources, "materials.json")

        // Load item types from JSON resource
        this.itemManager.loadItems(this.resources, "items.json")

        // Load covering types
        this.coveringManager.loadCoverings(this.resources, "coverings.json")

        // Initialize map decoration system
        this.mapDecorationSystem = MapDecorationSystem(this.resources)

        // Initialize water system
        this.waterSystem = WaterSystem(this.resources)

        // Create biome data set
        val biomeDataSet = BiomeDataSet(
                this.resources,
                "biome_test_mapinfo.json", listOf("biome_test_layer0.bmp", "biome_test_layer1.bmp"),
                Optional.of(TreeDataSetIds("biome_test_trees.json", listOf("biome_test_trees_layer0.bmp")))
        )

        // Load biome
        biomeDataSet.apply(this)

        this.mapDecorationSystem.addDecoration(Position3D(15, 1, 15), "test_plant")

        // Connect map to structure and covering holders
        this.map.registerHolder(this.treeHolder)
        this.map.registerHolder(this.mapDecorationSystem)
        this.map.registerHolder(this.waterSystem)

        // Update all map data structures to detect all initial data
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