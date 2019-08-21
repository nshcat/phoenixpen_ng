package com.phoenixpen.game.input

import java.util.*

/**
 * A class that transforms raw input into input events using input mappings
 *
 * @property inputProvider The input provider to use
 * @param mappings The initial collection of input mappings to register
 */
open class InputAdapter(val inputProvider: InputProvider, vararg mappings: InputMapping)
{
    /**
     * A list containing all the currently active events
     */
    protected val currentEvents = LinkedList<InputEvent>()

    /**
     * All registered input mappings, sorted by their weight, descending.
     */
    protected val inputMappings = PriorityQueue<InputMapping> { x, y -> y.weight() - x.weight() }

    /**
     * Initialize state
     */
    init
    {
        // Add all given input mappings to storage
        this.inputMappings.addAll(mappings)
    }

    /**
     * Register given mapping with this input adapter.
     *
     * @param mapping Input mapping instance to register with this adapter
     */
    fun addMapping(mapping: InputMapping)
    {
        this.inputMappings.add(mapping)
    }

    /**
     * Check if there currently are any pending events
     *
     * @return Flag indicating whether there currently are any pending events
     */
    fun hasEvents(): Boolean
    {
        return this.currentEvents.isNotEmpty()
    }

    /**
     * Retrieve pending events, but do not consume them
     *
     * @return Collection of all currently pending events
     */
    fun peekEvents(): Iterable<InputEvent>
    {
        return this.currentEvents
    }

    /**
     * Retrieve pending events, consuming them in the process
     *
     * @return Collection of all currently pending events
     */
    fun consumeEvents(): Iterable<InputEvent>
    {
        val events = this.currentEvents.filter{ true }
        this.currentEvents.clear()
        return events
    }

    /**
     * Register given input event as a pending event.
     *
     * @param event Event to register
     */
    protected fun queueEvent(event: InputEvent)
    {
        this.currentEvents.add(event)
    }

    /**
     * Perform logic update of this input adapter.
     * This will check all registered input mappings for fired events, and store them in an internal
     * buffer for later retrieval by user code.
     */
    fun update()
    {
        // Try to fire all registered input mappings
        for(mapping in this.inputMappings)
        {
            // Try to fire the event
            val event = mapping.tryFire(this.inputProvider)

            // If there was an event, we are done here. We do not try to fire any other events,
            // since they might be similar (Like SHIFT+W and W) and thus fire at the same time,
            // causing glitched input. Only one input per frame is enough.
            if(event.isPresent)
            {
                this.queueEvent(event.get())
                break
            }

            // Otherwise continue trying
        }
    }
}