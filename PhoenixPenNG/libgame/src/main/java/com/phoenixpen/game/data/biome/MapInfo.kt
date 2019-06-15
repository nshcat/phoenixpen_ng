package com.phoenixpen.game.data.biome

import kotlinx.serialization.Serializable

/**
 * A class containing all the information needed to interpret a [BiomeTemplate] and its layers
 *
 * @property key The map key used to interpret the colors in the map template
 * @property height The total map height. Any layers not described using [BiomeTemplateLayer] instances will be filled
 * with air.
 */
@Serializable
data class MapInfo(
        val key: MapKey,
        val height: Int
)