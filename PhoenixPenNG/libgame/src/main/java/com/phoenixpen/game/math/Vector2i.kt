package com.phoenixpen.game.math

/**
 * A simple, two dimensional vector of integral values.
 *
 * @property x The x component of this vector
 * @property y The y component of this vector
 */
class Vector2i(var x: Int, var y: Int)
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
}