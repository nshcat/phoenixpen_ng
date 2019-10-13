package com.phoenixpen.game.data

import com.phoenixpen.game.graphics.DrawInfo
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.ascii.TileInstance
import com.phoenixpen.game.core.Updateable
import com.phoenixpen.game.simulation.LeafState

/**
 * An instance of a tree part structure
 *
 * @property type The underlying tree part type class instance
 * @property position The position of this structure in the game world
 * @property tileInstance Tile instance describing how to draw this tree part
 * @property leafTileInstance Leaf tile instance used if this part is a leaf
 * @property leafState Current leaf state, only used if this tree part is a leaf
 * @property tree The tree this part belongs to
 */
class TreePart(
        val type: TreePartType,
        position: Position3D,
        val tileInstance: TileInstance,
        val leafTileInstance: LeafTileInstance,
        var leafState: LeafState,
        var tree: Tree
)
    : Structure(type.basicData, position), Updateable
{
    /**
     * Check if this needs to be drawn
     */
    override fun shouldDraw(): Boolean
    {
        // It depends on whether this is a leaf or not
        if(this.type.isLeaves)
        {
            return this.type.leafTileType.shouldDraw(this.leafTileInstance, this.leafState)
        }
        else
        {
            return this.type.tileType.shouldDraw(this.tileInstance)
        }
    }

    /**
     * Check whether this tree part represents leaves.
     *
     * @return Flag indicating if this tree part represents leaves
     */
    fun isLeaves(): Boolean = this.type.isLeaves

    /**
     * Check whether this tree will drop its leaves.
     *
     * @return Flag indicating whether this tree part will drop its leaves
     */
    fun dropsLeaves(): Boolean = this.type.dropsLeaves

    /**
     * The structure is drawn using the draw information supplied in the type class instance.
     */
    override fun tile(fancyMode: Boolean): DrawInfo
    {
        if(this.type.isLeaves)
        {
            return this.type.leafTileType.tile(this.leafTileInstance, this.leafState)
        }
        else
        {
            return this.type.tileType.tile(this.tileInstance)
        }
    }

    /**
     * Update all stored tile instances
     */
    override fun update(elapsedTicks: Int)
    {
        this.tileInstance.update(elapsedTicks)
        this.leafTileInstance.update(elapsedTicks)
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
        fun create(type: TreePartType, pos: Position3D, tree: Tree): TreePart
        {
            return TreePart(type, pos, type.tileType.createInstance(), type.leafTileType.createInstance(), LeafState.Normal, tree)
        }
    }
}