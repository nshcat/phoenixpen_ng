package com.phoenixpen.game.core

/**
 * A class used to count ticks and fire an event every fixed number of ticks (called the period)
 *
 * @property period The tick period of this counter. Event will be fired every [period] ticks.
 * @param initial The initial number of elapsed ticks stored in [totalPeriods]. This is used for animation
 * offset.
 */
class TickCounter(val period: Int, initial: Int = 0)
{
    /**
     * The current tick count
     */
    private var counter = 0

    /**
     * How many events were fired since creation of this tick counter instance
     */
    var totalPeriods = initial
        private set

    /**
     * Update tick counter with given number of elapsed ticks. Returns number of fired events in this
     * update.
     *
     * @param elapsedTicks Number of elapsed ticks since last update
     * @return Number of events that were fired in during this update
     */
    fun update(elapsedTicks: Int): Int
    {
        val tickCount = this.counter + elapsedTicks
        val firedEvents = tickCount / this.period
        this.totalPeriods += firedEvents
        this.counter = tickCount % this.period

        return firedEvents
    }

    /**
     * Retrieve the percentage progress of the tick counter towards the next full period.
     * Example: If the period is 100 and the current counter value is 50, this will return 0.5.
     *
     * @return Current percentage progress of the tick counter
     */
    fun percentage(): Double
    {
        // If the period is zero, this will cause a divide by zero exception.
        if(this.isEmpty())
            throw IllegalStateException("TickCounter::percentage is only defined for non-empty tick counters")

        return this.counter.toDouble() / this.period.toDouble()
    }

    /**
     * Check whether this tick counter is empty, which means it does nothing. This is the case
     * if the period is 0.
     */
    fun isEmpty(): Boolean = this.period == 0

    /**
     * Check if tick counter is not empty. Negated version of [isEmpty]
     */
    fun isNotEmpty(): Boolean = !this.isEmpty()

    companion object
    {
        /**
         * An empty tick counter.
         */
        fun empty(): TickCounter = TickCounter(0)
    }
}