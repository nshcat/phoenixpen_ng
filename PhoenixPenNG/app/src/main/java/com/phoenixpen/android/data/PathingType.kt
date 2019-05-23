package com.phoenixpen.android.data

/**
 * An enumeration detailing how a structure or material interacts with the path finding of actors.
 */
enum class PathingType
{
    /**
     * A material or structure not passable by land-bound actors
     */
    Water,

    /**
     * No restrictions at all
     */
    NonRestricted,

    /**
     * Blocks every actor other than flying ones
     */
    HalfBlocking,

    /**
     * Blocks all pathing
     */
    FullBlocking
}