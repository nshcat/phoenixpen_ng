package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.graphics.DrawInfo
import com.phoenixpen.game.ascii.TileType
import com.phoenixpen.game.ascii.TileTypeSerializer
import com.phoenixpen.game.core.WeightedTileList
import com.phoenixpen.game.core.WeightedTileListSerializer
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
 * @property bodyType The type of this body of water, e.g. river
 * @property salinity The salt contents of this water tile
 * @property foamChance The chance that a water tile of this tile will be replaced by a foam effect selected from [foamTiles]
 * @property foamDuration How long a foam tile will stay displayed
 * @property foamTiles A collection of possible foam tiles
 */
@Serializable
data class WaterType(
        @SerialName("basic_data") val basicData: StructureType,
        @SerialName("tile") @Serializable(with = TileTypeSerializer::class) val tileType: TileType = TileType(),
        @SerialName("body_type") val bodyType: WaterBodyType = WaterBodyType.Still,
        val salinity: WaterSalinity = WaterSalinity.Freshwater,
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
                        PathBlockType.NonRestricted
                ),
                TileType(
                        staticTile = DrawInfo(
                                background = Color.blue
                        )
                )
        )
    }
}