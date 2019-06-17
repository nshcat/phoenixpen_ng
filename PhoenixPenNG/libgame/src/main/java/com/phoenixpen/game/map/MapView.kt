package com.phoenixpen.game.map

import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.game.ascii.*
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.data.CoveringDrawMode
import com.phoenixpen.game.data.Structure
import java.lang.Integer.min
import java.util.*
import kotlin.math.max

/**
 * Class that actually does the rendering of a portion of the map.
 *
 * @property simulation Current simulation state
 * @property dimensions Dimensions of the map view, in glyphs
 * @property topLeft Position in the game map that corresponds to the top left point of the view
 * @property height Current height that is displayed
 */
class MapView(val simulation: Simulation, val dimensions: ScreenDimensions, var topLeft: Position, var height: Int)
    : SceneComponent
{
    /**
     * Tick counter used to control height change
     */
    private val counter = TickCounter(20)

    /**
     * Update scene based on given amount of elapsed ticks
     *
     * @param elapsedTicks Amount of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        this.counter.update(elapsedTicks)
        //this.height = 3

        //this.height = 2 //+ (this.counter.totalPeriods % 6)
    }

    /**
     * Tries to retrieve a structure at a given map position and below. This will disregard any
     * map cells, so calling function needs to make sure that the returned structure is actually visible
     * and not occluded by terrain. If multiple structures are present at a given position in the map,
     * the one first encountered will be selected. This is mostly influenced by the order in which the structure
     * holders are registered.
     *
     * @param mapPosition Position to look at, and below
     * @return Structure reference if found, otherwise empty
     */
    private fun retrieveStructureAt(mapPosition: Position3D): Optional<Structure>
    {
        // Try out all cells in downward direction, starting with the given position height
        for(iy in mapPosition.y downTo 0)
        {
            // Try to retrieve structures at this position
            val structures = this.simulation.map.getStructuresAtExact(Position3D(mapPosition.x, iy, mapPosition.z))

            // If we found any structures, return the first one found.
            if(structures.isPresent && structures.get().isNotEmpty())
                return Optional.of(structures.get().first())
        }

        // No structure was found
        return Optional.empty()
    }

    /**
     * Try to find a map cell to draw in the given column. Returns empty optional if such a map
     * cell could not be found
     */
    private fun retrieveDrawableCellAt(mapPosition: Position3D): Optional<Pair<MapCell, Position3D>>
    {
        // Retrieve cell
        val cell = this.simulation.map.cellAt(mapPosition)

        // If cell is solid, it might not be able to be drawn
        if(cell.state == MapCellState.Solid)
        {
            // Only draw if there exists a ground or air cell adjacent to it
            for(dx in -1 .. 1)
            {
                for(dz in -1 .. 1)
                {
                    val newPos = mapPosition + Position3D(dx, 0, dz)

                    if(this.simulation.map.isInBounds(newPos) && this.simulation.map.cellAt(newPos).state == MapCellState.Ground)
                    {
                        // We did find an adjacent ground cell. This means the solid cell can be drawn.
                        return Optional.of(Pair(cell, mapPosition))
                    }
                }
            }

            // This solid cell should not be drawn.
            return Optional.empty()
        }

        // Only draw if not transparent
        if(!cell.isTransparent())
        {
            // The cell will be drawn.
            return Optional.of(Pair(cell, mapPosition))
        }
        else
        {
            // This cell will not be drawn. Search downwards to find a cell to render instead, with proper depth fog
            // applied.
            for(dy in this.height-1 downTo 0)
            {
                // Retrieve cell
                val cell = this.simulation.map.cellAt(Position3D(mapPosition.x, dy, mapPosition.z))

                // Is it drawable?
                if(!cell.isTransparent())
                {
                    val currentPos = Position3D(mapPosition.x, dy, mapPosition.z)

                    return Optional.of(Pair(cell, currentPos))
                }
            }

            // We did not find a cell that is drawable and underneath the requested position
            return Optional.empty()
        }
    }

    /**
     * Move the map view by the given delta position
     *
     * @param delta Delta position to move the view by
     */
    fun move(delta: Position)
    {
        this.topLeft += delta
    }

    /**
     * Move the map view height up by given delta value.
     *
     * @param delta Delta value to change map view height by
     */
    fun moveUp(delta: Int)
    {
        this.height = max(0, this.height + delta)
    }

    /**
     * Direction vectors used in the iterations of the drop shadow calculation algorithm
     */
    val directionVectors = listOf(
            Position3D(-1, 0, 0),
            Position3D(1, 0, 0),
            Position3D(0, 0, -1),
            Position3D(0, 0, 1),
            Position3D(-1, 0, -1),
            Position3D(1, 0, -1),
            Position3D(1, 0, 1),
            Position3D(-1, 0, 1)
    )

    /**
     * Drop shadow values for the iterations in the drop shadow calculation algorithm
     */
    val directionValues = listOf(
            ShadowDirection.West,
            ShadowDirection.East,
            ShadowDirection.North,
            ShadowDirection.South,
            ShadowDirection.TopLeft,
            ShadowDirection.TopRight,
            ShadowDirection.BottomRight,
            ShadowDirection.BottomLeft
    )

    /**
     * Shadow directions that block a certain shadow direction from being applied. This only applies
     * to the diagonal drop shadows.
     */
    private val blockingDirections = listOf(
            emptyShadowDirections(),
            emptyShadowDirections(),
            emptyShadowDirections(),
            emptyShadowDirections(),
            ShadowDirections.of(ShadowDirection.North, ShadowDirection.West),
            ShadowDirections.of(ShadowDirection.North, ShadowDirection.East),
            ShadowDirections.of(ShadowDirection.South, ShadowDirection.East),
            ShadowDirections.of(ShadowDirection.South, ShadowDirection.West)
    )

    /**
     * Determine the set of drop shadows that need to be applied to a cell at given position
     */
    private fun calculateShadowsAt(position: Position3D): ShadowDirections
    {
        // We check one cell above the cell in question
        val currentPos = Position3D(position.x, position.y + 1, position.z)

        // Set to store directions in
        val directions = emptyShadowDirections.apply { clear() }

        for(dy in (position.y + 1) .. min(position.y + 2, height)/*height*/)
        {
            val pos = Position3D(position.x, dy, position.z)

            for(i in 0 until directionVectors.size)
            {
                val dir = directionVectors[i]
                val newPos = Position3D(pos.x + dir.x, dy, pos.z + dir.z)

                if (this.simulation.map.isInBounds(newPos))
                    if (!this.simulation.map.cellAt(newPos).isTransparent() || this.simulation.map.getStructureAtExact(newPos).isPresent)
                        if(!directions.any{ x -> blockingDirections[i].has(x) })
                            directions.add(directionValues[i])
            }

        }

        return directions
    }

    /**
     * Draw a map cell. This will apply depth value and drop shadows according to relative depth to
     * display height.
     *
     * @param screen Screen to draw to
     * @param cell Cell to draw
     * @param position Position of said cell
     */
    private fun drawCell(screen: Screen, cell: MapCell, position: Position3D)
    {
        // Apply draw info
        screen.setTile(position.xz() - this.topLeft, cell.tile())

        // If the cell is not directly at display level, depth and shadows need to be applied.
        if(position.y < this.height)
        {
            // Calculate relative depth
            val depth = this.height - position.y

            // Apply depth
            screen.setDepth(position.xz() - this.topLeft, depth)

            // Determine which drop shadows need to be applied
            val shadows = this.calculateShadowsAt(position)

            // Apply shadows
            screen.setShadows(position.xz() - this.topLeft, shadows)
        }
    }

    /**
     * Draw given structure. This will apply depth values to it, if required.
     *
     * @param screen Screen to draw to
     * @param structure Structure to draw
     */
    private fun drawStructure(screen: Screen, structure: Structure)
    {
        val structurePos = structure.position.xz() - this.topLeft

        // Apply draw info
        screen.setTile(structure.position.xz() - this.topLeft, structure.tile())

        // If the structure is not directly at view height, we need to apply depth information
        if(structure.position.y < this.height)
        {
            // Calculate relative depth
            val depth = this.height - structure.position.y

            // Apply depth
            screen.setDepth(structure.position.xz() - this.topLeft, depth)

            // Determine which drop shadows need to be applied
            val shadows = this.calculateShadowsAt(structure.position)

            // Apply shadows
            screen.setShadows(structure.position.xz() - this.topLeft, shadows)
        }
    }

    /**
     * Draw give covering to screen, possibly modifying the tile of underlying structure of map cell
     *
     * @param screen Screen to draw to
     * @param covering Covering to draw
     * @param underlying Tile of underlying structure or map cell.
     */
    private fun drawCovering(screen: Screen, covering: Covering, underlying: DrawInfo)
    {
        // Are we supposed to stain the underlying object?
        // Having a underlying tile with glyph 0 basically forces us to just overwrite the draw info.
        if(covering.type.drawMode == CoveringDrawMode.Staining && underlying.glyph != 0)
        {
            // Retrieve covering tile to extract data from
            val coveringTile = covering.tile()

            // Modify tile graphical representation
            screen.setTile(covering.position.xz() - this.topLeft, underlying.apply {
                this.foreground = coveringTile.foreground

                // Leave background as it is if its black
                if (this.background != Color.black)
                    this.background = coveringTile.background
            })
        }
        else // Glyph is 0
        {
            /*// Otherwise just overwrite the draw info
            var tile = covering.tile()

            // Switch foreground and background
            val background = tile.background
            tile.background = tile.foreground
            tile.foreground = background

            screen.setTile(covering.position.xz() - this.topLeft, tile)*/

            screen.setTile(covering.position.xz() - this.topLeft, covering.tile())
        }

        // If the covering is not directly at view height, we need to apply depth information
        if(covering.position.y < this.height)
        {
            // Calculate relative depth
            val depth = this.height - covering.position.y

            // Apply depth
            screen.setDepth(covering.position.xz() - this.topLeft, depth)

            // Determine which drop shadows need to be applied
            val shadows = this.calculateShadowsAt(covering.position)

            // Apply shadows
            screen.setShadows(covering.position.xz() - this.topLeft, shadows)
        }
    }

    /**
     * Render component to screen
     *
     * @param screen Screen to render component to
     */
    override fun render(screen: Screen)
    {
        // Retrieve map reference
        val map = this.simulation.map

        // Make sure map data is up to date
        map.updateDatastructures()

        // Render all map cells
        for(ix in 0 until this.dimensions.width)
        {
            for(iz in 0 until this.dimensions.height)
            {
                // Calculate map position
                val mapPos = Position3D(ix + this.topLeft.x, this.height, iz + this.topLeft.y)

                // Only continue if position is inside of map bounds. If its not, us not drawing anything here
                // causes the screen to be black.
                if(this.simulation.map.isInBounds(mapPos))
                {
                    // Try to retrieve a structure for this position
                    val structureToDraw = this.retrieveStructureAt(mapPos)

                    // Try to retrieve a map cell that can be drawn. This will either be a cell at the current
                    // display height, or one that is somewhere below that level.
                    val cellToDraw = this.retrieveDrawableCellAt(mapPos)

                    // Check if we should draw the structure.
                    // We draw the structure if it was found, and the map cell found is either not present or equal or below the structures
                    // position (height wise). We thus overwrite map cell drawing with the structure if they share the same height.
                    if(structureToDraw.isPresent && (!cellToDraw.isPresent || cellToDraw.get().second.y <= structureToDraw.get().position.y))
                    {
                        // Retrieve structure reference
                        val structure = structureToDraw.get()

                        // We know that we have a structure that can be drawn, but it might have a covering on it.
                        val covering = map.coveringAtExact(structure.position)

                        // If it is the case, draw the covering instead, possibly staining the underlying structure
                        // If the structure disallows staining and the covering wants to stain, nothing will happen and
                        // the structure is drawn as usual
                        if(covering.isPresent && !(covering.get().type.drawMode == CoveringDrawMode.Staining && !structure.baseType.isStainable))
                        {
                            this.drawCovering(screen, covering.get(), structure.tile())
                        }
                        else
                        {
                            // Otherwise just draw the structure
                            this.drawStructure(screen, structure)
                        }
                    }
                    else // Try to draw the map cell
                    {
                        // We might not have a cell to draw.
                        if (cellToDraw.isPresent)
                        {
                            // Retrieve map cell reference and its position
                            val pair = cellToDraw.get()

                            // We know that we have a map cell that can be drawn, but it might have a covering on it.
                            val covering = map.coveringAtExact(pair.second)

                            // If it is the case, draw the covering instead, possibly staining the underlying map cell
                            if(covering.isPresent)
                            {
                                this.drawCovering(screen, covering.get(), pair.first.tile())
                            }
                            else
                            {
                                // Otherwise just draw the cell
                                this.drawCell(screen, pair.first, pair.second)
                            }
                        }
                    }
                }
            }
        }
    }
}