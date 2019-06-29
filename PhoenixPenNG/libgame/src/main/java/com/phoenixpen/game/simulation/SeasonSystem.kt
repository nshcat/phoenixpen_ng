package com.phoenixpen.game.simulation

import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.core.Updateable
import com.phoenixpen.game.core.format
import com.phoenixpen.game.data.SeasonConfiguration
import com.phoenixpen.game.logging.GlobalLogger

/**
 * A system class managing the passing of seasons in the game simulation
 *
 * @property simulation The current simulation instance
 */
class SeasonSystem(val simulation: Simulation): Updateable
{
    /**
     * The main tick counter used to track the progress of the currently active season
     */
    protected var seasonCounter: TickCounter

    /**
     * The currently active season
     */
    var currentSeason: Season
        protected set

    /**
     * A counter for logging purposes.
     */
    private val logCounter = TickCounter(20)

    /**
     * Initialize season system to spring
     */
    init
    {
        // Initialize tick counter with spring duration
        this.seasonCounter = TickCounter(this.seasonConfig().springDuration)

        // The initial season is spring
        this.currentSeason = Season.Spring
    }

    /**
     * Retrieve season configuration instance from simulation. This is a helper function.
     *
     * @return The current season configuration object
     */
    private fun seasonConfig(): SeasonConfiguration = this.simulation.seasonConfiguration

    /**
     * The progress in the currently active season, in percent
     *
     * @return Season progress, in [0,1]
     */
    fun seasonProgress(): Double = this.seasonCounter.percentage()

    /**
     * Perform season simulation for given number of ticks
     */
    override fun update(elapsedTicks: Int)
    {
        // Update season counter. If it finished at least one period, we need to advance the currently
        // active season
        if(this.seasonCounter.update(elapsedTicks) > 0)
            this.advanceSeason()

        if(this.logCounter.update(elapsedTicks) > 0)
            GlobalLogger.d("SeasonSystem", "Current season progress: ${(this.seasonProgress()*100).format(1)}%")
    }

    /**
     * Advance the current season.
     */
    private fun advanceSeason()
    {
        // Retrieve new season
        this.currentSeason = this.currentSeason.nextSeason()

        // Reset tick counter with new season duration
        this.seasonCounter = TickCounter(this.seasonConfig().durationOf(this.currentSeason))

        // Log season change
        GlobalLogger.d("SeasonSystem", "Season changed to ${this.currentSeason}")
    }
}