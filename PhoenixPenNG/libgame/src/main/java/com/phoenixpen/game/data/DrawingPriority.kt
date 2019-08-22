package com.phoenixpen.game.data

/**
 * Enumeration describing how high of a drawing priority a particular covering type has.
 * This is useful to force certain kinds of coverings (like rain drops) to be always drawn instead
 * of other coverings that might already be in place at a given position.
 */
enum class DrawingPriority
{
    /**
     * Very high drawing priority. Should be used for coverings that are very short term, such as
     * rain drops.
     */
    VeryHigh,

    /**
     * High drawing priority. Should be used for coverings that are short term, such as snow or blood
     * coverings.
     */
    High,

    /**
     * Normal drawing priority. This should be used for coverings that are long term, such as fruits
     * or flowers on trees
     */
    Normal
}