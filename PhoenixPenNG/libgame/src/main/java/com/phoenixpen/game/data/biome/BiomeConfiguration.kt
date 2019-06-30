package com.phoenixpen.game.data.biome

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class containing configuration of a certain biome, such as average temperature, if water will
 * freeze in winter, etc..
 *
 * @property stillWaterFreezes Whether still bodies of water, such as lakes and stagnant pools, will freeze in winter
 * @property movingWaterFreezes Whether rivers will freeze in winter
 * @property oceanFreezes Whether ocean tiles will freeze in winter
 * @property snowEnabled Whether snow is enabled
 */
@Serializable
data class BiomeConfiguration(
        @SerialName("still_water_freezes") val stillWaterFreezes: Boolean = true,
        @SerialName("moving_water_freezes") val movingWaterFreezes: Boolean = false,
        @SerialName("ocean_freezes") val oceanFreezes: Boolean = false,
        @SerialName("snow") val snowEnabled: Boolean = true
)