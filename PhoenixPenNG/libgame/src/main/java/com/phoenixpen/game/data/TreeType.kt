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
 * @property doesBloom Whether this tree part blooms in spring
 * @property flowerCoveringType The covering type to use for blooming leaves
 * @property dropFlowerCoveringType The covering type to use for dropped bloom flowers
 * @property hasFruit Whether this tree will develop fruit in its yearly life cycle
 * @property fruitCoveringType The covering type used to visualize fruit on the tree
 * @property dropFlowerCoveringType The covering type used to visualize dropped fruit
 */
@Serializable
data class TreeType(
        val identifier: String,
        @SerialName("display_name") val displayName: String,
        val description: String = "",
        @SerialName("parts") val placeholderDefinition: PlaceholderDefinition,
        @SerialName("structure_types") val structureTypes: List<String>,
        @SerialName("does_bloom") val doesBloom: Boolean = false,
        @SerialName("flowers") val flowerCoveringType: String = "",
        @SerialName("dropped_flowers") val dropFlowerCoveringType: String = "",
        @SerialName("has_fruit") val hasFruit: Boolean = false,
        @SerialName("fruits") val fruitCoveringType: String = "",
        @SerialName("dropped_fruits") val dropFruitCoveringType: String = ""
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