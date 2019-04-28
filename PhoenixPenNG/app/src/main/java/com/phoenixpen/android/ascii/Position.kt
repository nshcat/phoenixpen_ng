package com.phoenixpen.android.ascii

import com.phoenixpen.android.application.ScreenDimensions
import org.joml.Vector2i

/**
 * A simple, two dimensional integral position
 */
typealias Position = Vector2i

operator fun Position.unaryMinus(): Position
{
    return Position(-this.x, -this.y)
}

operator fun Position.plus(rhs: Position): Position
{
    return Position(this.x + rhs.x, this.y + rhs.y)
}

operator fun Position.minus(rhs: Position): Position
{
    return Position(this.x - rhs.x, this.y - rhs.y)
}

operator fun Position.times(factor: Float): Position
{
    return Position((this.x.toFloat() * factor).toInt(), (this.y.toFloat() * factor).toInt())
}

operator fun Position.div(factor: Float): Position
{
    return Position((this.x.toFloat() / factor).toInt(), (this.y.toFloat() / factor).toInt())
}

/**
 * Convert a screen dimensions instance to a position.
 */
fun ScreenDimensions.toPosition(): Position
{
    return Position(this.width, this.height)
}