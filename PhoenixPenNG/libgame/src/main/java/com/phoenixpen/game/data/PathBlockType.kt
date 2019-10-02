package com.phoenixpen.game.data

/**
 * An enumeration detailing how a structure or material interacts with the path finding of actors.
 */
enum class PathBlockType
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
    FullBlocking,

    /**
     * Is ignored by path finding. Is used for special structures, such as water tiles, which use a underlying
     * map tile type to set the pathing mode.
     */
    None
}