package com.phoenixpen.android.map

/**
 * A class managing all data belonging to the game map. This class is not responsible for rendering
 * the map, there is [MapView] for that.
 *
 * @param dimensions The current map dimensions. Can never be changed after constructing a map.
 */
class Map(val dimensions: MapDimensions)
{
    /**
     * Three dimensional array containing all map cells. This is stored in a linear fashion. Use
     * [calculateIndex] to convert x,y,z coordinates to a corresponding index into this array.
     */
    val cells = ArrayList<MapCell>()

    /**
     * Load map data using given map loader implementation
     *
     * @param loader The map loader implementation to use
     */
    fun load(loader: MapLoader)
    {
        throw NotImplementedError()

        // Recalculate stuff...
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
}