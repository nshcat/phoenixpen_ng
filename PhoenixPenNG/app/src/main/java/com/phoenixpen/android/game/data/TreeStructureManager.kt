package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all TreeStructure type classes. TreeStructures can be loaded from JSON file and
 * be accessed via their unique id.
 */
class TreeStructureManager
{
    /**
     * Map of all known treeStructures based on their unique identifier
     */
    private val treeStructures = HashMap<String, TreeStructureType>()

    /**
     * Add default TreeStructures
     */
    init
    {
        this.treeStructures.put("placeholder", TreeStructureType.placeholder)
    }

    /**
     * Try to retrieve a treeStructure type based on its unique identifier. This will throw
     * if treeStructure is not known
     *
     * @param key The unique identifier of the treeStructure type
     * @return TreeStructureType instance matching the given identifier, if it exists
     */
    fun lookupTreeStructure(key: String): TreeStructureType
    {
        // Check if we know this treeStructure
        if(!treeStructures.containsKey(key))
            throw IllegalArgumentException("Unknown treeStructure: \"$key\"")

        // Otherwise just return the treeStructure
        return this.treeStructures[key] ?: throw IllegalArgumentException("Unknown treeStructure: \"$key\"")
    }

    /**
     * Try to retrieve a treeStructure type based on its unique identifier. Will return default
     * treeStructure if requested treeStructure does not exist.
     *
     * @param key The unique identifier of the treeStructure type
     * @return TreeStructureType instance matching the given identifier, if it exists
     */
    fun lookupTreeStructureSafe(key: String): TreeStructureType
    {
        if(!this.treeStructures.containsKey(key))
            Log.w("TreeStructureManager", "Missing treeStructure \"$key\" replaced with placeholder treeStructure")

        return this.treeStructures[key] ?: TreeStructureType.placeholder
    }

    /**
     * Load all treeStructure types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadTreeStructures(stream: InputStream)
    {
        // Read all treeStructure types contained in the JSON document
        val treeStructureList = Json.parse(TreeStructureType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(treeStructure in treeStructureList)
        {
            // Check for duplicates
            if(this.treeStructures.containsKey(treeStructure.identifier))
                throw IllegalStateException("Found duplicate TreeStructure type id: \"${treeStructure.identifier}\"")

            this.treeStructures.put(treeStructure.identifier, treeStructure)
        }
    }

    /**
     * Load all treeStructure types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadTreeStructures(ctx: Context, id: Int)
    {
        this.loadTreeStructures(ctx.resources.openRawResource(id))
    }
}