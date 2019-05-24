package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.DrawInfo
import com.phoenixpen.android.game.ascii.Position3D

/**
 * An instance of a tree part structure
 */
class TreePartStructure(val type: TreePartType, position: Position3D): Structure(type.basicData, position)
{
    /**
     * The structure is drawn using the draw information supplied in the type class instance.
     */
    override fun tile(fancyMode: Boolean): DrawInfo
    {
        return this.type.tile
    }
}