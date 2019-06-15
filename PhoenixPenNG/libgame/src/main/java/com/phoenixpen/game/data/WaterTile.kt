package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.DrawInfo
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.ascii.TileInstance
import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.core.Updateable
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * A water tile used to create effects like flowing rivers. In this game, they're implemented as structures.
 *
 * @property tileInstance Current tile state
 * @property type Type class instance describing the properties of this water tile
 * @property bodyType The type of this body of water, e.g. river
 * @property salinity If there is salt in this water tile
 * @param position Position in the game world
 */
class WaterTile(
        val type: WaterType,
        val bodyType: WaterBodyType = WaterBodyType.Still,
        val salinity: WaterSalinity = WaterSalinity.Freshwater,
        position: Position3D,
        private val tileInstance: TileInstance
    ): Structure(type.basicData, position), Updateable
{
    /**
     * The current foam tile, if available.
     */
    private var foamTile: Optional<DrawInfo> = Optional.empty()

    /**
     * The current foam tile lifetime counter, if available
     */
    private var foamLifetime: Optional<TickCounter> = Optional.empty()

    /**
     * Retrieve tile represenation for this water tile.
     */
    override fun tile(fancyMode: Boolean): DrawInfo
    {
        // Are we already displaying a foam tile?
        if(!foamLifetime.isPresent)
        {
            // Check if we need to draw a foam tile
            if (this.type.foamChance > 0.0 && !this.type.foamTiles.isEmpty())
            {
                // Draw random number
                val randomNr = ThreadLocalRandom.current().nextDouble(1.0)

                // Decide if we should draw a foam tile
                if(randomNr <= this.type.foamChance)
                {
                    // Create new tick counter
                    this.foamLifetime = Optional.of(TickCounter(this.type.foamDuration))
                    this.foamTile = Optional.of(this.type.foamTiles.drawElement())

                    // Proceed to draw it
                    return this.foamTile.get()
                }
            }

            // Just draw normally
            return this.type.tileType.tile(this.tileInstance)
        }
        else
        {
            // Display the foam tile
            return foamTile.get()
        }
    }

    /**
     * Update internal logic based on given number of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // Update animation
        this.tileInstance.update(elapsedTicks)

        // If there is a foam tile active, check if its lifetime is expired
        if(this.foamLifetime.isPresent)
        {
            // Is it expired?
            if(this.foamLifetime.get().update(elapsedTicks) > 0)
            {
                // If so, clear foam tile information
                this.foamTile = Optional.empty()
                this.foamLifetime = Optional.empty()
            }
        }
    }

    companion object
    {
        /**
         * Create new water tile instance with given data
         *
         * @param type Type class instance to use
         * @param pos Position in the game world
         * @param bodyType The type of the body of water this tile belongs to
         * @param salinity The salt content of the body of water this tile belongs to
         * @param animationOffset Animation offset. Used to implement river animation effect
         * @return New water tile instance
         *
         */
        fun create(type: WaterType, pos: Position3D, bodyType: WaterBodyType, salinity: WaterSalinity, animationOffset: Int = 0): WaterTile
        {
            return WaterTile(type, bodyType, salinity, pos, type.tileType.createInstance(animationOffset))
        }
    }
}