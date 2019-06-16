package com.phoenixpen.desktop.rendering

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

/**
 * Matrix multiplication for 4x4 matrices
 */
operator fun Matrix4f.times(other: Matrix4f): Matrix4f
{
    val result = Matrix4f()

    this.mul(other, result)

    return result
}

/**
 * Swap vector direction
 */
operator fun Vector3f.unaryMinus(): Vector3f
{
    val result = Vector3f()

    this.mul(-1.0f, result)

    return result
}

/**
 * Vector subtraction
 */
operator fun Vector3f.minus(other: Vector3f): Vector3f
{
    val result = Vector3f()

    this.sub(other, result)

    return result
}

/**
 * Vector addition
 */
operator fun Vector3f.plus(other: Vector3f): Vector3f
{
    val result = Vector3f()

    this.add(other, result)

    return result
}

/**
 * Vector scalar multiplication
 */
operator fun Vector3f.times(other: Float): Vector3f
{
    val result = Vector3f()

    this.mul(other, result)

    return result
}

/**
 * Matrix vector multiplication
 */
operator fun Matrix4f.times(other: Vector3f): Vector3f
{
    // Create 4d vector in homogenous coordinates
    val vec4 = Vector4f(other, 1.0f)

    // Perform this * vec4 and store in vec4
    vec4.mul(this)

    // Discard fourth entry
    return Vector3f(vec4.x, vec4.y, vec4.z)
}