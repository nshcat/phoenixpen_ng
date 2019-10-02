package com.phoenixpen.game.simulation.pathfinding

import java.util.*

/**
 * A class used to store the result of a pathfinding operation
 *
 * @property path The pathfinding result, if available
 * @property elapsedTime The time it took to do the pathfinding operation, no matter if succeeded or not.
 */
class PathfindingResult(val path: Optional<Path>, val elapsedTime: Double)
{
    /**
     * Check whether the pathfinding operation yielded a result
     */
    fun pathExists() = this.path.isPresent

    /**
     * Retrieve the pathfinding result, if possible.
     *
     * @return The determined path, if present.
     */
    fun path(): Path
    {
        if(!this.pathExists())
            throw IllegalStateException("Pathfinding operation did not yield a result")

        return this.path.get()
    }

    /**
     * Utility functions
     */
    companion object
    {
        /**
         * Create path finding result for successful operation
         */
        fun success(path: Path, elapsedTime: Double) = PathfindingResult(Optional.of(path), elapsedTime)

        /**
         * Create path finding result for failed operation
         */
        fun failure(elapsedTime: Double) = PathfindingResult(Optional.empty(), elapsedTime)
    }
}