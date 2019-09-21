package com.phoenixpen.game.data.biome

import com.phoenixpen.game.math.NormalDistribution
import com.phoenixpen.game.simulation.Season
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Weather data for a single season.
 *
 * @property rainDistribution Normal distribution that controls rain probability and length. The usage
 * of the included probability field is advised.
 */
@Serializable
data class WeatherData(
        @SerialName("rain_distribution") val rainDistribution: NormalDistribution
)


/**
 * Weather configuration for a biome. Determines probabilities and other properties
 * of various weather effects for each of the four seasons.
 */
@Serializable
data class WeatherConfig(
        val spring: WeatherData,
        val summer: WeatherData,
        val autumn: WeatherData,
        val winter: WeatherData
)
{
    /**
     * Retrieve weather data for given season
     *
     * @param season The season to retrieve weather data for
     * @return Weather data for given season
     * @return Weather data for given season
     */
    fun dataFor(season: Season): WeatherData = when(season) {
        Season.Spring -> this.spring
        Season.Summer -> this.summer
        Season.Autumn -> this.autumn
        Season.Winter -> this.winter
    }
}