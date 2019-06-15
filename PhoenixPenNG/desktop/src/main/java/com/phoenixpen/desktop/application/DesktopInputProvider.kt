package com.phoenixpen.desktop.application

import com.phoenixpen.game.input.InputEvent
import com.phoenixpen.game.input.InputProvider

/**
 * Input provider for the desktop app. Does nothing for now.
 */
class DesktopInputProvider: InputProvider
{
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

    override fun queueEvent(event: InputEvent)
    {
        return
    }
}