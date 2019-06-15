package com.phoenixpen.android.game.data

import com.phoenixpen.android.resources.ResourceProvider
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
        return this.treeStructures[key] ?: TreeStructureType.placeholder
    }

    /**
     * Load all treeStructure types from given JSON document stored as a resource.
     *
     * @param res Resource provider
     * @param id Resource id
     */
    fun loadTreeStructures(res: ResourceProvider, id: String)
    {
        // Read all treeStructure types contained in the JSON document
        val treeStructureList = Json.parse(TreeStructureType.serializer().list, res.json(id))

        // Store them all in the hash map for later retrieval
        for(treeStructure in treeStructureList)
        {
            // Check for duplicates
            if(this.treeStructures.containsKey(treeStructure.identifier))
                throw IllegalStateException("Found duplicate TreeStructure type id: \"${treeStructure.identifier}\"")

            this.treeStructures.put(treeStructure.identifier, treeStructure)
        }
    }
}