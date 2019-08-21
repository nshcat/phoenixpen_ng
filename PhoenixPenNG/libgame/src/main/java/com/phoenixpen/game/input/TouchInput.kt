package com.phoenixpen.game.input

import com.phoenixpen.game.ascii.Position

/**
 * Base interface for all touch input types
 */
interface TouchInput

/**
 * Enumeration describing the different types of touch input taps
 */
enum class TouchTapType
{
    /**
     * A single tap
     */
    SingleTap,

    /**
     * A double tap
     */
    DoubleTap
}

/**
 * Touch input consisting of a type of touch tap, be it singular, double or other
 *
 * @property position The screen position the touch event occurred at, in pixels
 * @property type The type of this touch tap input
 */
data class TouchTapInput(val position: Position, val type: TouchTapType): TouchInput