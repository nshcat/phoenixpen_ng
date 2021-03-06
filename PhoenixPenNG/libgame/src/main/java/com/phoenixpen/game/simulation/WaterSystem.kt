package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.core.Updateable
import com.phoenixpen.game.data.*
import com.phoenixpen.game.resources.ResourceProvider

/**
 * Class managing all the water tiles in the game world
 *
 * @property resources The current resource provider
 */
class WaterSystem(val resources: ResourceProvider): StructureHolder, Updateable
{
    /**
     * Collection of all water tiles currently present in the game world
     */
    val waterTiles = ArrayList<WaterTile>()

    /**
     * Class that manages all the water type classes
     */
    val waterManager = WaterManager()

    /**
     * Initialize all the type class managers present in this system
     */
    init
    {
        // Load water types
        this.waterManager.loadWaterTypes(this.resources, "water_types.json")
    }

    /**
     * Retrieve all structures managed by this class
     *
     * @return Collection of all structures managed by this class
     */
    override fun structures(): Collection<Structure>
    {
        return this.waterTiles
    }

    /**
     * Update state of the water system and all water tiles
     */
    override fun update(elapsedTicks: Int)
    {
        // Update all water tiles
        this.waterTiles.forEach{ x -> x.update(elapsedTicks)}
    }

    /**
     * Create a new water tile.
     *
     * @param position Position of the water ile in the game world
     * @param type Water type identifier
     * @param bodyType Water body type, i.e. ocean or river
     * @param salinity The salt contents of the water tile
     * @param animOffset Animation offset used to create flowing water effect
     */
    fun addWaterTile(position: Position3D, type: String, animOffset: Int = 0)
    {
        // Try to retrieve the water type
        val waterType = this.waterManager.lookupWaterType(type)

        // Create tile and add
        this.waterTiles.add(WaterTile.create(waterType, position, animOffset))
    }

    /**
     * Mark a given position in the game world as being a beach. This is used to generate waves.
     *
     * @param position Position in the game world to mark as a beach
     */
    fun addBeachMarker(position: Position3D)
    {
        throw NotImplementedError("Not implemented")
    }

    /**
     * Mark a given position in the game world as being the origin point for waves.
     *
     * @param position Position in the game world to mark as a wave origin point
     */
    fun addWaveOriginMarker(position: Position3D)
    {
        throw NotImplementedError("Not implemented")
    }
}