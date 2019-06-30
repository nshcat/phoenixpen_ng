package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.MapCellState
import java.util.*
import kotlin.collections.ArrayList
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
    private val coverings = LinkedList<Covering>()

    /**
     * A tick counter used to both create and thaw snow
     */
    private var modificationCounter: Optional<TickCounter> = Optional.empty()

    /**
     * The period between each covering modification step
     */
    private val modificationPeriod = 1

    /**
     * The percentage of tiles to modify in each modification step
     */
    private val modificationsPerStepPercent = 0.08

    /**
     * The current state the system is in
     */
    private var currentState: SystemState = SystemState.Idle

    /**
     * The number of modifications to do in each modification step
     */
    private var modificationsPerStep: Int = 0

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

                    // We also need to initialize the tick counter
                    this.modificationCounter = Optional.of(TickCounter(this.modificationPeriod))

                    // And we need to put all map columns into the buffer

                    // Clear buffer to be safe
                    this.mapColumns.clear()

                    // Retrieve current map dimensions
                    val mapDimensions = this.simulation.map.dimensions

                    // Iterate over all (x,z) coordinate pairs within the map dimensions
                    for(ix in 0 until mapDimensions.width)
                    {
                        for(iz in 0 until mapDimensions.depth)
                        {
                            this.mapColumns.add(Position(ix, iz))
                        }
                    }

                    // Shufle columns in order to make snow spread appear organic
                    this.mapColumns.shuffle()

                    // Determine number of modifications per step
                    this.modificationsPerStep = max(1, (this.modificationsPerStepPercent * this.mapColumns.size).toInt())

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

                    // We also need to initialize the tick counter
                    this.modificationCounter = Optional.of(TickCounter(this.modificationPeriod))

                    // Randomly shuffle covering collection in order to make snow disappear "organically"
                    this.coverings.shuffle()

                    // Determine number of modifications per step
                    this.modificationsPerStep = max(1, (this.modificationsPerStepPercent * this.coverings.size).toInt())

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
        // Retrieve number of elapsed snow spawning steps
        val steps = this.modificationCounter.get().update(elapsedTicks)

        GlobalLogger.d("SnowSystem", "Spawning ${this.modificationsPerStep} snow tiles")

        // Perform steps
        for(i in 1 .. steps)
        {
            // If all columns have been covered in snow, switch states
            if(this.mapColumns.isEmpty())
            {
                this.currentState = SystemState.IdleWinter

                // No more work to do
                return
            }

            // Otherwise there are still columns left
            val columns = this.mapColumns.take(this.modificationsPerStep)

            // Spawn snow on those columns
            for(column in columns)
            {
                this.spawnSnowTile(column)
            }

            // Remove columns from list
            for(i in 1 .. columns.size)
                this.mapColumns.removeFirst()
        }
    }

    /**
     * Possibly perform thaw snow steps
     */
    private fun thawSnowStep(elapsedTicks: Int)
    {
        // Retrieve number of elapsed snow thawing steps
        val steps = this.modificationCounter.get().update(elapsedTicks)

        // Perform steps
        for(i in 1 .. steps)
        {
            // If all snow tiles have been removed
            if(this.coverings.isEmpty())
            {
                this.currentState = SystemState.Idle

                // No more work to do
                return
            }

            // Otherwise remove up to N snow coverings
            for(i in 1 .. min(this.coverings.size, this.modificationsPerStep))
                this.coverings.removeFirst()
        }
    }
}