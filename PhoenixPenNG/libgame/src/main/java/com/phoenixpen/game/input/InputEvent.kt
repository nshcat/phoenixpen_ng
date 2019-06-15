package com.phoenixpen.game.input

/**
 * Base interface for all input events
 */
interface InputEvent

/**
 * Enumeration describing cardinal directions on a 2D plane
 */
enum class Direction
{
    North,
    South,
    West,
    East,
    Up,
    Down
}

/**
 * Move the map view in a certain direction
 */
class MapViewMoveEvent(val direction: Direction): InputEvent

