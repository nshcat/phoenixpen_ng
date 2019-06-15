package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.DrawInfo
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.ascii.TileInstance
import com.phoenixpen.game.core.Updateable

/**
 * An instance of a simple structure type - a structure that has a fixed graphical representation
 * and no logic attached to it
 */
class MapDecoration(val type: MapDecorationType, position: Position3D, val tileInstance: TileInstance): Structure(type.basicData, position), Updateable
{
    /**
     * Update tile instance, which might contain an animation
     */
    override fun update(elapsedTicks: Int)
    {
        this.tileInstance.update(elapsedTicks)
    }

    /**
     * The structure is drawn using the draw information supplied in the type class instance.
     */
    override fun tile(fancyMode: Boolean): DrawInfo
    {
        return this.type.tile.tile(this.tileInstance)
    }

    companion object
    {
        /**
         * Create anew map decoration instance based on given type and position.
         */
        fun create(type: MapDecorationType, position: Position3D, animationOffset: Int = 0): MapDecoration
        {
            return MapDecoration(type, position, type.tile.createInstance(animationOffset))
        }
    }
}