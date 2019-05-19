package com.phoenixpen.android.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all material type classes. Materials can be loaded from JSON file and
 * be accessed via their unique id.
 */
class MaterialManager
{
    /**
     * Map of all known materials based on their unique identifier
     */
    private val materials = HashMap<String, MaterialType>()

    /**
     * Add default materials
     */
    init
    {
        this.materials.put("air", MaterialType.air)
        this.materials.put("placeholder", MaterialType.placeholder)
    }

    /**
     * Try to retrieve a material type based on its unique identifier. This will throw
     * if material is not known
     *
     * @param key The unique identifier of the material type
     * @return MaterialType instance matching the given identifier, if it exists
     */
    fun lookupMaterial(key: String): MaterialType
    {
        // Check if we know this material
        if(!materials.containsKey(key))
            throw IllegalArgumentException("Unknown material: \"$key\"")

        // Otherwise just return the material
        return this.materials[key] ?: throw IllegalArgumentException("Unknown material: \"$key\"")
    }

    /**
     * Try to retrieve a material type based on its unique identifier. Will return default
     * material if requested material does not exist.
     *
     * @param key The unique identifier of the material type
     * @return MaterialType instance matching the given identifier, if it exists
     */
    fun lookupMaterialSafe(key: String): MaterialType
    {
        if(!this.materials.containsKey(key))
            Log.w("MaterialManager", "Missing material \"$key\" replaced with placeholder material")

        return this.materials[key] ?: MaterialType.placeholder
    }

    /**
     * Load all material types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadMaterials(stream: InputStream)
    {
        // Read all material types contained in the JSON document
        val materialList = Json.parse(MaterialType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(material in materialList)
        {
            // Check for duplicates
            if(this.materials.containsKey(material.identifier))
                throw IllegalStateException("Found duplicate material type id: \"${material.identifier}\"")

            this.materials.put(material.identifier, material)
        }
    }

    /**
     * Load all material types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadMaterials(ctx: Context, id: Int)
    {
        this.loadMaterials(ctx.resources.openRawResource(id))
    }
}