package com.phoenixpen.game.core

/**
 * Interface for observers based on a values of type [T]. Is used to implement the observer pattern.
 *
 * @param T The type of value that is observed
 */
interface Observer<T>
{
    /**
     * Notify this observer of a new or changed value.
     *
     * @param value New value
     */
    fun notify(value: T)
}