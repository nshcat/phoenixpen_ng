package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.DrawInfo
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.ascii.TileInstance

/**
 * A covering instance as part of the game world.
 *
 * @property type The type class instance associated with this covering
 * @property position The position of this covering in the game world
 * @property tileInstance The graphical representation of this covering
 */
class Covering(
        val type: CoveringType,
        val position: Position3D,
        val tileInstance: TileInstance
)
{
    /**
     * Retrieve graphical representation for this covering
     *
     * @return Draw info describing this covering
     */
    fun tile(): DrawInfo
    {
        return this.type.tileType.tile(this.tileInstance)
    }

    companion object
    {
        /**
         * Create a new covering instance based on a type class instance and a postion in the game
         * world.
         *
         * @param type Type class instance
         * @param position Position in the game world
         * @return New covering instance
         */
        fun create(type: CoveringType, position: Position3D): Covering
        {
            return Covering(type, position, type.tileType.createInstance())
        }
    }
}