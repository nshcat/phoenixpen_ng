package com.phoenixpen.game.simulation


/**
 * An interface for processes that implemented transitions in the seasonal cycle of a tree.
 */
interface TreeTransition
{
    /**
     * Check whether this transition is completed
     *
     * @return Whether this transition is completed
     */
    fun isDone(): Boolean

    /**
     * Update tree transition based on given number of elapsed ticks
     *
     * @param elapsedTicks Number of simulation ticks elapsed since last update
     */
    fun update(elapsedTicks: Int)
}