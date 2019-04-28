package com.phoenixpen.android.ascii

import org.joml.Vector2i

/**
 * A simple, two dimensional integral position
 */
typealias Position = Vector2i

fun Position.unaryMinus(): Position
{
    return Position(-this.x, -this.y)
}

fun Position.plus(rhs: Position): Position
{
    return Position(this.x + rhs.x, this.y + rhs.y)
}

fun Position.minus(rhs: Position): Position
{
    return Position(this.x - rhs.x, this.y - rhs.y)
}

fun Position.times(factor: Float): Position
{
    return Position((this.x.toFloat() * factor).toInt(), (this.y.toFloat() * factor).toInt())
}