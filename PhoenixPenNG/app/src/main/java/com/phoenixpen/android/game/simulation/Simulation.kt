package com.phoenixpen.android.game.simulation

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.game.core.Updateable
import com.phoenixpen.android.game.data.*
import com.phoenixpen.android.game.data.biome.BiomeDataSet
import com.phoenixpen.android.game.data.biome.DecorationDataSetIds
import com.phoenixpen.android.game.data.biome.TreeDataSetIds
import com.phoenixpen.android.game.map.Map
import java.util.*

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

        // Load covering types
        this.coveringManager.loadCoverings(this.context, R.raw.coverings)

        // Initialize map decoration system
        this.mapDecorationSystem = MapDecorationSystem(this.context)

        // Initialize water system
        this.waterSystem = WaterSystem(this.context)

        // Create biome data set
        val biomeDataSet = BiomeDataSet(
                this.context,
                R.raw.biome_test_mapinfo, listOf(R.drawable.biome_test_layer0, R.drawable.biome_test_layer1),
                Optional.of(TreeDataSetIds(R.raw.biome_test_trees, listOf(R.drawable.biome_test_trees_layer0)))
                //Optional.of(DecorationDataSetIds(R.raw.biome_test_decorations, listOf(R.drawable.biome_test_decorations_layer0)))
        )

        // Load biome
        biomeDataSet.apply(this)

        // Connect map to structure and covering holders
        this.map.registerHolder(this.treeHolder)
        this.map.registerHolder(this.mapDecorationSystem)
        this.map.registerHolder(this.waterSystem)

        // Update all map data structures to detect all initial data
        this.map.updateDatastructures()

        // Cover everything in snow
        this.snowSystem = SnowSystem(this)
        //this.map.registerHolder(this.snowSystem)
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