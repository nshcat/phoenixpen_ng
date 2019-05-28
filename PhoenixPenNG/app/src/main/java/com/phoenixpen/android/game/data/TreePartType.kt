package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.DrawInfo
import com.phoenixpen.android.game.ascii.TileType
import com.phoenixpen.android.game.ascii.TileTypeSerializer
import com.phoenixpen.android.game.core.WeightedList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for a tree part, which is a structure.
 *
 * @property basicData Basic structure information, like identifier and description
 * @property tile Graphical representation of this tree part
 */
@Serializable
data class TreePartType(
        @SerialName("basic_data") val basicData: StructureType,
        @SerialName("tile") @Serializable(with=TileTypeSerializer::class) val tileType: TileType = TileType()
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
                TileType(staticTile = DrawInfo(background = Color.green))
        )
    }
}