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
 * @property weather The weather configuration for all four seasons
 */
@Serializable
data class BiomeConfiguration(
        @SerialName("still_water_freezes") val stillWaterFreezes: Boolean = true,
        @SerialName("moving_water_freezes") val movingWaterFreezes: Boolean = false,
        @SerialName("ocean_freezes") val oceanFreezes: Boolean = false,
        @SerialName("snow") val snowEnabled: Boolean = true,
        @SerialName("generation") val generationInfo: BiomeGenerationConfiguration,
        val weather: WeatherConfig
)

/**
 * Class holding all IDs used to generate a map based on a biome.
 *
 * @property mapInfoId ID of the map type key
 * @property mapLayerIds IDs of all template layers used for terrain generation
 * @property decorationInfoId ID of the decoration type key
 * @property decorationLayerIds IDs of all decoration template layers
 * @property waterInfoId ID of the water type key
 * @property waterLayerIds IDs of all water template layers
 * @property treeInfoId ID of the tree type key
 * @property treeLayerIds IDs of all tree template layers
 */
@Serializable
data class BiomeGenerationConfiguration(
        @SerialName("terrain_info") val mapInfoId: String,
        @SerialName("terrain_layers") val mapLayerIds: List<String>,

        @SerialName("decoration_info") val decorationInfoId: String,
        @SerialName("decoration_layers") val decorationLayerIds: List<String>,

        @SerialName("water_info") val waterInfoId: String,
        @SerialName("water_layers") val waterLayerIds: List<String>,

        @SerialName("tree_info") val treeInfoId: String,
        @SerialName("tree_layers") val treeLayerIds: List<String>
)
