package com.phoenixpen.android.map

import java.nio.file.Path

/**
 * Class managing the de/serialization of map data to the filesystem.
 *
 * @property filePath The file path to serialize map data to / deserialize map data from
 */
class MapSerialization(var filePath: Path): MapLoader, MapUnloader
{
    /**
     * Load map from file
     */
    override fun load(map: Map)
    {
        throw NotImplementedError()
    }

    /**
     * Unload map to file
     */
    override fun unload(map: Map)
    {
        throw NotImplementedError()
    }
}