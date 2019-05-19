package com.phoenixpen.android.map

import com.phoenixpen.android.ascii.DrawInfo
import com.phoenixpen.android.data.MaterialType

/**
 * A single cell in the three dimensional map.
 *
 * @property state Current cell state.
 * @property material Current cell material.
 */
data class MapCell(
        var state: MapCellState,
        var material: Material
)
{
    /**
     * Check if this cell should be drawn to screen.
     *
     * @return Flag indicating whether this cell should be drawn.
     */
    fun isTransparent(): Boolean
    {
        return this.state == MapCellState.Air
    }

    /**
     * Retrieve current graphical representation of this map cell. This can not be done if [isTransparent]
     * is true.
     *
     * @param fancyMode Flag indicating whether game is in normal or fancy mode
     * @return Draw information instance describing graphical representation of this cell
     */
    fun tile(fancyMode: Boolean = true): DrawInfo
    {
        if(this.isTransparent())
            throw IllegalStateException("Can't retrieve graphical representation of transparent cell")

        return this.material.tile()
    }

    companion object
    {
        /**
         * Empty map cell. Contains air.
         */
        //val empty = MapCell(MapCellState.Air, Material.empty.copy())
        fun empty(): MapCell
        {
            return MapCell(MapCellState.Air, Material.empty.copy())
        }
    }
}