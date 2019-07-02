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
 */
@Serializable
data class SeasonConfiguration(
        @SerialName("spring_duration") val springDuration: Int = 1200,
        @SerialName("summer_duration") val summerDuration: Int = 1200,
        @SerialName("autumn_duration") val autumnDuration: Int = 1200,
        @SerialName("winter_duration") val winterDuration: Int = 1200,
        @SerialName("leaf_drop_start") val leafDropStart: Double = 0.5
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