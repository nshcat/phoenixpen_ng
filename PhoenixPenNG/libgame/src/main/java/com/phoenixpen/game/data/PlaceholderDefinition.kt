package com.phoenixpen.game.data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.JsonInput

/**
 * A class mapping tree shape placeholder labels to actual tree part type identifiers.
 */
@Serializable(with=PlaceholderDefinitionSerializer::class)
class PlaceholderDefinition(val definitions: HashMap<PlaceholderType, String>)
{
    /**
     * Retrieve definition for given placeholder type
     *
     * @param placeholder Placeholder type to look up definition for
     * @return Definition for given placeholder type
     */
    fun definitionFor(placeholder: PlaceholderType): String
    {
        return this.definitions[placeholder] ?: throw IllegalArgumentException("Unknown placeholder type")
    }

    companion object
    {
        /**
         * Placeholder instance used in placeholder tree type
         */
        val placeholder = PlaceholderDefinition(HashMap()).apply{
            for(type in PlaceholderType.values())
                this.definitions.put(type, "placeholder")
        }
    }
}

/**
 * Custom serializer for placeholder definitions
 */
@Serializer(forClass = PlaceholderDefinition::class)
object PlaceholderDefinitionSerializer: KSerializer<PlaceholderDefinition>
{
    override val descriptor: SerialDescriptor =
            StringDescriptor.withName("PlaceholderDefinition")

    override fun serialize(output: Encoder, obj: PlaceholderDefinition)
    {
        throw NotImplementedError("Serialization not implemented for class PlaceholderDefinition")
    }

    override fun deserialize(input: Decoder): PlaceholderDefinition
    {
        // Our mapping between placeholder types and tree part type identifiers
        val mapping = HashMap<PlaceholderType, String>()

        // The JSON AST only supports unchecked casts, so we need to be prepared for any
        // type mismatch exceptions here
        try
        {
            // Retrieve JSON input
            val jsonInput = input as JsonInput

            // Decode JSON to AST and interpret as JSON object
            val root = jsonInput.decodeJson().jsonObject

            // Iterate through all known placeholder type identifiers, trying to find a matching
            // JSON entry.
            for(placeholder in PlaceholderType.values())
            {
                // If it was found, just apply mapping
                if(root.containsKey(placeholder.identifier))
                {
                    // Retrieve entry
                    val entry = root[placeholder.identifier] ?: throw IllegalStateException("Entry not found")

                    mapping.put(placeholder, entry.primitive.content)
                }
                else
                {
                    // It was not found. Apply default type.
                    val defaultType = "${placeholder.identifier}_default"
                    mapping.put(placeholder, defaultType)
                }
            }

            // Create placeholder definition instance and return
            return PlaceholderDefinition(mapping)
        }
        catch(e: Exception)
        {
            throw IllegalStateException("Failed to deserialize PlaceholderDefinition: ${e.message}")
        }
    }
}