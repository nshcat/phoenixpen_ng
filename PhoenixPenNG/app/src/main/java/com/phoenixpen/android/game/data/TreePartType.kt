package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.DrawInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for a tree part, which is a structure.
 *
 * @property basicData Basic structure information, like identifier and description
 * @property tile Information about how to draw this tree part
 */
@Serializable
data class TreePartType(
        @SerialName("basic_data") val basicData: StructureType,
        val tile: DrawInfo
)
{
    companion object
    {
        /**
         * Placeholder tree part type, used if a type is missing
         */
        val placeholder = TreePartType(
                StructureType(
                        "placeholder",
                        "MISSING STRUCTURE",
                        "MISSING STRUCTURE DATA",
                        PathingType.NonRestricted
                ),
                DrawInfo(
                        0,
                        Color.green,
                        Color.green
                )
        )
    }
}