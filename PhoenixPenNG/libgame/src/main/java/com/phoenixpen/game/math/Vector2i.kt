package com.phoenixpen.game.math

/**
 * A simple, two dimensional vector of integral values.
 *
 * @property x The x component of this vector
 * @property y The y component of this vector
 */
class Vector2i(var x: Int = 0, var y: Int = 0)
{
    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector2i

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int
    {
        val prime = 31
        var result = 1
        result = prime * result + x
        result = prime * result + y
        return result
    }

    /**
     * Additional helper instances
     */
    companion object
    {
        /**
         * A vector describing the direction "north"
         */
        val north = Vector2i(0, -1)

        /**
         * A vector describing the direction "south"
         */
        val south = Vector2i(0, 1)

        /**
         * A vector describing the direction "west"
         */
        val west = Vector2i(-1, 0)

        /**
         * A vector describing the direction "east"
         */
        val east = Vector2i(1, 0)
    }
}