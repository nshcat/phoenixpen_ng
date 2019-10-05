package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.*
import com.phoenixpen.game.graphics.DrawInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for map decorations, which have no
 * logic attached to them.
 *
 * @property basicData Basic structure information, like identifier and description
 * @property tile Information about how to draw this structure
 */
@Serializable
data class MapDecorationType(
        @SerialName("basic_data") val basicData: StructureType,
        @Serializable(with=TileTypeSerializer::class) val tile: TileType
)
{
    companion object
    {
        /**
         * Placeholder simple structure type, used if a type is missing
         */
        val placeholder = MapDecorationType(
                StructureType(
                        "placeholder",
                        "MISSING STRUCTURE",
                        "MISSING STRUCTURE DATA",
                        PathBlockType.NonRestricted
                ),
                TileType(
                        mode = TileTypeMode.Static,
                        staticTile = DrawInfo(
                                0,
                                Color.magenta,
                                Color.magenta
                        )
                )
        )
    }
}