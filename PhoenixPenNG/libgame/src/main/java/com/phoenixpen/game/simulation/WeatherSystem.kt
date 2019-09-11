package com.phoenixpen.game.simulation

import com.phoenixpen.game.data.Covering
import java.util.*

/**
 * Class managing weather phenomena like rain, snow and wind.
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
     * An internal counter used to store how many ticks the current weather phenomenon should still
     * last. This is initialized with the duration at the start of each phenomenon.
     */
    private var durationLeft = 0

    /**
     * The current state of this system
     */
    private var currentState = SystemState.Idle

    /**
     * Update weather system based on given number of elapsed ticks
     *
     * @param elapsedTicks Number of ticks elapsed since last update
     */
    override fun update(elapsedTicks: Int)
    {

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