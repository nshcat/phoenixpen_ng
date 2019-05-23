package com.phoenixpen.android.game.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class containing all information about a type of structure. This class implements the type class
 * pattern.
 *
 * @property identifier Unique identifier of this structure type
 * @property displayName Human-readable name for this structure type
 * @property description Short description text for this structure type
 * @property pathingType How this structure interacts with path finding
 */
@Serializable
data class StructureType(
        val identifier: String,
        @SerialName("display_name") val displayName: String,
        val description: String = "",
        @SerialName("pathing_type") val pathingType: PathingType
)