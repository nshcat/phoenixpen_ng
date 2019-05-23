package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.DrawInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for simple structures, which have a fixed glyph and no
 * logic attached to them.
 *
 * @property basicData Basic structure information, like identifier and description
 * @property tile Information about how to draw this structure
 */
@Serializable
data class SimpleStructureType(
        @SerialName("basic_data") val basicData: StructureType,
        val tile: DrawInfo
)
{
    companion object
    {
        /**
         * Placeholder simple structure type, used if a type is missing
         */
        val placeholder = SimpleStructureType(
                StructureType(
                        "placeholder",
                        "MISSING STRUCTURE",
                        "MISSING STRUCTURE DATA",
                        PathingType.NonRestricted
                ),
                DrawInfo(
                        0,
                        Color.magenta,
                        Color.magenta
                )
        )
    }
}