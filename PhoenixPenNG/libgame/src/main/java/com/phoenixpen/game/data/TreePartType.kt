package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.DrawInfo
import com.phoenixpen.game.ascii.TileType
import com.phoenixpen.game.ascii.TileTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for a tree part, which is a structure.
 *
 * @property basicData Basic structure information, like identifier and description
 * @property tileType Graphical representation of this tree part
 * @property isLeaves Whether this tree part is a type of leaf, like primary or secondary leaves
 */
@Serializable
data class TreePartType(
        @SerialName("basic_data") val basicData: StructureType,
        @SerialName("tile") @Serializable(with=TileTypeSerializer::class) val tileType: TileType = TileType(),
        @SerialName("is_leaves") val isLeaves: Boolean = false
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