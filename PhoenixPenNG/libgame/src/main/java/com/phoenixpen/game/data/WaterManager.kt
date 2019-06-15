package com.phoenixpen.game.data

import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * A class managing all Water type classes. Waters can be loaded from JSON file and
 * be accessed via their unique id.
 */
class WaterManager
{
    /**
     * Map of all known waterTypes based on their unique identifier
     */
    private val waterTypes = HashMap<String, WaterType>()

    /**
     * Add default Waters
     */
    init
    {
        this.waterTypes.put("placeholder", WaterType.placeholder)
    }

    /**
     * Try to retrieve a waterType type based on its unique identifier. This will throw
     * if waterType is not known
     *
     * @param key The unique identifier of the waterType type
     * @return WaterType instance matching the given identifier, if it exists
     */
    fun lookupWaterType(key: String): WaterType
    {
        // Check if we know this waterType
        if(!waterTypes.containsKey(key))
            throw IllegalArgumentException("Unknown waterType: \"$key\"")

        // Otherwise just return the waterType
        return this.waterTypes[key] ?: throw IllegalArgumentException("Unknown waterType: \"$key\"")
    }

    /**
     * Try to retrieve a waterType type based on its unique identifier. Will return default
     * waterType if requested waterType does not exist.
     *
     * @param key The unique identifier of the waterType type
     * @return WaterType instance matching the given identifier, if it exists
     */
    fun lookupWaterTypeSafe(key: String): WaterType
    {
        return this.waterTypes[key] ?: WaterType.placeholder
    }

    /**
     * Load all waterType types from given JSON document stored as a resource.
     *
     * @param res Resource provider
     * @param id Resource id
     */
    fun loadWaterTypes(res: ResourceProvider, id: String)
    {
        // Read all waterType types contained in the JSON document
        val waterTypeList = Json.parse(WaterType.serializer().list, res.json(id))

        // Store them all in the hash map for later retrieval
        for(waterType in waterTypeList)
        {
            // Check for duplicates
            if(this.waterTypes.containsKey(waterType.basicData.identifier))
                throw IllegalStateException("Found duplicate waterType type id: \"${waterType.basicData.identifier}\"")

            this.waterTypes.put(waterType.basicData.identifier, waterType)
        }
    }
}