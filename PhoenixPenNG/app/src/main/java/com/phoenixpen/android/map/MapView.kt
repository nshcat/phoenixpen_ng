package com.phoenixpen.android.map

import android.util.Log
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.Position
import com.phoenixpen.android.ascii.Position3D
import com.phoenixpen.android.ascii.SceneComponent
import com.phoenixpen.android.ascii.Screen

/**
 * Class that actually does the rendering of a portion of the map.
 *
 * @property map Reference to game map
 * @property dimensions Dimensions of the map view, in glyphs
 * @property topLeft Position in the game map that corresponds to the top left point of the view
 * @property height Current height that is displayed
 */
class MapView(val map: Map, val dimensions: ScreenDimensions, val topLeft: Position, var height: Int)
    : SceneComponent
{
    /**
     * Update scene based on given amount of elapsed ticks
     *
     * @param elapsedTicks Amount of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {

    }

    /**
     * Render component to screen
     *
     * @param screen Screen to render component to
     */
    override fun render(screen: Screen)
    {
        Log.d("MapView", "Drawing map view with dimensions ${this.dimensions.width}, ${this.dimensions.height}")

        for(ix in 0 until this.dimensions.width)
        {
            for(iz in 0 until this.dimensions.height)
            {
                // Calculate map position
                val mapPos = Position3D(ix + this.topLeft.x, this.height, iz + this.topLeft.y)

                // Only continue of its inside of the map bounds. If its not, us not drawing anything here
                // causes the screen to be black. TODO maybe different effect?
                if(this.map.isInBounds(mapPos))
                {
                    // Retrieve cell
                    val cell = this.map.cellAt(mapPos)

                    // Only draw if not transparent TODO this is not how depth should work.
                    if(!cell.isTransparent())
                    {
                        // Retrieve graphical representation of map cell
                        val tile = cell.tile()

                        // Draw to screen
                        screen.setTile(Position(ix, iz), tile)
                    }
                }
            }
        }
    }
}