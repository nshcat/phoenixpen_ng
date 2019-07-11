package com.phoenixpen.game.simulation

/**
 * An enumeration describing the different states a leaf structure can be in
 */
enum class LeafState
{
    /**
     * Default leaf state, used in both spring and summer
     */
    Normal,

    /**
     * Discolored autumn leaves
     */
    Autumnal,

    /**
     * Branches with leaves that have been dropped
     */
    Dropped
}