package com.phoenixpen.android.game.ascii

import android.util.Log
import com.phoenixpen.android.application.Application
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.game.map.MapView
import com.phoenixpen.android.game.simulation.Simulation
import com.phoenixpen.android.game.core.TickCounter
import com.phoenixpen.android.input.Direction
import com.phoenixpen.android.input.MapViewMoveEvent

/**
 * The game main scene, displaying the map and allowing interaction with the game.
 *
 * @property application The application currently in ownership of this scene
 * @property dimensions The dimensions of the screen, in glyphs
 */
class MainScene(application: Application, dimensions: ScreenDimensions): Scene(application, dimensions)
{
    /**
     * The main simulation state
     */
    val simulationState: Simulation = Simulation(this.application.context)

    /**
     * The map view, a scene component tasked with rendering the map to screen
     */
    val mapView: MapView = MapView(this.simulationState, this.dimensions, Position(0, 0), 2)

    /**
     * A tick counter used to sporadically report the current FPS value to the debug log
     */
    val fpsTickCounter = TickCounter(20)

    /**
     * Do additional initialization
     */
    init
    {
    }


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
        if(this.application.input.hasEvents())
        {
            Log.d("INPUT", "Main scene had events")

            // Consume all events
            val events = this.application.input.consumeEvents()

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

        // Update the FPS counter and decide whether the current value needs to be logged now
        if(this.fpsTickCounter.update(elapsedTicks) > 0)
            Log.d("MapTestScene", "Current FPS: ${this.application.fpsCounter.fps}")
    }
}