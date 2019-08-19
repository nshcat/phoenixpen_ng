package com.phoenixpen.game.simulation


/**
 * An interface for processes that describes transitions in the game simulation.
 * This is a very abstract concept - what exactly a transition is will be defined by
 * subclasses.
 */
interface Transition
{
    /**
     * Check whether this transition is completed
     *
     * @return Whether this transition is completed
     */
    fun isDone(): Boolean

    /**
     * Update transition based on given number of elapsed ticks
     *
     * @param elapsedTicks Number of simulation ticks elapsed since last update
     */
    fun update(elapsedTicks: Int)
}