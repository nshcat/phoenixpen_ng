package com.phoenixpen.game.input

/**
 * Input provider implementation that does nothing.
 */
class NullInputProvider: InputProvider
{
    override fun queueEvent(event: InputEvent)
    {
    }

    override fun hasEvents(): Boolean
    {
        return false
    }

    override fun peekEvents(): Iterable<InputEvent>
    {
        return listOf()
    }

    override fun consumeEvents(): Iterable<InputEvent>
    {
        return listOf()
    }
}