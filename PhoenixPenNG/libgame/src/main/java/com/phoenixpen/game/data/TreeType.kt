package com.phoenixpen.game.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class that describes how a tree looks.
 *
 * @property identifier Unique identifier for this tree type
 * @property displayName Human readable name for this tree type
 * @property description Description for this tree type
 * @property placeholderDefinition Definition for the tree structure placeholder types
 * @property structureTypes All structure templates that can be used for this tree
 * @property bloomCoveringType Covering type identifier to use for blooming leaves
 * @property blooms Whether this tree blooms in mid spring
 */
@Serializable
data class TreeType(
        val identifier: String,
        @SerialName("display_name") val displayName: String,
        val description: String = "",
        @SerialName("parts") val placeholderDefinition: PlaceholderDefinition,
        @SerialName("structure_types") val structureTypes: List<String>,
        val blooms: Boolean = false,
        @SerialName("bloom_covering") val bloomCoveringType: String = ""
        )
{
    companion object
    {
        /**
         * Placeholder tree type used if game encounters a missing tree type
         */
        val placeholder = TreeType(
                "placeholder",
                "MISSING TREE TYPE",
                "MISSING TREE TYPE",
                PlaceholderDefinition.placeholder,
                listOf("placeholder")
        )
    }
}