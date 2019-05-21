package com.phoenixpen.android.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all structure type classes.
 */
class StructureManager
{
    /**
     * Map of all known structures based on their unique identifier
     */
    private val structures = HashMap<String, StructureType>()

    /**
     * Register placeholder structure
     */
    init
    {
        this.structures.put(StructureType.placeholder.identifier, StructureType.placeholder)
    }

    /**
     * Try to retrieve a structure type based on its unique identifier. This will throw
     * if an structure type is not known
     *
     * @param key The unique identifier of the structure type
     * @return structureType instance matching the given identifier, if it exists
     */
    fun lookupStructureType(key: String): StructureType
    {
        // Check if we know this structure
        if(!structures.containsKey(key))
            throw IllegalArgumentException("Unknown structure: \"$key\"")

        // Otherwise just return the structure
        return this.structures[key] ?: throw IllegalArgumentException("Unknown structure: \"$key\"")
    }

    /**
     * Try to retrieve a structure type based on its unique identifier. Will return default
     * structure if requested structure does not exist.
     *
     * @param key The unique identifier of the structure type
     * @return structureType instance matching the given identifier, if it exists
     */
    fun lookupStructureTypeSafe(key: String): StructureType
    {
        if(!this.structures.containsKey(key))
            Log.w("StructureManager", "Missing structure \"$key\" replaced with placeholder structure")

        return this.structures[key] ?: StructureType.placeholder
    }

    /**
     * Load all structure types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadStructures(stream: InputStream)
    {
        // Read all structure types contained in the JSON document
        val structureList = Json.parse(StructureType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(structure in structureList)
        {
            // Check for duplicates
            if(this.structures.containsKey(structure.identifier))
                throw IllegalStateException("Found duplicate structure type id: \"${structure.identifier}\"")

            this.structures.put(structure.identifier, structure)
        }
    }

    /**
     * Load all structure types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadStructures(ctx: Context, id: Int)
    {
        this.loadStructures(ctx.resources.openRawResource(id))
    }
}