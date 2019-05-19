package com.phoenixpen.android.map

import android.util.Log
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.*
import com.phoenixpen.android.utility.TickCounter

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
     * Tick counter used to control height change
     */
    private val counter = TickCounter(10)

    /**
     * Update scene based on given amount of elapsed ticks
     *
     * @param elapsedTicks Amount of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        this.height = (this.height + this.counter.update(elapsedTicks)) % this.map.dimensions.height
    }

    /**
     * Render component to screen
     *
     * @param screen Screen to render component to
     */
    override fun render(screen: Screen)
    {
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

                    // If cell is solid, it might not be able to be drawn
                    if(cell.state == MapCellState.Solid)
                    {
                        // Only draw if there exists a ground or air cell adjacent to it
                        var found = false
                        for(dx in -1 .. 1)
                        {
                            for(dz in -1 .. 1)
                            {
                                val newPos = mapPos + Position3D(dx, 0, dz)

                                if(this.map.isInBounds(newPos) && this.map.cellAt(newPos).state == MapCellState.Ground)
                                {
                                    found = true
                                    break
                                }
                            }

                            if(found)
                                break
                        }

                        if(!found)
                            continue
                    }

                    // Only draw if not transparent TODO this is not how depth should work.
                    if(!cell.isTransparent())
                    {
                        // Retrieve graphical representation of map cell
                        val tile = cell.tile()

                        // Draw to screen
                        screen.setTile(Position(ix, iz), tile)
                    }
                    else
                    {

                        for(dy in this.height-1 downTo 0)
                        {
                            // Retrieve cell
                            val cell = this.map.cellAt(Position3D(ix, dy ,iz))

                            if(!cell.isTransparent())
                            {
                                val depth = this.height - dy
                                screen.setTile(Position(ix, iz), cell.tile())
                                screen.setDepth(Position(ix, iz), depth)

                                // TODO only drop shadows if depth is 1?

                                val directions = emptyShadowDirections()


                                val currentPos = Position3D(ix, dy+1, iz)

                                val testDirection = { dPos: Position3D, direction: ShadowDirection ->
                                    val newPos = currentPos + dPos

                                    if(this.map.isInBounds(newPos))
                                        if(!this.map.cellAt(newPos).isTransparent())
                                            directions.add(direction)
                                }

                                testDirection(Position3D(-1, 0, 0), ShadowDirection.West)
                                testDirection(Position3D(1, 0, 0), ShadowDirection.East)
                                testDirection(Position3D(0, 0, -1), ShadowDirection.North)
                                testDirection(Position3D(0, 0, 1), ShadowDirection.South)

                                screen.setShadows(Position(ix, iz), directions)

                                break
                            }
                        }

                    }
                }
            }
        }
    }
}