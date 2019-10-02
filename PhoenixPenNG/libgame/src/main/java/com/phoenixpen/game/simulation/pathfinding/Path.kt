package com.phoenixpen.game.simulation.pathfinding

import com.phoenixpen.game.ascii.Position3D
import java.util.*

/**
 * A class representing a path as a result of a pathfinding request. Includes a sequence of absolute
 * points that describe all grid points that the actor is supposed to move to when moving along
 * the path.
 *
 * @param points The points that make up this path
 */
class Path(points: Collection<Position3D>)
{
    /**
     * All points this path is made up from. It is immutable.
     */
    val points: List<Position3D> = LinkedList(points)

    /**
     * Check if the path is empty
     */
    fun isEmpty() = this.points.isEmpty()

    /**
     * Retrieve the beginning position of the path
     */
    fun start() = this.points.first()

    /**
     * Retrieve the end position of the path
     */
    fun destination() = this.points.last()

    /**
     * Create a new path walker based on this path
     */
    fun walker() = PathWalker(this)
}

/**
 * A class used to walk along a given path
 *
 * @property path The path to walk along
 */
class PathWalker(val path: Path)
{
    /**
     * Construction based on an empty path is not allowed
     */
    init
    {
        if(this.path.isEmpty())
            throw IllegalArgumentException("Can't construct PathWalker from empty path")
    }

    /**
     * The currently active path segment
     */
    private var currentIndex = 0

    /**
     * Check whether there are still path points available
     */
    fun hasNext() = this.currentIndex < this.path.points.size - 1

    /**
     * Retrieve current path point without advancing the path state
     */
    fun current(): Position3D
    {
        return this.path.points[this.currentIndex]
    }

    /**
     * Retrieve current path point and advance the path state
     */
    fun consume(): Position3D
    {
        if(!this.hasNext())
            throw IllegalStateException("Tried consume on exhausted path")

        val element = this.current()

        ++this.currentIndex

        return element
    }
}