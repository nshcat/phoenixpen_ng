package com.phoenixpen.android.game.ascii

import kotlinx.serialization.Serializable

/**
 * A class representing an animation made up of single tile entries and an animation speed, in ticks.
 *
 * @property speed The animation speed, which is the duration of a single frame in ticks.
 * @property frames All frames that make up this animation
 */
@Serializable
class Animation(
        val speed: Int,
        val frames: List<DrawInfo>
)
{
    /**
     * Retrieve frame at given frame index
     */
    fun frameAt(frameIdx: Int): DrawInfo
    {
        if(frameIdx < this.frames.size)
            return this.frames[frameIdx]
        else
            throw IllegalArgumentException("Animation: frame index out of range")
    }

    /**
     * Retrieve frame at given frame index which can be out of range.
     */
    fun frameAtRaw(frameIdx: Int): DrawInfo
    {
        return this.frameAt(frameIdx % this.frames.size)
    }

    /**
     * Retrieve next frame index, given a previous index
     */
    fun nextIndex(frameIdx: Int): Int
    {
        return (frameIdx + 1) % this.frames.size
    }

    /**
     * Check whether this animation is empty
     */
    fun isEmpty(): Boolean = this.frames.isEmpty()

    /**
     * Check whether this animation is not empty
     */
    fun isNotEmpty(): Boolean = this.frames.isNotEmpty()

    companion object
    {
        /**
         * Empty animation
         */
        fun empty() = Animation(0, listOf())
    }
}