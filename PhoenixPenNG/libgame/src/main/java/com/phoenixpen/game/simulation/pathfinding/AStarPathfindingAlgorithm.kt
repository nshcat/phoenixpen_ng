package com.phoenixpen.game.simulation.pathfinding

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.core.Timer
import com.phoenixpen.game.data.PathingType
import com.phoenixpen.game.map.Map

/**
 * A class that implements path finding using the A* algorithm
 *
 * @param map Map to perform path finding on
 */
class AStarPathfindingAlgorithm(map: Map): PathfindingAlgorithm(map)
{
    override fun findPath(start: Position3D, end: Position3D, pathingType: PathingType): PathfindingResult
    {
        val timer = Timer.tick()

        return PathfindingResult.failure(timer.tock())
    }
}