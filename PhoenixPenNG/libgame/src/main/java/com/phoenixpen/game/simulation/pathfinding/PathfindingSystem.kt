package com.phoenixpen.game.simulation.pathfinding

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.data.PathBlockType
import com.phoenixpen.game.data.PathingType
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.simulation.System

/**
 * The default path finding algorithm to use
 */
typealias DefaultPathfindingAlgorithm = NullPathfindingAlgorithm

/**
 * A simulation system that handles all pathfinding requests from game object in the simulated
 * world.
 *
 * @param simulation The currently active simulation state
 */
class PathfindingSystem(simulation: Simulation): System(simulation)
{
    /**
     * The path finding algorithm to use for path calculation
     */
    private val pathfinder = DefaultPathfindingAlgorithm(simulation.map)

    /**
     * The pathfinding system does not have any state, and thus does not require any updating.
     */
    override fun update(elapsedTicks: Int) = Unit

    /**
     * Calculate a path from given start position to given end position.
     *
     * @param start Start position
     * @param end End position
     * @param pathingType Pathing type of the actor to calculate path for
     */
    fun calculatePath(start: Position3D, end: Position3D, pathingType: PathingType): PathfindingResult
    {
        // This method exists since later on, there might be an asynchronous implementation.
        return this.pathfinder.findPath(start, end, pathingType)
    }
}