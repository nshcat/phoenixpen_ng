package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.MapCellState
import com.phoenixpen.game.math.NormalDistribution
import com.phoenixpen.game.math.PoissonDiskSampler
import java.util.*

/**
 * Container used to store all 2D positions in a single frame of a rain animation
 */
typealias RainFrame = List<Position>

/**
 * Class managing weather phenomena like rain, snow and wind.
 *
 * TODO: different intensities of rain
 *
 * @param simulation The current simulation instance
 */
class WeatherSystem(simulation: Simulation): System(simulation), CoveringHolder
{
    /**
     * Enumeration detailing the different states the weather system can be in
     */
    enum class SystemState
    {
        /**
         * Not displaying any weather effect
         */
        Idle,

        /**
         * Currently displaying rain
         */
        Rain
    }

    /**
     * Collection of all coverings that are used to implement the rain splash effect
     */
    private val rainCoverings = LinkedList<Covering>()

    /**
     * How many rain frames to pregenerate
     */
    private val numRainFrames = 100

    /**
     * Pre-generated rain frames
     */
    private val rainFrames = ArrayList<RainFrame>()

    /**
     * The index of the rain frame that should be displayed next
     */
    private var nextRainFrame = 0

    /**
     * An internal counter used to store how many ticks the current weather phenomenon should still
     * last. This is initialized with the duration at the start of each phenomenon.
     */
    private var durationLeft = 0

    /**
     * How many ticks a rain covering will last
     */
    private var rainCoveringLifespan = 2

    /**
     * The current state of this system
     */
    private var currentState = SystemState.Idle

    /**
     * Hash map used to cache the determined rain drop collision height given a 2D position
     */
    private val heightCache = HashMap<Position, Int>()

    /**
     * The covering type to use for rain drops
     */
    private val rainCoveringType = this.simulation.coveringManager.lookupCovering("rain_drop")

    /**
     * Initialize rain frames
     */
    init
    {
        // A poisson disk sampler is used in order to create a nice rain effect
        val sampler = PoissonDiskSampler(
                this.simulation.map.dimensions.wh(),
                18.5,
                minDistanceMod = NormalDistribution(
                        mean = 1.0,
                        variance = 2.0,
                        restricted = true,
                        min = -2.0,
                        max = 3.5
                )
        )

        // Create all rain frames
        for(i in 1 .. this.numRainFrames)
        {
            this.rainFrames.add(sampler.sample().toList())
        }
    }

    /**
     * Update weather system based on given number of elapsed ticks
     *
     * @param elapsedTicks Number of ticks elapsed since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // Remove all coverings that have exceeded their life span
        this.killCoverings()

        // Update all rain coverings
        this.rainCoverings.forEach { x -> x.update(elapsedTicks) }

        // Perform state-specific system actions
        when(this.currentState)
        {
            // In idle state, check if we need to start applying rain
            SystemState.Idle ->
            {
                // Retrieve the current season
                val season = this.simulation.seasonSystem.currentSeason

                // Retrieve weather data for current season
                val weatherData = this.simulation.biomeConfiguration.weather.dataFor(season)

                // Retrieve the rain distribution
                val rainDistrib = weatherData.rainDistribution

                // Check if we are supposed to start a rain effect
                if(rainDistrib.shouldGenerate())
                {
                    // Pick a length
                    this.durationLeft = rainDistrib.nextValue().toInt()

                    // Reset rain frame rendering to the beginning
                    this.nextRainFrame = 0

                    // Switch to rain mode
                    this.currentState = SystemState.Rain

                    GlobalLogger.d("WeatherSystem", "It has started raining with duration ${this.durationLeft}")
                }
            }
            SystemState.Rain ->
            {
                // First, check if are done with raining
                if(this.durationLeft <= 0)
                {
                    this.currentState = SystemState.Idle
                    GlobalLogger.d("WeatherSystem", "It has stopped raining")
                }
                else
                {
                    // Otherwise progress
                    this.spawnRainFrame()

                    // Decrement duration
                    this.durationLeft -= 1
                }
            }
        }
    }

    /**
     * Spawn the next rain frame
     */
    private fun spawnRainFrame()
    {
        // Retrieve next rain frame
        val frame = this.rainFrames[this.nextRainFrame]

        // Spawn rain coverings at each of the contained positions
        for(position in frame)
        {
            val map = this.simulation.map
            val mapHeight = map.dimensions.height

            // Check if we have cached the 3D position for this 2D position
            /*if(this.heightCache.containsKey(position))
            {
                // Build position and check that there really is something at that position.
                // This is important since, for example, a structure might have become invisible or
                // removed.
                val pos = Position3D(position.x, this.heightCache.getValue(position), position.y)

                val cell = map.cellAt(pos)

                if(cell.state == MapCellState.Ground || map.getStructureAtExact(pos, true).isPresent)
                {
                    this.spawnRainCovering(pos)
                    break
                }
                else
                {
                    // The entry is not up to date anymore
                    this.heightCache.remove(position)

                    // This will fall through and cause the height to be recalculated.
                }
            }*/

            // "Raycast" from the sky downwards until something was hit
            for(iy in mapHeight-1 downTo 0)
            {
                // Current position
                val pos = Position3D(position.x, iy, position.y)

                // Retrieve map cell at this position
                val cell = map.cellAt(pos)

                // If its solid, cancel search for this column
                if(cell.state == MapCellState.Solid)
                    break

                // If it is ground or we found a structure create rain drop and cache height
                if(cell.state == MapCellState.Ground
                        || map.getStructureAtExact(pos, true).isPresent)
                {
                    this.spawnRainCovering(pos)

                    this.heightCache.put(position, iy)

                    break
                }
            }
        }

        // Update next rain frame counter
        this.nextRainFrame = (this.nextRainFrame + 1) % this.numRainFrames
    }

    /**
     * Actuall spawn a rain covering at given position.
     *
     * @param position Position to spawn the rain covering at
     */
    private fun spawnRainCovering(position: Position3D)
    {
        this.rainCoverings.add(Covering.create(this.rainCoveringType, position))
    }

    /**
     * Kill all rain coverings that have exceeded the maximum life span
     */
    private fun killCoverings()
    {
        // Since new coverings are always appended to the end of the covering list,
        // it is enough to only remove coverings until the first one is found that has not
        // yet exceeded the maximum life span
        while(this.rainCoverings.isNotEmpty()
                && this.rainCoverings.first.lifetime >= this.rainCoveringLifespan)
            this.rainCoverings.pop()
    }

    /**
     * Retrieve all coverings managed by this covering holder.
     *
     * @return Collection of all coverings managed by this system.
     */
    override fun coverings(): Collection<Covering>
    {
        return this.rainCoverings
    }
}