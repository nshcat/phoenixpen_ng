package com.phoenixpen.android.game.core

/**
 * A class used to count ticks and fire an event every fixed number of ticks (called the period)
 *
 * @property period The tick period of this counter. Event will be fired every [period] ticks.
 */
class TickCounter(val period: Int)
{
    /**
     * The current tick count
     */
    private var counter = 0

    /**
     * How many events were fired since creation of this tick counter instance
     */
    var totalPeriods = 0
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
}