package com.phoenixpen.android.map

/**
 * An enumeration detailing the different main types of map cell states
 */
enum class MapCellState
{
    /**
     * Cell is completely filled with a material, to full height. This can, for example be, stone.
     * This also does not reveal whats behind it, so in general, nothing behind it is shown, and only
     * the border is actually rendered. Does not allow structures to be present, and no pathing through
     * the cell is possible.
     */
    Solid,

    /**
     * Cell is filled halfway with a material. This is used to model the ground floor that is lying
     * on top of a solid layer of ground material. Most commonly, this is grass. This supports
     * structures to be present.
     */
    Ground,

    /**
     * A completely empty cell.
     */
    Air
}