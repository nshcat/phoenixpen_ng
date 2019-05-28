package com.phoenixpen.android.game.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Enumeration describing the different ways a covering can be drawn
 */
enum class CoveringDrawMode
{
    /**
     * Completely cover underlying structures and map cells. This is the default.
     */
    Covering,

    /**
     * Stain existing structure or map cell glyphs. Any glyph specified in the covering type instance
     * is ignored.
     */
    Staining
}

/**
 * A class containing all information about a type of covering. This class implements the type class
 * pattern.
 *
 * @property identifier Unique identifier of this structure type
 * @property displayName Human-readable name for this structure type
 * @property description Short description text for this structure type
 */
@Serializable
data class CoveringType(
        val identifier: String,
        @SerialName("display_name") val displayName: String,
        val description: String = "",
        @SerialName("draw_mode") val drawMode: CoveringDrawMode = CoveringDrawMode.Covering
)