package com.phoenixpen.game.math

import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * A restricted implementation of a queue that pops of random elements.
 * Since this class is only used in very special algorithms in this code base,
 * a full implementation of the Queue<T> interface is not provided. This means that
 * actions like iterating are not supported.
 *
 * @param T Type of elements stored in this random queue
 */
class RandomQueue<T>
{
    /**
     * The internal data structure used to hold the elements
     */
    private val internalBuffer = ArrayList<T>()

    /**
     * Add given element to the [RandomQueue].
     *
     * @param value Value to add
     */
    fun push(value: T)
    {
        this.internalBuffer.add(value)
    }

    /**
     * Retrieve random element from this queue and remove it.
     *
     * @return Random element from queue, if not empty.
     */
    fun pop(): T
    {
        if(this.isEmpty())
            throw IllegalStateException("RandomQueue::pop called on empty RandomQueue")

        // Select a random index
        val index = ThreadLocalRandom.current().nextInt(0, this.internalBuffer.size)

        // Retrieve the element
        val elem = this.internalBuffer[index]

        // Remove it
        this.internalBuffer.removeAt(index)

        return elem
    }

    /**
     * Check whether this random queue is empty.
     *
     * @return Flag indicating whether this queue is empty.
     */
    fun isEmpty(): Boolean
    {
        return this.internalBuffer.isEmpty()
    }

    /**
     * Check whether this random queue is not empty.
     *
     * @return Flag indicating whether this queue is not empty.
     */
    fun isNotEmpty(): Boolean
    {
        return this.internalBuffer.isNotEmpty()
    }
}