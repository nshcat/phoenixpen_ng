package com.phoenixpen.android.game.ascii

import com.phoenixpen.android.application.ScreenDimensions
import org.joml.Vector2i
import org.joml.Vector3i

/**
 * A simple, two dimensional integral position
 */
typealias Position = Vector2i

/**
 * A simple, three dimensional integral position
 */
typealias Position3D = Vector3i

operator fun Position.unaryMinus(): Position
{
    return Position(-this.x, -this.y)
}

/**
 * Extract x and z components of given 3d position
 */
fun Position3D.xz(): Position
{
    return Position(this.x, this.z)
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

operator fun Position3D.unaryMinus(): Position3D
{
    return Position3D(-this.x, -this.y, -this.z)
}

operator fun Position3D.plus(rhs: Position3D): Position3D
{
    return Position3D(this.x + rhs.x, this.y + rhs.y, this.z + rhs.z)
}

operator fun Position3D.minus(rhs: Position3D): Position3D
{
    return Position3D(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z)
}

/**
 * Convert a screen dimensions instance to a position.
 */
fun ScreenDimensions.toPosition(): Position
{
    return Position(this.width, this.height)
}