package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import com.phoenixpen.android.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all Covering type classes. Coverings can be loaded from JSON file and
 * be accessed via their unique id.
 */
class CoveringManager
{
    /**
     * Map of all known coverings based on their unique identifier
     */
    private val coverings = HashMap<String, CoveringType>()

    /**
     * Add default Coverings
     */
    init
    {
        this.coverings.put("placeholder", CoveringType.placeholder)
    }

    /**
     * Try to retrieve a covering type based on its unique identifier. This will throw
     * if covering is not known
     *
     * @param key The unique identifier of the covering type
     * @return CoveringType instance matching the given identifier, if it exists
     */
    fun lookupCovering(key: String): CoveringType
    {
        // Check if we know this covering
        if(!coverings.containsKey(key))
            throw IllegalArgumentException("Unknown covering: \"$key\"")

        // Otherwise just return the covering
        return this.coverings[key] ?: throw IllegalArgumentException("Unknown covering: \"$key\"")
    }

    /**
     * Try to retrieve a covering type based on its unique identifier. Will return default
     * covering if requested covering does not exist.
     *
     * @param key The unique identifier of the covering type
     * @return CoveringType instance matching the given identifier, if it exists
     */
    fun lookupCoveringSafe(key: String): CoveringType
    {
        if(!this.coverings.containsKey(key))
            Log.w("CoveringManager", "Missing covering \"$key\" replaced with placeholder covering")

        return this.coverings[key] ?: CoveringType.placeholder
    }

    /**
     * Load all covering types from given JSON document stored as a resource.
     *
     * @param res Resource provider
     * @param id Resource id
     */
    fun loadCoverings(res: ResourceProvider, id: String)
    {
        // Read all covering types contained in the JSON document
        val coveringList = Json.parse(CoveringType.serializer().list, res.json(id))

        // Store them all in the hash map for later retrieval
        for(covering in coveringList)
        {
            // Check for duplicates
            if(this.coverings.containsKey(covering.identifier))
                throw IllegalStateException("Found duplicate covering type id: \"${covering.identifier}\"")

            this.coverings.put(covering.identifier, covering)
        }
    }
}