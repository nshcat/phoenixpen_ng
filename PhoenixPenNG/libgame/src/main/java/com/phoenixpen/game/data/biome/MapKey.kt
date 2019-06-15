package com.phoenixpen.game.data.biome

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.map.MapCellState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An entry in the map key, containing both the map cell state and the material.
 *
 * @property cellType The cell state
 * @property material The material identifier
 */
@Serializable
data class MapKeyEntry(
        @SerialName("cell_type") val cellType: MapCellState = MapCellState.Ground,
        val material: String
)

/**
 * A typealias used to implement the map key, which can be used to look up the colors in the
 * map template layer images
 */
typealias MapKey = HashMap<Color, MapKeyEntry>