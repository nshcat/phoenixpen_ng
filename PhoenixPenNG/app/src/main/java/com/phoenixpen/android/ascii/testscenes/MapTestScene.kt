package com.phoenixpen.android.ascii.testscenes

import android.util.Log
import com.phoenixpen.android.R
import com.phoenixpen.android.application.Application
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.*
import com.phoenixpen.android.data.MaterialManager
import com.phoenixpen.android.map.MapView
import com.phoenixpen.android.map.TestMapGenerator
import com.phoenixpen.android.utility.TickCounter

/**
 * A simple test scene
 */
class MapTestScene(application: Application, dimensions: ScreenDimensions): Scene(application, dimensions)
{
    val map: com.phoenixpen.android.map.Map

    val mapView: MapView

    val materialManager = MaterialManager()

    val fpsTickCounter = TickCounter(20)

    init
    {
        // Load materials
        this.materialManager.loadMaterials(this.application.context, R.raw.test)

        // Create map generator
        val generator = TestMapGenerator(this.materialManager)

        // Initialize map
        this.map = com.phoenixpen.android.map.Map.load(generator)

        // Initialize map view
        this.mapView = MapView(this.map, this.dimensions, Position(0, 0), 2)
    }

    override fun render(screen: Screen)
    {
        this.mapView.render(screen)
    }

    override fun update(elapsedTicks: Int)
    {
        this.mapView.update(elapsedTicks)

        if(this.fpsTickCounter.update(elapsedTicks) > 0)
            Log.d("MapTestScene", "Current FPS: ${this.application.fpsCounter.fps}")
    }
}