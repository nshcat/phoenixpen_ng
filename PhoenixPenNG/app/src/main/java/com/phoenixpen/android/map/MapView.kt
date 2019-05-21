package com.phoenixpen.android.map

import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.*
import com.phoenixpen.android.simulation.Simulation
import com.phoenixpen.android.utility.TickCounter

/**
 * Class that actually does the rendering of a portion of the map.
 *
 * @property simulation Current simulation state
 * @property dimensions Dimensions of the map view, in glyphs
 * @property topLeft Position in the game map that corresponds to the top left point of the view
 * @property height Current height that is displayed
 */
class MapView(val simulation: Simulation, val dimensions: ScreenDimensions, val topLeft: Position, var height: Int)
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
        this.height = (this.height + this.counter.update(elapsedTicks)) % this.simulation.map.dimensions.height
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
                if(this.simulation.map.isInBounds(mapPos))
                {
                    // Retrieve cell
                    val cell = this.simulation.map.cellAt(mapPos)

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

                                if(this.simulation.map.isInBounds(newPos) && this.simulation.map.cellAt(newPos).state == MapCellState.Ground)
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

                    // Only draw if not transparent
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
                            val cell = this.simulation.map.cellAt(Position3D(ix, dy ,iz))

                            if(!cell.isTransparent())
                            {
                                val depth = this.height - dy
                                screen.setTile(Position(ix, iz), cell.tile())
                                screen.setDepth(Position(ix, iz), depth)

                                val directions = emptyShadowDirections()

                                val currentPos = Position3D(ix, dy+1, iz)

                                // Test all main directions (W, S, E, N). They apply full-width drop
                                // shadows
                                val testMainDirection = { dPos: Position3D, direction: ShadowDirection ->
                                    val newPos = currentPos + dPos

                                    if(this.simulation.map.isInBounds(newPos))
                                        if(!this.simulation.map.cellAt(newPos).isTransparent())
                                            directions.add(direction)
                                }

                                testMainDirection(Position3D(-1, 0, 0), ShadowDirection.West)
                                testMainDirection(Position3D(1, 0, 0), ShadowDirection.East)
                                testMainDirection(Position3D(0, 0, -1), ShadowDirection.North)
                                testMainDirection(Position3D(0, 0, 1), ShadowDirection.South)

                                // Test all diagonal directions (SW, SE, NW, NE). They apply a little corner
                                // drop shadow, but only if it wouldnt be overlayed with a full drop shadow
                                val testDiagonalDirection = { dPos: Position3D, direction: ShadowDirection, blockingDirections: ShadowDirections ->
                                    val newPos = currentPos + dPos

                                    if(this.simulation.map.isInBounds(newPos))
                                    {
                                        if (!this.simulation.map.cellAt(newPos).isTransparent())
                                        {
                                            // The cell is candidate for applying the corner shadow.
                                            // Check that none of the blocking shadows are already
                                            // applied.
                                            if(!directions.any({ x -> blockingDirections.has(x) }))
                                                directions.add(direction)
                                        }
                                    }
                                }

                                testDiagonalDirection(Position3D(-1, 0, -1), ShadowDirection.TopLeft, ShadowDirections.of(ShadowDirection.North, ShadowDirection.West))
                                testDiagonalDirection(Position3D(1, 0, -1), ShadowDirection.TopRight, ShadowDirections.of(ShadowDirection.North, ShadowDirection.East))
                                testDiagonalDirection(Position3D(1, 0, 1), ShadowDirection.BottomRight, ShadowDirections.of(ShadowDirection.South, ShadowDirection.East))
                                testDiagonalDirection(Position3D(-1, 0, 1), ShadowDirection.BottomLeft, ShadowDirections.of(ShadowDirection.South, ShadowDirection.West))

                                // Apply all accumulated shadows
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