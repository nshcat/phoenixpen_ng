package com.phoenixpen.game.simulation

import com.phoenixpen.game.core.TickCounter
import kotlin.math.max
import kotlin.math.min

/**
 * A simple helper class designed to support the creation of an animation where a number of game
 * objects are modified over time rather than instantly.
 *
 * @property modificationPercentage How many game objects to modify per frame, in percent
 * @property frameDuration Duration of a single animation frame, in ticks
 */
class ModificationAnimation(val modificationPercentage: Double, val frameDuration: Int)
{
    /**
     * Whether there currently is an animation active
     */
    var isActive: Boolean = false
        private  set

    /**
     * The tick counter used
     */
    private var modificationCounter = TickCounter.empty()

    /**
     * How many game objects should be modified, per animation frame.
     * This will be calculated based on the percentage.
     */
    private var modificationsPerFrame = -1

    /**
     * The number of game objects left to modify
     */
    private var amountLeft = 0

    /**
     * Update animation. Returns number of objects to modify.
     *
     * @param elapsedTicks Number of elapsed ticks
     * @return Number of objects to modify
     */
    fun update(elapsedTicks: Int): Int
    {
        if(!this.isActive)
            throw IllegalStateException("Can't update inactive modification animation")

        // Update tick counter
        val events = this.modificationCounter.update(elapsedTicks)

        // Calculate how many objects to modify
        val toModify = min(this.amountLeft, events * this.modificationsPerFrame)

        // Adjust amount left
        this.amountLeft -= toModify

        // Check if we are done
        if(this.amountLeft <= 0)
            this.isActive = false

        return toModify
    }

    /**
     * Reset the animation based on given amount of game objects to modify in total
     *
     * @param amount Number of game objects to modify in total over the whole animation
     */
    fun reset(amount: Int)
    {
        // Calculate modifications per frame
        this.modificationsPerFrame =  max(1, (this.modificationPercentage * amount).toInt())

        // Set up tick counter
        this.modificationCounter = TickCounter(this.frameDuration)

        // Save total number of items for later use
        this.amountLeft = amount

        // Switch to active
        this.isActive = true
    }
}