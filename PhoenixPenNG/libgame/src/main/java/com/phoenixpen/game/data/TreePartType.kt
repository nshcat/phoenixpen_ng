package com.phoenixpen.game.data

import com.phoenixpen.game.graphics.DrawInfo
import com.phoenixpen.game.ascii.TileType
import com.phoenixpen.game.ascii.TileTypeSerializer
import com.phoenixpen.game.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for a tree part, which is a structure.
 *
 * @property basicData Basic structure information, like identifier and description
 * @property tileType Graphical representation of this tree part
 * @property isLeaves Whether this tree part is a type of leaf, like primary or secondary leaves
 * @property dropCoveringType The covering type identifier to use when dropping leaves
 * @property dropsLeaves Whether this tree part drops leaves
 * @property leafTileType Combined tile types for different leaf states. Only used if [isLeaves] is true.
 * @property isEvergreen Whether this leaf type is evergreen, meaning it doesnt brown in autumn
 */
@Serializable
data class TreePartType(
        @SerialName("basic_data") val basicData: StructureType,
        @SerialName("tile") @Serializable(with=TileTypeSerializer::class) val tileType: TileType = TileType(),
        @SerialName("is_leaves") val isLeaves: Boolean = false,
        @SerialName("leaf_tiles") val leafTileType: LeafTileType = LeafTileType(),
        @SerialName("drops_leaves") val dropsLeaves: Boolean = false,
        @SerialName("dropped_leaves") val dropCoveringType: String = "",
        @SerialName("is_evergreen") val isEvergreen: Boolean = false
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
                        PathBlockType.NonRestricted
                ),
                TileType(staticTile = DrawInfo(background = Color.green))
        )
    }
}