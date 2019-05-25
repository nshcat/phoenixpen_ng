package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.DrawInfo
import com.phoenixpen.android.game.ascii.Position3D

/**
 * An instance of a tree part structure
 */
class TreePartStructure(val type: TreePartType, position: Position3D): Structure(type.basicData, position)
{
    /**
     * The tile that was chosen from varied tile array, if varied tiles are enabled for the type
     * of this tree part.
     */
    var chosenTile = DrawInfo()

    init
    {
        // Do we need to draw varied tiles?
        if(this.type.variedTile)
        {
            // Select a tile from the weighted list
            this.chosenTile = this.type.tiles.drawElement()
        }
    }

    /**
     * The structure is drawn using the draw information supplied in the type class instance.
     */
    override fun tile(fancyMode: Boolean): DrawInfo
    {
        if(this.type.variedTile)
            return this.chosenTile
        else return this.type.tile
    }
}