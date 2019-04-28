package com.phoenixpen.android.ascii

import java.util.*

/**
 * Shadow direction flag field values.
 *
 * @property nativeValue The value the ASCII shader expects. Always has exactly one bit set.
 */
enum class ShadowDirection(val nativeValue: Int)
{
    North(1 shl 8),
    West(1 shl 9),
    South(1 shl 10),
    East(1 shl 11),
    TopLeft(1 shl 12),
    TopRight(1 shl 13),
    BottomLeft(1 shl 14),
    BottomRight(1 shl 15);

    /**
     * Combine two enum values
     */
    infix fun and(other: ShadowDirection) = ShadowDirections.of(other, this)
}

/**
 * A flag field of shadow direction values.
 */
typealias ShadowDirections = EnumSet<ShadowDirection>

/**
 * Check if a shadow direction flag field contains all of given shadow directions
 */
infix fun ShadowDirections.allOf(other: ShadowDirections) = this.containsAll(other)

/**
 * Add shadow direction to shadow directions flag field
 */
infix fun ShadowDirections.and(other: ShadowDirection) = ShadowDirections.of(other, *this.toTypedArray())

/**
 * Check if shadow direction flag field contains given shadow direction
 */
infix fun ShadowDirections.has(other: ShadowDirection) = this.contains(other)