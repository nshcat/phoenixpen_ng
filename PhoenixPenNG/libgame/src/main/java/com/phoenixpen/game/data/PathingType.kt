package com.phoenixpen.game.data

/**
 * Enumeration describing what types of tiles an actor can move on
 */
enum class PathingType
{
    /**
     * Actor can only move on land
     */
    LandBound,

    /**
     * Actor can only move in water
     */
    WaterBound,

    /**
     * Actor can move on land and in the air, and also ignores half-height path blocks like bushes
     */
    Flying
}