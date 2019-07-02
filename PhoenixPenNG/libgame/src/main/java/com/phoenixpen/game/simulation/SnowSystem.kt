package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.MapCellState
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * A system managing snow in the game
 */
class SnowSystem(simulation: Simulation): System(simulation), CoveringHolder
{
    /**
     * The different states this system can be in.
     */
    enum class SystemState
    {
        /**
         * Idling in months that arent winter, with no snow present
         */
        Idle,

        /**
         * In the progress of spawning snow coverings
         */
        SpawningSnow,

        /**
         * Idling in winter, with snow completely spawned
         */
        IdleWinter,

        /**
         * Thawing snow
         */
        ThawingSnow
    }

    /**
     * A list used to store all remaining map columns to cover with snow
     */
    private val mapColumns = LinkedList<Position>()

    /**
     * All coverings managed by this system
     */
    private var coverings = LinkedList<Covering>()

    /**
     * Modification animation used to animate snow spawning and thawing
     */
    private val modificationAnim = ModificationAnimation(0.08, 1)

    /**
     * The current state the system is in
     */
    private var currentState: SystemState = SystemState.Idle

    /**
     * Spawn snow tile in given map column
     */
    private fun spawnSnowTile(position: Position)
    {
        // Retrieve map reference
        val map = this.simulation.map

        // Retrieve map dimensions
        val dimensions = map.dimensions

        // Retrieve snow covering type
        val snowType = this.simulation.coveringManager.lookupCovering("snow")

        // "Raycast" from the sky downwards until something was hit
        for(iy in dimensions.height-1 downTo 0)
        {
            // Current position
            val pos = Position3D(position.x, iy, position.y)

            // Retrieve map cell at this position
            val cell = map.cellAt(pos)

            // If its solid, cancel search for this column
            if(cell.state == MapCellState.Solid)
                break

            // If it is ground, create snow and stop further search
            if(cell.state == MapCellState.Ground || map.getStructureAtExact(pos, true).isPresent)
            {
                this.coverings.add(Covering.create(snowType, pos))
                break
            }
        }
    }

    /**
     * Retrieve all coverings managed by this system
     *
     * @return A collection of all coverings managed by this system
     */
    override fun coverings(): Collection<Covering>
    {
        return this.coverings
    }

    /**
     * Advance simulation state by given number of ticks
     *
     * @param elapsedTicks Number of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // Action depends on the current system state
        when(this.currentState)
        {
            // When in idle mode, check if winter has begun.
            SystemState.Idle ->
            {
                // If spring has begun, switch to snow dispersal mode, but only if snow is enabled.
                if(this.simulation.seasonSystem.currentSeason == Season.Winter && this.simulation.biomeConfiguration.snowEnabled)
                {
                    // We dont do any snow spawning in here, that is done in the next update step.
                    this.currentState = SystemState.SpawningSnow

                    // Clear buffer to be safe
                    this.mapColumns.clear()

                    // Retrieve current map dimensions
                    val mapDimensions = this.simulation.map.dimensions

                    // Iterate over all (x,z) coordinate pairs within the map dimensions and
                    // store all map columns in buffer
                    for(ix in 0 until mapDimensions.width)
                    {
                        for(iz in 0 until mapDimensions.depth)
                        {
                            this.mapColumns.add(Position(ix, iz))
                        }
                    }

                    // Shufle columns in order to make snow spread appear organic
                    this.mapColumns.shuffle()

                    // Reset modification animation
                    this.modificationAnim.reset(this.mapColumns.size)

                    GlobalLogger.d("SnowSystem", "Switched to snow spawning state")
                }
            }
            // When in idle winter mode, check if winter is over
            SystemState.IdleWinter ->
            {
                // The snow will start to thaw in spring
                if(this.simulation.seasonSystem.currentSeason == Season.Spring)
                {
                    // Switch to snow thawing mode
                    this.currentState = SystemState.ThawingSnow

                    // Randomly shuffle covering collection in order to make snow disappear "organically"
                    this.coverings.shuffle()

                    // Reset modification animation
                    this.modificationAnim.reset(this.coverings.size)

                    GlobalLogger.d("SnowSystem", "Switched to snow thawing state")
                }
            }
            // Perform snow spawning step if needed
            SystemState.SpawningSnow -> this.spawnSnowStep(elapsedTicks)
            SystemState.ThawingSnow ->
            {
                this.thawSnowStep(elapsedTicks)

                // If we are already in summer, be sure to just erase everything
                if(this.simulation.seasonSystem.currentSeason == Season.Summer)
                {
                    this.coverings.clear()
                    this.currentState = SystemState.Idle
                }
            }
        }
    }

    /**
     * Possibly perform spawn snow steps
     */
    private fun spawnSnowStep(elapsedTicks: Int)
    {
        // If the animation is finished, we have to switch states
        if(!this.modificationAnim.isActive)
        {
            this.currentState = SystemState.IdleWinter
        }
        else
        {
            // Otherwise retrieve number of map columns to spawn snow on
            val steps = this.modificationAnim.update(elapsedTicks)

            // Spawn snow on those columns
            for(column in this.mapColumns.take(steps))
            {
                this.spawnSnowTile(column)
            }

            // Remove columns from list
            for(i in 1 .. steps)
                this.mapColumns.removeFirst()
        }
    }

    /**
     * Possibly perform thaw snow steps
     */
    private fun thawSnowStep(elapsedTicks: Int)
    {
        // If the animation is finished, we have to switch states
        if(!this.modificationAnim.isActive)
        {
            this.currentState = SystemState.Idle
        }
        else
        {
            // Otherwise retrieve number of coverings to remove
            val steps = this.modificationAnim.update(elapsedTicks)

            // Remove snow coverings
            for(i in 1 .. steps)
                this.coverings.removeFirst()
        }
    }
}