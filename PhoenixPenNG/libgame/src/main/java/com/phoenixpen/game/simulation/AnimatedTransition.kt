package com.phoenixpen.game.simulation

import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.core.applyIf
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * An animated transition where a number of game objects are modified over time rather than instantly.
 * This is used to implement various state transitions inside the simulation in a visually pleasing way.
 *
 * @param T Type of object that is processed by this animated transition
 *
 * @param objectCollection A collection of all objects to be processed by this animated transition
 * @property objectPercentage How many of the affected objects should be modified in each animation frame
 * @property animationSpeed The speed of the animation - number of ticks each frame should take
 * @param shuffleObjects Whether order of the given objects should be randomized before starting the animation
 */
abstract class AnimatedTransition<T>(
        objectCollection: Collection<T>,
        private val objectPercentage: Double = 0.05,
        private val animationSpeed: Int = 1,
        shuffleObjects: Boolean = true
): Transition
{
    /**
     * Tick counter used to implement the animation
     */
    private val animationCounter = TickCounter(this.animationSpeed)

    /**
     * Number of objectCollection affected each animation frame
     */
    private val objectsPerFrame: Int

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