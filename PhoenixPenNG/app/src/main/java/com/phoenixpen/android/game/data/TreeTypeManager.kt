package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all Tree type classes. Trees can be loaded from JSON file and
 * be accessed via their unique id.
 */
class TreeTypeManager
{
    /**
     * Map of all known treeTypes based on their unique identifier
     */
    private val treeTypes = HashMap<String, TreeType>()

    /**
     * Add default Trees
     */
    init
    {
        this.treeTypes.put("placeholder", TreeType.placeholder)
    }

    /**
     * Try to retrieve a treeType type based on its unique identifier. This will throw
     * if treeType is not known
     *
     * @param key The unique identifier of the treeType type
     * @return TreeType instance matching the given identifier, if it exists
     */
    fun lookupTree(key: String): TreeType
    {
        // Check if we know this treeType
        if(!treeTypes.containsKey(key))
            throw IllegalArgumentException("Unknown treeType: \"$key\"")

        // Otherwise just return the treeType
        return this.treeTypes[key] ?: throw IllegalArgumentException("Unknown treeType: \"$key\"")
    }

    /**
     * Try to retrieve a treeType type based on its unique identifier. Will return default
     * treeType if requested treeType does not exist.
     *
     * @param key The unique identifier of the treeType type
     * @return TreeType instance matching the given identifier, if it exists
     */
    fun lookupTreeSafe(key: String): TreeType
    {
        if(!this.treeTypes.containsKey(key))
            Log.w("TreeManager", "Missing treeType \"$key\" replaced with placeholder treeType")

        return this.treeTypes[key] ?: TreeType.placeholder
    }

    /**
     * Load all treeType types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadTrees(stream: InputStream)
    {
        // Read all treeType types contained in the JSON document
        val treeTypeList = Json.parse(TreeType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(treeType in treeTypeList)
        {
            // Check for duplicates
            if(this.treeTypes.containsKey(treeType.identifier))
                throw IllegalStateException("Found duplicate Tree type id: \"${treeType.identifier}\"")

            this.treeTypes.put(treeType.identifier, treeType)
        }
    }

    /**
     * Load all treeType types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadTrees(ctx: Context, id: Int)
    {
        this.loadTrees(ctx.resources.openRawResource(id))
    }
}