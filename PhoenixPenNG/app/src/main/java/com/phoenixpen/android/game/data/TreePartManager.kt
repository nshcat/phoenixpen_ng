package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all TreePart type classes. TreeParts can be loaded from JSON file and
 * be accessed via their unique id.
 */
class TreePartManager
{
    /**
     * Map of all known treeParts based on their unique identifier
     */
    private val treeParts = HashMap<String, TreePartType>()

    /**
     * Add default TreeParts
     */
    init
    {
        this.treeParts.put("placeholder", TreePartType.placeholder)
    }

    /**
     * Try to retrieve a treePart type based on its unique identifier. This will throw
     * if treePart is not known
     *
     * @param key The unique identifier of the treePart type
     * @return TreePartType instance matching the given identifier, if it exists
     */
    fun lookupTreePart(key: String): TreePartType
    {
        // Check if we know this treePart
        if(!treeParts.containsKey(key))
            throw IllegalArgumentException("Unknown treePart: \"$key\"")

        // Otherwise just return the treePart
        return this.treeParts[key] ?: throw IllegalArgumentException("Unknown treePart: \"$key\"")
    }

    /**
     * Try to retrieve a treePart type based on its unique identifier. Will return default
     * treePart if requested treePart does not exist.
     *
     * @param key The unique identifier of the treePart type
     * @return TreePartType instance matching the given identifier, if it exists
     */
    fun lookupTreePartSafe(key: String): TreePartType
    {
        if(!this.treeParts.containsKey(key))
            Log.w("TreePartManager", "Missing treePart \"$key\" replaced with placeholder treePart")

        return this.treeParts[key] ?: TreePartType.placeholder
    }

    /**
     * Load all treePart types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadTreeParts(stream: InputStream)
    {
        // Read all treePart types contained in the JSON document
        val treePartList = Json.parse(TreePartType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(treePart in treePartList)
        {
            // Check for duplicates
            if(this.treeParts.containsKey(treePart.basicData.identifier))
                throw IllegalStateException("Found duplicate treePart type id: \"${treePart.basicData.identifier}\"")

            this.treeParts.put(treePart.basicData.identifier, treePart)
        }
    }

    /**
     * Load all treePart types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadTreeParts(ctx: Context, id: Int)
    {
        this.loadTreeParts(ctx.resources.openRawResource(id))
    }
}