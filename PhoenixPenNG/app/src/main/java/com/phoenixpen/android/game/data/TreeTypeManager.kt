package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

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
        return this.treeTypes[key] ?: TreeType.placeholder
    }

    /**
     * Load all treeType types from given JSON document stored as a resource.
     *
     * @param res Resource provider
     * @param id Resource id
     */
    fun loadTrees(res: ResourceProvider, id: String)
    {
        // Read all treeType types contained in the JSON document
        val treeTypeList = Json.parse(TreeType.serializer().list, res.json(id))

        // Store them all in the hash map for later retrieval
        for(treeType in treeTypeList)
        {
            // Check for duplicates
            if(this.treeTypes.containsKey(treeType.identifier))
                throw IllegalStateException("Found duplicate Tree type id: \"${treeType.identifier}\"")

            this.treeTypes.put(treeType.identifier, treeType)
        }
    }
}