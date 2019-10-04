package com.phoenixpen.game.simulation.pathfinding

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.data.PathingType
import com.phoenixpen.game.map.Map

/**
 * An abstract base class for path finding algorithms, following the strategy pattern.
 *
 * @property map The map to perform path finding on.
 */
abstract class PathfindingAlgorithm(val map: Map)
{
    /**
     * Perform path finding from given start to end position based on given actor pathing type
     *
     * @param start Start position
     * @param end End position
     * @param pathingType Actor pathing type
     * @return Result of the path finding operation
     */
    abstract fun findPath(start: Position3D, end: Position3D, pathingType: PathingType): PathfindingResult
}

/**
 * Path finding algorithm that always fails to determine a path.
 */
class NullPathfindingAlgorithm(map: Map): PathfindingAlgorithm(map)
{
    override fun findPath(start: Position3D, end: Position3D, pathingType: PathingType): PathfindingResult
    {
        return PathfindingResult.failure(0.0)
    }
}