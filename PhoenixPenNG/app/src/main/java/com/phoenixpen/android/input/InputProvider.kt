package com.phoenixpen.android.input

/**
 * Interface for classes that emit input events for the game logic to process and react on
 */
interface InputProvider
{
    /**
     * Check if there currently are any pending events
     *
     * @return Flag indicating whether there currently are any pending events
     */
    fun hasEvents(): Boolean

    /**
     * Retrieve pending events, but do not consume them
     *
     * @return Collection of all currently pending events
     */
    fun peekEvents(): Iterable<InputEvent>

    /**
     * Retrieve pending events, consuming them in the process
     *
     * @return Collection of all currently pending events
     */
    fun consumeEvents(): Iterable<InputEvent>

    /**
     * Register given input event as a pending event.
     *
     * @param event Event to register
     */
    fun queueEvent(event: InputEvent)
}