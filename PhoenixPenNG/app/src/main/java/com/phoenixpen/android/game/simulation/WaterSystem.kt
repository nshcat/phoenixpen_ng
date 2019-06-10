package com.phoenixpen.android.game.simulation

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.game.ascii.Position
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.ascii.minus
import com.phoenixpen.android.game.core.Updateable
import com.phoenixpen.android.game.data.*
import com.phoenixpen.android.game.map.MapCellState

/**
 * Class managing all the water tiles in the game world
 *
 * @property context The Android application context
 */
class WaterSystem(val context: Context): StructureHolder, Updateable
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
        this.waterManager.loadWaterTypes(this.context, R.raw.water_types)

        val type = this.waterManager.lookupWaterType("default_water")

        // Create some water
        // Generate lower area in middle
        /*for(ix in 5 until 10)
        {
            for (iz in 5 until 10)
            {
                val pos = Position3D(ix, 1, iz)

                this.waterTiles.add(
                        WaterTile.create(
                                type,
                                pos,
                                WaterBodyType.Still,
                                WaterSalinity.Freshwater,
                                iz - 5
                                )
                )
            }
        }*/
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
}