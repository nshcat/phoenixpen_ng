package com.phoenixpen.android.map

import com.phoenixpen.android.data.MaterialType

/**
 * A single cell in the three dimensional map.
 *
 * @property state Current cell state.
 * @property material Current cell material. It's type has to fit the current state.
 */
data class MapCell(
        var state: MapCellState,
        var material: MaterialType
)
{
    companion object
    {
        /**
         * Empty map cell. Contains air.
         */
        val empty = MapCell(MapCellState.Air, MaterialType.air)
    }
}