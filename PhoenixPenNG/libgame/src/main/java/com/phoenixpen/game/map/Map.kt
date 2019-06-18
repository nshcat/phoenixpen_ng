package com.phoenixpen.game.map

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.core.Updateable
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.data.Structure
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.simulation.CoveringHolder
import com.phoenixpen.game.simulation.StructureHolder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A class managing all data belonging to the game map. This class is not responsible for rendering
 * the map, there is [MapView] for that.
 *
 * @param dimensions The current map dimensions. Width is in x direction, height in y and depth in z
 */
class Map(val dimensions: MapDimensions): Updateable
{
    /**
     * Three dimensional array containing all map cells. This is stored in a linear fashion. Use
     * [calculateIndex] to convert x,y,z coordinates to a corresponding index into this array.
     */
    val cells = ArrayList<MapCell>(dimensions.width * dimensions.height * dimensions.depth)

    /**
     * All structures that are present in the game world. Note that this class does not own them,
     * they are contributed by a number of [StructureHolder] classes
     */
    var structures = ArrayList<Structure>()

    /**
     * All coverings that are present in the game world. Note that this class does not own them,
     * they are contributed by a number of [CoveringHolder] classes
     */
    var coverings = ArrayList<Covering>()

    /**
     * Hashmap of all coverings associated with their position, for faster lookup
     */
    private val coveringMap = HashMap<Position3D, MutableList<Covering>>()

    /**
     * Hashmap of all structure associated with their position, for faster lookup
     */
    private val structureMap = HashMap<Position3D, MutableList<Structure>>()

    /**
     * All registered structure holders. These are used to retrieve all structures that need to be rendered.
     */
    private val structureHolders = ArrayList<StructureHolder>()

    /**
     * All registered covering holders. These are used to retrieve all coverings that need to be rendered.
     */
    private val coveringHolders = ArrayList<CoveringHolder>()

    /**
     * Initialize each map cell to a known, empty default
     */
    init
    {
        // The total amount of cells stored in this map
        val cellCount = dimensions.width * dimensions.height * dimensions.depth

        // Initialize all cells
        for(i in 0 until cellCount)
        {
            cells.add(MapCell.empty())
        }
    }

    /**
     * Update structure and covering caches
     */
    override fun update(elapsedTicks: Int)
    {
        this.updateDatastructures()
    }

    /**
     * Update all acceleration datastructures
     */
    fun updateDatastructures()
    {
        // Update game object collections
        this.coverings = this.retrieveCoverings()
        this.structures = this.retrieveStructures()

        // Update acceleration structures
        this.updateCoveringMap()
        this.updateStructureMap()
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
     * Register new covering holder to be used as a source of coverings.
     *
     * @param holder The covering holder to add
     */
    fun registerHolder(holder: CoveringHolder)
    {
        this.coveringHolders.add(holder)
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
     * Retrieve all structures present at a given map position, but NOT below.
     *
     * @param mapPosition Position to check for structures at
     * @param ignoreInvisible Whether structures that are currently not drawn should be ignored
     * @return Structures found at the given position
     */
    fun getStructuresAtExact(mapPosition: Position3D, ignoreInvisible: Boolean): Optional<List<Structure>>
    {
        if(!ignoreInvisible)
            return Optional.ofNullable(this.structureMap[mapPosition])
        else
        {
            // Retrieve the entry for this position
            val entry = this.structureMap[mapPosition]

            // Entry might be null
            if(entry != null)
            {
                // Otherwise filter for structures that are currently not invisible
                val structures = entry.filter { x -> x.shouldDraw() }

                if(structures.isEmpty())
                    return Optional.empty()
                else
                    return Optional.of(structures.toList())
            }
            else return Optional.empty()
        }
    }

    /**
     * Retrieve first structure present at a given map position, but NOT below.
     *
     * @param mapPosition Position to check for structures at
     * @param ignoreInvisible Whether to ignore invisible structures
     * @return Structure found at the given position
     */
    fun getStructureAtExact(mapPosition: Position3D, ignoreInvisible: Boolean): Optional<Structure>
    {
        val result = this.getStructuresAtExact(mapPosition, ignoreInvisible)

        if(result.isPresent && result.get().isNotEmpty())
            return Optional.of(result.get().first())
        else return Optional.empty()
    }

    /**
     * Retrieve covering at given position, if one exists. Returns the last encountered covering,
     * since it overlays all the other ones.
     *
     * @param position Position to look at
     * @return Covering reference, if found
     */
    fun coveringAtExact(position: Position3D): Optional<Covering>
    {
        if(this.coveringMap.containsKey(position))
        {
            val list = this.coveringMap[position] ?: throw RuntimeException("Should not happen")

            if(list.isNotEmpty())
                return Optional.of(list.last())
        }

        return Optional.empty()
    }

    /**
     * Retrieve all coverings managed by the registered covering holders.
     *
     * @return List containing all coverings managed by registered covering holders
     */
    private fun retrieveCoverings(): ArrayList<Covering>
    {
        // Destination collection for the covering references
        val coverings = ArrayList<Covering>()

        // Aggregate all structures
        for(holder in this.coveringHolders)
        {
            coverings.addAll(holder.coverings())
        }

        return coverings
    }

    /**
     * Update the structure map for faster structure look up
     */
    private fun updateStructureMap()
    {
        // Clear the hash map
        this.structureMap.clear()

        // Insert all elements
        for(structure in this.structures)
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
     * Update the covering map for faster covering look up
     */
    private fun updateCoveringMap()
    {
        // Clear the hash map
        this.coveringMap.clear()

        // Insert all elements
        for(covering in this.coverings)
        {
            // Is there already a list in the map?
            if(!this.coveringMap.containsKey(covering.position))
            {
                // If not, add one
                this.coveringMap.put(covering.position, ArrayList())
            }

            // Retrieve list. It is guaranteed to exist by now.
            val list = this.coveringMap.get(covering.position) ?: throw RuntimeException("Should not happen")

            // Add the structure to it
            list.add(covering)
        }
    }

    /**
     * Unload map data using given map unloader implementation
     *
     * @param unloader The map unloader implementation to use
     */
    fun unload(unloader: MapUnloader)
    {
        throw NotImplementedError()
    }

    /**
     * Calculate linear index for given 3D position. Note that xz are describing the horizontal plane,
     * while y determines the height
     *
     * @param pos Position to convert into linear index
     * @return Linear index matching given position
     */
    fun calculateIndex(pos: Position3D): Int
    {
        return (pos.z * this.dimensions.width * this.dimensions.height) + (pos.y * this.dimensions.width) + pos.x
    }

    /**
     * Retrieve map cell at given position
     *
     * @param pos The map cell position
     * @return MapCell at given position, if not out of bounds
     */
    fun cellAt(pos: Position3D): MapCell
    {
        return this.cells[this.calculateIndex(pos)]
    }

    /**
     * Determine if the given position is inside of the current map bounds
     *
     * @param pos Position to check
     * @return Flag indicating check result
     */
    fun isInBounds(pos: Position3D): Boolean
    {
        return (pos.x >= 0 && pos.x < this.dimensions.width) &&
                (pos.y >= 0 && pos.y < this.dimensions.height) &&
                (pos.z >= 0 && pos.z < this.dimensions.depth)
    }

    /**
     * Determine if the given position is inside of the current map bounds
     *
     * @param x X coordinate of the position to check
     * @param y X coordinate of the position to check
     * @param z X coordinate of the position to check
     * @return Flag indicating check result
     */
    fun isInBounds(x: Int, y: Int, z: Int): Boolean
    {
        return (x >= 0 && x < this.dimensions.width) &&
                (y >= 0 && y < this.dimensions.height) &&
                (z >= 0 && z < this.dimensions.depth)
    }

    /**
     * Map creation routines
     */
    companion object
    {
        /**
         * Load map data using given map loader implementation
         *
         * @param loader The map loader implementation to use
         */
        fun load(loader: MapLoader): Map
        {
            // Delegate map loading process to interface implementation
            return loader.load()
        }
    }
}