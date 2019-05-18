package com.phoenixpen.android.map

import com.phoenixpen.android.data.MaterialInfo

/**
 * A single cell in the three dimensional map.
 *
 * @property state Current cell state.
 * @property material Current cell material. It's type has to fit the current state.
 */
class MapCell(var state: MapCellState, var material: MaterialInfo)
{
}