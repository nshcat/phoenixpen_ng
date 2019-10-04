package com.phoenixpen.game.core

/**
 * A simple class that can be used as a stop watch to measure the execution time of a segment
 * of code.
 *
 * If the timer is constructed via its constructor, the method [tick] needs to be called in order to
 * start time measurements. To create a timer object that is immediately started, use the [tock] class
 * method.
 */
class Timer
{
    /**
     * Whether the timer has already been started, i.e. by the tick method or the tick class builder
     * function
     */
    var isStarted: Boolean = false
        private set

    /**
     * The time stamp at the beginning of the measurements
     */
    private var startTime: Long = 0

    /**
     * Begin measuring time with this timer.
     */
    fun tick()
    {
        if(this.isStarted)
            throw IllegalStateException("Can't call tick method on timer that has already been started")

        // Record current execution time
        this.startTime = System.currentTimeMillis()

        this.isStarted = true
    }

    /**
     * Stop measurement of time and return time elapsed in seconds.
     *
     * @return Elapsed time since last [tick] call, in seconds
     */
    fun tock(): Double
    {
        if(!this.isStarted)
            throw IllegalStateException("Can't call tock method on timer that hasn't been started yet")

        var elapsedTime = System.currentTimeMillis() - this.startTime.toDouble()
        elapsedTime /= 1000.0

        this.isStarted = false
        return elapsedTime
    }

    /**
     * Helper methods
     */
    companion object
    {
        /**
         * Create a timer object that is already set to being started
         */
        fun tick(): Timer = Timer().apply{ tick() }
    }
}