package com.phoenixpen.android.ascii.testscenes

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.*
import com.phoenixpen.android.data.MaterialManager
import com.phoenixpen.android.map.MapView
import com.phoenixpen.android.map.TestMapGenerator

/**
 * A simple test scene
 */
class MapTestScene(ctx: Context, dimensions: ScreenDimensions): Scene(ctx, dimensions)
{
    val map: com.phoenixpen.android.map.Map

    val mapView: MapView

    val materialManager = MaterialManager()

    init
    {
        // Load materials
        this.materialManager.loadMaterials(this.context, R.raw.test)

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

    override fun update(elapsedTicks: Long)
    {
        // Do nothing
    }
}