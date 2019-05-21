package com.phoenixpen.android.data

import com.phoenixpen.android.ascii.Color
import com.phoenixpen.android.ascii.DrawInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class containing all information about a type of structure. This class implements the type class
 * pattern.
 *
 * @property identifier Unique identifier of this structure type
 * @property displayName Human-readable name for this structure type
 * @property description Short description text for this structure type
 * @property tile Draw info for this structure
 * @property tileFancy Draw info for this structure, fancy graphics mode
 */
@Serializable
data class StructureType(
        val identifier: String,
        @SerialName("display_name") val displayName: String,
        val description: String = "",
        val tile: DrawInfo = DrawInfo(),
        @SerialName("tile_fancy") val tileFancy: DrawInfo = DrawInfo()
)
{
    companion object
    {
        /**
         * Placeholder structure type used in place of missing types
         */
        val placeholder: StructureType = StructureType(
            "placeholder",
                "MISSING STRUCTURE",
                "MISSING STRUCTURE TYPE",
                DrawInfo(0, Color.red, Color.red),
                DrawInfo(0, Color.red, Color.red)
        )
    }
}