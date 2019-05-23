package com.phoenixpen.android.game.core

/**
 * An interface for game objects that carry some sort of logic state with them which can be updated
 * by supplying an amount of elapsed logic ticks
 */
interface Updateable
{
    /**
     * Update the state of this game object
     */
    fun update(elapsedTicks: Int)
}