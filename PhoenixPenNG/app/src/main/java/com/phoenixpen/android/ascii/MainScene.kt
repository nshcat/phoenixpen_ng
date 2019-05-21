package com.phoenixpen.android.ascii

import android.util.Log
import com.phoenixpen.android.R
import com.phoenixpen.android.application.Application
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.*
import com.phoenixpen.android.data.MaterialManager
import com.phoenixpen.android.map.MapView
import com.phoenixpen.android.map.TestMapGenerator
import com.phoenixpen.android.simulation.Simulation
import com.phoenixpen.android.utility.TickCounter

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
        // Update simulation state
        this.simulationState.update(elapsedTicks)

        // Update the map view
        this.mapView.update(elapsedTicks)

        // Update the FPS counter and decide whether the current value needs to be logged now
        if(this.fpsTickCounter.update(elapsedTicks) > 0)
            Log.d("MapTestScene", "Current FPS: ${this.application.fpsCounter.fps}")
    }
}