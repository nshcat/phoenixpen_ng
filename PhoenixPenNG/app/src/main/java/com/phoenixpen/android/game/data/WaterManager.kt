package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

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
        if(!this.waterTypes.containsKey(key))
            Log.w("WaterManager", "Missing waterType \"$key\" replaced with placeholder waterType")

        return this.waterTypes[key] ?: WaterType.placeholder
    }

    /**
     * Load all waterType types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadWaterTypes(stream: InputStream)
    {
        // Read all waterType types contained in the JSON document
        val waterTypeList = Json.parse(WaterType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(waterType in waterTypeList)
        {
            // Check for duplicates
            if(this.waterTypes.containsKey(waterType.basicData.identifier))
                throw IllegalStateException("Found duplicate waterType type id: \"${waterType.basicData.identifier}\"")

            this.waterTypes.put(waterType.basicData.identifier, waterType)
        }
    }

    /**
     * Load all waterType types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadWaterTypes(ctx: Context, id: Int)
    {
        this.loadWaterTypes(ctx.resources.openRawResource(id))
    }
}