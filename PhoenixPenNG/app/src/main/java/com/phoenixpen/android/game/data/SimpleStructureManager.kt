package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all simpleStructure type classes. SimpleStructures can be loaded from JSON file and
 * be accessed via their unique id.
 */
class SimpleStructureManager
{
    /**
     * Map of all known simpleStructures based on their unique identifier
     */
    private val simpleStructures = HashMap<String, SimpleStructureType>()

    /**
     * Add default simpleStructures
     */
    init
    {
        this.simpleStructures.put("placeholder", SimpleStructureType.placeholder)
    }

    /**
     * Try to retrieve a simpleStructure type based on its unique identifier. This will throw
     * if simpleStructure is not known
     *
     * @param key The unique identifier of the simpleStructure type
     * @return SimpleStructureType instance matching the given identifier, if it exists
     */
    fun lookupSimpleStructure(key: String): SimpleStructureType
    {
        // Check if we know this simpleStructure
        if(!simpleStructures.containsKey(key))
            throw IllegalArgumentException("Unknown simpleStructure: \"$key\"")

        // Otherwise just return the simpleStructure
        return this.simpleStructures[key] ?: throw IllegalArgumentException("Unknown simpleStructure: \"$key\"")
    }

    /**
     * Try to retrieve a simpleStructure type based on its unique identifier. Will return default
     * simpleStructure if requested simpleStructure does not exist.
     *
     * @param key The unique identifier of the simpleStructure type
     * @return SimpleStructureType instance matching the given identifier, if it exists
     */
    fun lookupSimpleStructureSafe(key: String): SimpleStructureType
    {
        if(!this.simpleStructures.containsKey(key))
            Log.w("SimpleStructureManager", "Missing simpleStructure \"$key\" replaced with placeholder simpleStructure")

        return this.simpleStructures[key] ?: SimpleStructureType.placeholder
    }

    /**
     * Load all simpleStructure types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadSimpleStructures(stream: InputStream)
    {
        // Read all simpleStructure types contained in the JSON document
        val simpleStructureList = Json.parse(SimpleStructureType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(simpleStructure in simpleStructureList)
        {
            // Check for duplicates
            if(this.simpleStructures.containsKey(simpleStructure.basicData.identifier))
                throw IllegalStateException("Found duplicate simpleStructure type id: \"${simpleStructure.basicData.identifier}\"")

            this.simpleStructures.put(simpleStructure.basicData.identifier, simpleStructure)
        }
    }

    /**
     * Load all simpleStructure types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadSimpleStructures(ctx: Context, id: Int)
    {
        this.loadSimpleStructures(ctx.resources.openRawResource(id))
    }
}