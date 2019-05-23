package com.phoenixpen.android.game.map

/**
 * An interface for classes that can read map data from some kind of source, thereby populating a
 * map instance with map cell data. This can either be map generation, or things like map data loading
 * from file.
 */
interface MapLoader
{
    /**
     * Load map with given map as destination
     *
     * @return Loaded map
     */
    fun load(): Map
}