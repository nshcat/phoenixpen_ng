package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.DrawInfo
import com.phoenixpen.game.ascii.TileInstance

/**
 * A specific material instance, based on a [MaterialType]. It stores information unique to the
 * specific cell, such as which visual representation was chosen from the material type.
 *
 * @property type The corresponding material type instance
 * @property tileInstance The current tile instance
 */
data class Material(var type: MaterialType, var tileInstance: TileInstance)
{
    /**
     * Change material type to given material.
     *
     * @param type New material type
     */
    fun setMaterial(type: MaterialType)
    {
        this.type = type
        this.tileInstance = this.type.tileType.createInstance()
    }

    /**
     * Clear this material. This resets it back to air.
     */
    fun clear()
    {
        this.type = MaterialType.air
    }

    /**
     * Retrieve current graphical representation of this material instance.
     *
     * @param fancyMode Flag indicating whether game is in normal or fancy mode
     * @return Draw information instance describing graphical representation of this material
     */
    fun tile(): DrawInfo
    {
        return this.type.tileType.tile(this.tileInstance)
    }

    /**
     * Some utility methods
     */
    companion object
    {
        /**
         * Create a new material instance from given material type. The visual representation will be
         * randomly chosen.
         */
        fun create(type: MaterialType): Material
        {
            return Material(type, type.tileType.createInstance())
        }

        /**
         * An empty material instance
         */
        val empty = Material(MaterialType.air, MaterialType.air.tileType.createInstance())
    }
}