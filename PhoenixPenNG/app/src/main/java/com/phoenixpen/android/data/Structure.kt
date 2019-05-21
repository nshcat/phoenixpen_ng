package com.phoenixpen.android.data

import com.phoenixpen.android.ascii.DrawInfo

/**
 * A class representing a singular structure instance based on a structure type.
 * A structure is basically a static entity that is unable to move.
 */
class Structure(val type: StructureType)
{
    /**
     * Retrieve current tile draw info for this structure
     */
    fun tile(fancyMode: Boolean = true): DrawInfo
    {
        return when(fancyMode)
        {
            true -> this.type.tileFancy
            false -> this.type.tile
        }
    }
}