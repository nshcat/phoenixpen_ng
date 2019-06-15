package com.phoenixpen.game.ascii

import com.phoenixpen.game.map.MapView
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.input.Direction
import com.phoenixpen.game.input.InputProvider
import com.phoenixpen.game.input.MapViewMoveEvent
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.logging.Logger
import com.phoenixpen.game.resources.ResourceProvider

/**
 * The game main scene, displaying the map and allowing interaction with the game.
 *
 * @property resources The resource manager to retrieve game data from
 * @property input The input manager providing input events
 * @property logger The logger instance to use
 * @property dimensions The screen dimensions
 */
class MainScene(
        resources: ResourceProvider,
        input: InputProvider,
        logger: Logger,
        dimensions: ScreenDimensions
): Scene(resources, input, logger, dimensions)
{
    /**
     * Initialize global logger
     */
    init
    {
        GlobalLogger.setLogger(logger)
    }

    /**
     * The main simulation state
     */
    val simulationState: Simulation = Simulation(this.resources)

    /**
     * The map view, a scene component tasked with rendering the map to screen
     */
    val mapView: MapView = MapView(this.simulationState, this.dimensions, Position(0, 0), 2)

    /**
     * Render main scene
     */
    override fun render(screen: Screen)
    {
        // Render the map and all its structures, entities, etc..
        this.mapView.render(screen)
    }

    /**
     * Update main scene logic
     */
    override fun update(elapsedTicks: Int)
    {
        // Check for input
        if(this.input.hasEvents())
        {
            // Consume all events
            val events = this.input.consumeEvents()

            for(event in events)
            {
                when(event)
                {
                    is MapViewMoveEvent ->
                    {
                        when(event.direction)
                        {
                            Direction.North -> this.mapView.move(Position(0, -4))
                            Direction.South -> this.mapView.move(Position(0, 4))
                            Direction.West -> this.mapView.move(Position(-4, 0))
                            Direction.East -> this.mapView.move(Position(4, 0))
                            Direction.Up -> this.mapView.moveUp(1)
                            Direction.Down -> this.mapView.moveUp(-1)
                        }
                    }
                }

            }
        }

        // Update simulation state
        this.simulationState.update(elapsedTicks)

        // Update the map view
        this.mapView.update(elapsedTicks)
    }
}