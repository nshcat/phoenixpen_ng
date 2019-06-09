package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.DrawInfo
import com.phoenixpen.android.game.ascii.TileType
import com.phoenixpen.android.game.ascii.TileTypeSerializer
import com.phoenixpen.android.game.core.WeightedTileList
import com.phoenixpen.android.game.core.WeightedTileListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An enumeration describing the different types of water bodies supported by the game.
 */
enum class WaterBodyType
{
    /**
     * A moving river
     */
    River,

    /**
     * A large ocean
     */
    Ocean,

    /**
     * Any stationary body of water that is not an ocean, e.g. a lake or a little puddle of stagnant
     * water
     */
    Still
}

/**
 * An enumeration describing the salt content of a body of water.
 */
enum class WaterSalinity
{
    /**
     * Unconsumable salt water.
     */
    Saltwater,

    /**
     * Water with drinkable levels of salt in it.
     */
    Freshwater
}

/**
 * Type class describing a water type, which can be for rivers, bodies of water and the ocean.
 *
 * @property basicData Basic structure data
 * @property tileType Water tile information. Flowing water effect is implemented by animation frame offsets
 * @property foamChance The chance that a water tile of this tile will be replaced by a foam effect selected from [foamTiles]
 * @property foamDuration How long a foam tile will stay displayed
 * @property foamTiles A collection of possible foam tiles
 */
@Serializable
data class WaterType(
        @SerialName("basic_data") val basicData: StructureType,
        @SerialName("tile") @Serializable(with = TileTypeSerializer::class) val tileType: TileType = TileType(),
        @SerialName("foam_chance") val foamChance: Double = 0.0,
        @SerialName("foam_duration") val foamDuration: Int = 1,
        @SerialName("foam_tiles") @Serializable(with = WeightedTileListSerializer::class) val foamTiles: WeightedTileList = WeightedTileList(listOf())
)
{
    companion object
    {
        /**
         * A placeholder water type used when data is missing
         */
        val placeholder = WaterType(
                StructureType(
                        "placeholder",
                        "MISSING STRUCTURE",
                        "MISSING STRUCTURE DATA",
                        PathingType.NonRestricted
                ),
                TileType(
                        staticTile = DrawInfo(
                                background = Color.blue
                        )
                )
        )
    }
}