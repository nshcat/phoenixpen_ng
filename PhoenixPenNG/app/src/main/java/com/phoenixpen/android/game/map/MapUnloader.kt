package com.phoenixpen.android.game.map

/**
 * An interface for classes that can write back map data to some kind of destination. Mostly used
 * to implement map serialization to the file system.
 */
interface MapUnloader
{
    /**
     * Unload given map
     *
     * @param map Map to unload
     */
    fun unload(map: Map)
}