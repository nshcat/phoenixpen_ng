package com.phoenixpen.game.data

import com.phoenixpen.game.simulation.Season
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing info about the seasons
 *
 * @property springDuration Duration of spring, in ticks
 * @property summerDuration Duration of summer, in ticks
 * @property autumnDuration Duration of autumn, in ticks
 * @property winterDuration Duration of winter, in ticks
 * @property leafDropStart Percentage of autumn when leaves begin to fall to the ground
 * @property bloomStart Percentage of spring when leaves begin to bloom
 * @property flowerCleanupStart Percentage of summer when dropped flowers are cleaned up
 * @property fruitOnTreeStart Percentage of summer when fruit spawns on trees
 * @property fruitDropStart Percentage of summer when fruit is dropped
 * @property leafCleanupStart Percentage of autumn when dropped leaves are cleaned up
 * @property fruitCleanupStart Percentage of autumn when dropped fruit is removed
 */
@Serializable
data class SeasonConfiguration(
        @SerialName("spring_duration") val springDuration: Int = 1200,
        @SerialName("summer_duration") val summerDuration: Int = 1200,
        @SerialName("autumn_duration") val autumnDuration: Int = 1200,
        @SerialName("winter_duration") val winterDuration: Int = 1200,
        @SerialName("leaf_drop_start") val leafDropStart: Double = 0.5,
        @SerialName("leaf_cleanup_start") val leafCleanupStart: Double = 0.9,
        @SerialName("bloom_start") val bloomStart: Double = 0.75,
        @SerialName("flower_cleanup_start") val flowerCleanupStart: Double = 0.10,
        @SerialName("fruit_start") val fruitOnTreeStart: Double = 0.65,
        @SerialName("drop_fruit_start") val fruitDropStart: Double = 0.85,
        @SerialName("dropped_fruit_cleanup_start") val fruitCleanupStart: Double = 0.85
)
{
    /**
     * Retrieve season duration for given season.
     *
     * @param season Season to look up duration for
     * @return Duration of given season
     */
    fun durationOf(season: Season): Int = when (season)
    {
        Season.Spring -> this.springDuration
        Season.Summer -> this.summerDuration
        Season.Autumn -> this.autumnDuration
        Season.Winter -> this.winterDuration
    }
}