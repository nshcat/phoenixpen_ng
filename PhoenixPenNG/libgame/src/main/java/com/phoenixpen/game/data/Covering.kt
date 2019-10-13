package com.phoenixpen.game.data

import com.phoenixpen.game.graphics.DrawInfo
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.ascii.TileInstance
import com.phoenixpen.game.core.Updateable

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
): Updateable
{
    /**
     * For how many ticks this covering has been "alive" for
     * TODO: Base class "GameObject" that implements this for all game objects, like coverings
     *       and structures
     */
    var lifetime: Int = 0

    /**
     * Retrieve graphical representation for this covering
     *
     * @return Draw info describing this covering
     */
    fun tile(): DrawInfo
    {
        return this.type.tileType.tile(this.tileInstance)
    }

    /**
     * Update covering state based on given number of elapsed ticks
     *
     * @param elapsedTicks Elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        this.lifetime += elapsedTicks
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