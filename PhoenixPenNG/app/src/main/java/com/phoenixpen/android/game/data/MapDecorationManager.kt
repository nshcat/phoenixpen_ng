package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all map decoration type classes. Map decorations can be loaded from JSON file and
 * be accessed via their unique id.
 */
class MapDecorationManager
{
    /**
     * Map of all known map decorations based on their unique identifier
     */
    private val mapDecorations = HashMap<String, MapDecorationType>()

    /**
     * Add default map decorations
     */
    init
    {
        this.mapDecorations.put("placeholder", MapDecorationType.placeholder)
    }

    /**
     * Try to retrieve a mapDecoration type based on its unique identifier. This will throw
     * if map decoration is not known
     *
     * @param key The unique identifier of the mapDecoration type
     * @return MapDecorationType instance matching the given identifier, if it exists
     */
    fun lookupMapDecoration(key: String): MapDecorationType
    {
        // Check if we know this mapDecoration
        if(!mapDecorations.containsKey(key))
            throw IllegalArgumentException("Unknown mapDecoration: \"$key\"")

        // Otherwise just return the mapDecoration
        return this.mapDecorations[key] ?: throw IllegalArgumentException("Unknown mapDecoration: \"$key\"")
    }

    /**
     * Try to retrieve a map decoration type based on its unique identifier. Will return default
     * map decoration if requested mapDecoration does not exist.
     *
     * @param key The unique identifier of the map decoration type
     * @return MapDecorationType instance matching the given identifier, if it exists
     */
    fun lookupMapDecorationSafe(key: String): MapDecorationType
    {
        if(!this.mapDecorations.containsKey(key))
            Log.w("MapDecorationManager", "Missing map decoration \"$key\" replaced with placeholder map decoration")

        return this.mapDecorations[key] ?: MapDecorationType.placeholder
    }

    /**
     * Load all mapDecoration types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadMapDecorations(stream: InputStream)
    {
        // Read all mapDecoration types contained in the JSON document
        val mapDecorationList = Json.parse(MapDecorationType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(mapDecoration in mapDecorationList)
        {
            // Check for duplicates
            if(this.mapDecorations.containsKey(mapDecoration.basicData.identifier))
                throw IllegalStateException("Found duplicate mapDecoration type id: \"${mapDecoration.basicData.identifier}\"")

            this.mapDecorations.put(mapDecoration.basicData.identifier, mapDecoration)
        }
    }

    /**
     * Load all map decoration types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadMapDecorations(ctx: Context, id: Int)
    {
        this.loadMapDecorations(ctx.resources.openRawResource(id))
    }
}