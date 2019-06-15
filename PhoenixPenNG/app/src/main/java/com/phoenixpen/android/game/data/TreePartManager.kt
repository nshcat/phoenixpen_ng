package com.phoenixpen.android.game.data

import com.phoenixpen.android.resources.ResourceProvider
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
        return this.treeParts[key] ?: TreePartType.placeholder
    }

    /**
     * Load all treePart types from given JSON document stored as a resource.
     *
     * @param res Resource Provider
     * @param id Resource id
     */
    fun loadTreeParts(res: ResourceProvider, id: String)
    {
        // Read all treePart types contained in the JSON document
        val treePartList = Json.parse(TreePartType.serializer().list, res.json(id))

        // Store them all in the hash map for later retrieval
        for(treePart in treePartList)
        {
            // Check for duplicates
            if(this.treeParts.containsKey(treePart.basicData.identifier))
                throw IllegalStateException("Found duplicate treePart type id: \"${treePart.basicData.identifier}\"")

            this.treeParts.put(treePart.basicData.identifier, treePart)
        }
    }
}