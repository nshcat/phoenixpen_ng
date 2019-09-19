package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.core.Updateable
import com.phoenixpen.game.core.WeightedList
import com.phoenixpen.game.core.WeightedPair
import com.phoenixpen.game.data.*
import com.phoenixpen.game.data.biome.*
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.Map
import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
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
    val treeHolder = TreeSystem(this)

    /**
     * Snow system
     */
    val snowSystem: SnowSystem

    /**
     * Water system
     */
    val waterSystem: WaterSystem

    /**
     * Information about the current biome
     */
    lateinit var biomeConfiguration: BiomeConfiguration

    /**
     * Information about the seasons
     */
    val seasonConfiguration: SeasonConfiguration

    /**
     * The system that manages seasons in the game
     */
    val seasonSystem: SeasonSystem

    /**
     * Simulation state initialization procedure
     */
    init
    {
        GlobalLogger.d("Simulation", "Initializing simulation")

        // Load season information
        this.seasonConfiguration = Json.parse(SeasonConfiguration.serializer(), this.resources.json("season_config.json"))

        // The material manager needs to be initialized and materials loaded before
        // the map can be loaded
        this.materialManager.loadMaterials(this.resources, "materials.json")

        // Load item types from JSON resource
        this.itemManager.loadItems(this.resources, "items.json")

        // Load covering types
        this.coveringManager.loadCoverings(this.resources, "coverings.json")

        // Initialize map decoration system
        this.mapDecorationSystem = MapDecorationSystem(this.resources)

        // Initialize season system
        this.seasonSystem = SeasonSystem(this)

        // Initialize water system
        this.waterSystem = WaterSystem(this.resources)

        // Setup biome generator
        val biomeGenerator = BiomeGenerator(this.resources, "biome_test.json")

        // Load biome
        biomeGenerator.apply(this)

        this.mapDecorationSystem.addDecoration(Position3D(15, 1, 15), "test_plant")

        // Connect map to structure and covering holders
        this.map.registerHolder(this.treeHolder as StructureHolder)
        this.map.registerHolder(this.mapDecorationSystem)
        this.map.registerHolder(this.waterSystem)

        // Update all map data structures to detect all initial data
        this.map.updateDatastructures()

        // Cover everything in snow
        this.snowSystem = SnowSystem(this)
        this.map.registerHolder(this.snowSystem)
        this.map.registerHolder(this.treeHolder as CoveringHolder)
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

        // Update season system
        this.seasonSystem.update(elapsedTicks)

        // Update water system
        this.waterSystem.update(elapsedTicks)

        // Update snow system
        this.snowSystem.update(elapsedTicks)

        // Update tree system
        this.treeHolder.update(elapsedTicks)
    }
}