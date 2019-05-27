package com.phoenixpen.android.game.map

import com.phoenixpen.android.game.ascii.Position3D

/**
 * A class managing all data belonging to the game map. This class is not responsible for rendering
 * the map, there is [MapView] for that.
 *
 * @param dimensions The current map dimensions. Width is in x direction, height in y and depth in z
 */
class Map(val dimensions: MapDimensions)
{
    /**
     * Three dimensional array containing all map cells. This is stored in a linear fashion. Use
     * [calculateIndex] to convert x,y,z coordinates to a corresponding index into this array.
     */
    val cells = ArrayList<MapCell>(dimensions.width * dimensions.height * dimensions.depth)

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