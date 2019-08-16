package com.phoenixpen.game.simulation

import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.core.applyIf
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * A tree transition that applies change in a smooth animation.
 *
 * @property objectPercentage How many of the affected objectCollection should be modified in each animation frame
 * @property animationSpeed The speed of the animation -- number of ticks each frame should take
 * @param objectCollection A collection of all objectCollection to be processed by this animated transition
 * @param T Type of object that is processed by this animated transition
 * @param shuffleObjects Whether order of the given objects should be randomized before starting the animation
 */
abstract class AnimatedTreeTransition<T>(
        protected val objectPercentage: Double,
        protected val animationSpeed: Int,
        objectCollection: Collection<T>,
        shuffleObjects: Boolean = true
): TreeTransition
{
    /**
     * Tick counter used to implement the animation
     */
    protected val animationCounter = TickCounter(this.animationSpeed)

    /**
     * Number of objectCollection affected each animation frame
     */
    protected val objectsPerFrame: Int

    /**
     * List of all affected objects
     */
    protected val objects = LinkedList<T>()
            .apply { addAll(objectCollection) }
            .applyIf(shuffleObjects) { shuffle() }

    /**
     * Whether this transition is active, i.e. not finished
     */
    protected var isActive: Boolean = true

    /**
     * Initialize internal state
     */
    init
    {
        // Calculate how many objectCollection will be affected per animation frame
        this.objectsPerFrame = max(1, (this.objectPercentage * this.objects.size).toInt())
    }

    /**
     * Check if this animated transition is done.
     *
     * @return Returns a value based on [isActive]
     */
    override fun isDone(): Boolean
    {
        return !this.isActive
    }

    /**
     * Update animated transition.
     *
     * @param elapsedTicks Number of ticks elapsed since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // If we are already finished with the animation we dont expect any more updated
        if(!this.isActive)
            throw IllegalStateException("Can't perform update on finished animated transition")

        // Perform update on the animation tick counter
        val events = this.animationCounter.update(elapsedTicks)

        // Calculate the number of objects to modify in this frame
        val modifyCount = min(this.objects.size, events * this.objectsPerFrame)

        // Retrieve first [modifyCount] objects from our buffer
        val toModify = this.objects.take(modifyCount)

        // Perform operations on them
        this.processObjects(toModify)

        // Remove from buffer
        for(i in 1 .. modifyCount)
            this.objects.removeFirst()

        // Check if we are done with the animation
        if(this.objects.size <= 0)
            this.isActive = false
    }

    /**
     * Function called every time the transition has made progress and new objects to be modified
     * have been determined. Sub classes must do their work in this function. The objects will automatically
     * be removed from the internal object storage [objects].
     *
     * @param affectedObjects Objects to be modified in this transition frame
     */
    protected abstract fun processObjects(affectedObjects: Collection<T>)
}