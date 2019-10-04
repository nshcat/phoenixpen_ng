package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.DrawInfo
import com.phoenixpen.game.ascii.TileType
import com.phoenixpen.game.ascii.TileTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class defining several properties of a material that can be used as primary content of a
 * map cell. This represents the type class pattern.
 *
 * TODO:
 *  - Pathing
 *
 * @property identifier The unique identifier of this material type.
 * @property description A human readable description of the material
 * @property isWater Whether this terrain is meant to be water.
 * @property tileType Graphical representation of this material
 */
@Serializable
data class MaterialType(
        val identifier: String,
        val description: String,
        @SerialName("is_water") val isWater: Boolean = false,
        @SerialName("tile") @Serializable(with=TileTypeSerializer::class) val tileType: TileType = TileType()
)
{
    /**
     * Default materials
     */
    companion object
    {
        /**
         * Placeholder material intended to be used if a material can not be found anymore. Very visible
         * to make debugging easier
         */
        val placeholder = MaterialType("placeholder", "Placeholder material", false,
                TileType(staticTile = DrawInfo(background = Color.magenta))
        )

        /**
         * Special air material that will be used for air cells. Will not be drawn.
         */
        val air = MaterialType("air", "Placeholder material for air cells")
    }
}