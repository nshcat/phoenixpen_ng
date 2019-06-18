package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.DrawInfo
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.ascii.TileInstance

/**
 * An instance of a tree part structure
 *
 * @property type The underlying tree part type class instance
 * @property position The position of this structure in the game world
 * @property tileInstance Tile instance describing how to draw this tree part
 */
class TreePart(
        val type: TreePartType,
        position: Position3D,
        val tileInstance: TileInstance)
    : Structure(type.basicData, position)
{
    /**
     * Check if this needs to be drawn
     */
    override fun shouldDraw(): Boolean
    {
        return this.type.tileType.shouldDraw(this.tileInstance)
    }

    /**
     * The structure is drawn using the draw information supplied in the type class instance.
     */
    override fun tile(fancyMode: Boolean): DrawInfo
    {
        return this.type.tileType.tile(this.tileInstance)
    }

    companion object
    {
        /**
         * Create new tree part instance with given type and position
         *
         * @param type Type class instance to use
         * @param pos Position in the game world
         * @return New tree part instance
         *
         */
        fun create(type: TreePartType, pos: Position3D): TreePart
        {
            return TreePart(type, pos, type.tileType.createInstance())
        }
    }
}