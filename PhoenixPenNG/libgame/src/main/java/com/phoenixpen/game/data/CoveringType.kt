package com.phoenixpen.game.data

import com.phoenixpen.game.graphics.DrawInfo
import com.phoenixpen.game.ascii.TileType
import com.phoenixpen.game.ascii.TileTypeSerializer
import com.phoenixpen.game.graphics.Color
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
 * @property drawMode How this covering should be drawn on top of other structures or map cells
 * @property tileType Graphical representation of this covering
 * @property priority Drawing priority of this covering. See [DrawingPriority] for explanation
 */
@Serializable
data class CoveringType(
        val identifier: String,
        @SerialName("display_name") val displayName: String,
        val description: String = "",
        @SerialName("draw_mode") val drawMode: CoveringDrawMode = CoveringDrawMode.Covering,
        @SerialName("tile") @Serializable(with = TileTypeSerializer::class) val tileType: TileType = TileType(),
        @SerialName("drawing_priority") val priority: DrawingPriority = DrawingPriority.Normal
)
{
    companion object
    {
        /**
         * Placeholder covering type used if a type is not available anymore
         */
        val placeholder = CoveringType(
                "placeholder",
                "MISSING COVERING",
                "MISSING COVERING TYPE",
                tileType = TileType(staticTile = DrawInfo(background = Color.red))
        )
    }
}