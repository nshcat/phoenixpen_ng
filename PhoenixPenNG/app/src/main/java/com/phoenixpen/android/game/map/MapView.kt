package com.phoenixpen.android.game.map

import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.game.ascii.*
import com.phoenixpen.android.game.simulation.Simulation
import com.phoenixpen.android.game.core.TickCounter
import com.phoenixpen.android.game.data.Structure
import com.phoenixpen.android.game.simulation.StructureHolder
import java.lang.Integer.min
import java.util.*
import kotlin.collections.ArrayList

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
     * All registered structure holders. These are used to retrieve all structures that need to be rendered.
     */
    private val structureHolders = ArrayList<StructureHolder>()

    /**
     * A hash map used to speed up lookup for structures at a given position in the world map
     */
    private val structureMap = HashMap<Position3D, MutableList<Structure>>()

    /**
     * Buffer used to hold all aggregated structures in each frame.
     */
    private var structureBuffer = ArrayList<Structure>()

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
        this.height = 3

        //this.height = 2 + (this.counter.totalPeriods % 6)
    }

    /**
     * Update the structure map for faster structure look up
     */
    private fun updateStructureMap()
    {
        // Clear the hash map
        this.structureMap.clear()

        // Insert all elements
        for(structure in this.structureBuffer)
        {
            // Is there already a list in the map?
            if(!this.structureMap.containsKey(structure.position))
            {
                // If not, add one
                this.structureMap.put(structure.position, ArrayList())
            }

            // Retrieve list. It is guaranteed to exist by now.
            val list = this.structureMap.get(structure.position) ?: throw RuntimeException("Should not happen")

            // Add the structure to it
            list.add(structure)
        }
    }

    /**
     * Register new structure holder to be used as a source of structures.
     *
     * @param holder The structure holder to add
     */
    fun registerHolder(holder: StructureHolder)
    {
        this.structureHolders.add(holder)
    }

    /**
     * Retrieve all structures managed by the registered structure holders.
     *
     * @return List containing all structures managed by registered structure holders
     */
    private fun retrieveStructures(): ArrayList<Structure>
    {
        // Destination collection for the structure references
        val structures = ArrayList<Structure>()

        // Aggregate all structures
        for(holder in this.structureHolders)
        {
            structures.addAll(holder.structures())
        }

        return structures
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
            val structures = this.getStructuresAtExact(Position3D(mapPosition.x, iy, mapPosition.z))

            // If we found any structures, return the first one found.
            if(structures.isPresent && structures.get().isNotEmpty())
                return Optional.of(structures.get().first())
        }

        // No structure was found
        return Optional.empty()
    }

    /**
     * Retrieve all structures present at a given map position, but NOT below.
     *
     * @param mapPosition Position to check for structures at
     * @return Structures found at the given position
     */
    private fun getStructuresAtExact(mapPosition: Position3D): Optional<List<Structure>>
    {
        return Optional.ofNullable(this.structureMap[mapPosition])
    }

    /**
     * Retrieve first structure present at a given map position, but NOT below.
     *
     * @param mapPosition Position to check for structures at
     * @return Structure found at the given position
     */
    private fun getStructureAtExact(mapPosition: Position3D): Optional<Structure>
    {
        val result = this.getStructuresAtExact(mapPosition)

        if(result.isPresent && result.get().isNotEmpty())
            return Optional.of(result.get().first())
        else return Optional.empty()
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
                    if (!this.simulation.map.cellAt(newPos).isTransparent() || getStructureAtExact(newPos).isPresent)
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
        screen.setTile(position.xz(), cell.tile())

        // If the cell is not directly at display level, depth and shadows need to be applied.
        if(position.y < this.height)
        {
            // Calculate relative depth
            val depth = this.height - position.y

            // Apply depth
            screen.setDepth(position.xz(), depth)

            // Determine which drop shadows need to be applied
            val shadows = this.calculateShadowsAt(position)

            // Apply shadows
            screen.setShadows(position.xz(), shadows)
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
        // Apply draw info
        screen.setTile(structure.position.xz(), structure.tile())

        // If the structure is not directly at view height, we need to apply depth information
        if(structure.position.y < this.height)
        {
            // Calculate relative depth
            val depth = this.height - structure.position.y

            // Apply depth
            screen.setDepth(structure.position.xz(), depth)

            // Determine which drop shadows need to be applied
            val shadows = this.calculateShadowsAt(structure.position)

            // Apply shadows
            screen.setShadows(structure.position.xz(), shadows)
        }
    }

    /**
     * Render component to screen
     *
     * @param screen Screen to render component to
     */
    override fun render(screen: Screen)
    {
        // Retrieve all structures
        this.structureBuffer = this.retrieveStructures()

        // Update structure map
        this.updateStructureMap()

        // Render all map cells
        for(ix in 0 until this.dimensions.width)
        {
            for(iz in 0 until this.dimensions.height)
            {
                // Calculate map position
                val mapPos = Position3D(ix + this.topLeft.x, this.height, iz + this.topLeft.y)

                // Only continue of its inside of the map bounds. If its not, us not drawing anything here
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
                        this.drawStructure(screen, structureToDraw.get())
                    }
                    else // Try to draw the map cell
                    {
                        // We might not have a cell to draw.
                        if (cellToDraw.isPresent)
                        {
                            // Retrieve map cell reference and its position
                            val pair = cellToDraw.get()

                            // Actually draw the cell
                            this.drawCell(screen, pair.first, pair.second)
                        }
                    }
                }
            }
        }
    }
}