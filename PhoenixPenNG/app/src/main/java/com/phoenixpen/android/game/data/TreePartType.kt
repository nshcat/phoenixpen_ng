package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.DrawInfo
import com.phoenixpen.android.game.core.WeightedList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for a tree part, which is a structure.
 *
 * @property basicData Basic structure information, like identifier and description
 * @property tile Information about how to draw this tree part
 * @property variedTile Whether this tree part has multiple different tile representations
 * @property tiles Weighted list of tile representations. Only used if [variedTile] is set to true.
 */
@Serializable
data class TreePartType(
        @SerialName("basic_data") val basicData: StructureType,
        val tile: DrawInfo,
        @SerialName("varied_tile") val variedTile: Boolean = false,
        val tiles: WeightedList<DrawInfo> = WeightedList(listOf())
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