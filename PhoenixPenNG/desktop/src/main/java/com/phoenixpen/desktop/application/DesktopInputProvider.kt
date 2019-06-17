package com.phoenixpen.desktop.application

import com.phoenixpen.game.input.Direction
import com.phoenixpen.game.input.InputEvent
import com.phoenixpen.game.input.InputProvider
import com.phoenixpen.game.input.MapViewMoveEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*

/**
 * Input provider for the desktop app. Does nothing for now.
 */
class DesktopInputProvider: InputProvider, KeyAdapter()
{
    /**
     * The currently pending input events
     */
    val currentEvents: Queue<InputEvent> = ArrayDeque<InputEvent>()

    override fun hasEvents(): Boolean
    {
        return !this.currentEvents.isEmpty()
    }

    override fun peekEvents(): Iterable<InputEvent>
    {
        return this.currentEvents
    }

    override fun consumeEvents(): Iterable<InputEvent>
    {
        val elements = this.currentEvents.filter { true }

        this.currentEvents.clear()

        return elements
    }

    override fun queueEvent(event: InputEvent)
    {
        this.currentEvents.add(event)
    }

    override fun keyPressed(event: KeyEvent?)
    {
        if(event == null)
        {
            return
        }
        else
        {
            // Retrieve key code
            val keyCode = event.keyCode

            println(keyCode)

            when(keyCode)
            {
                KeyEvent.VK_PAGE_UP -> this.queueEvent(MapViewMoveEvent(Direction.Up))
                KeyEvent.VK_PAGE_DOWN -> this.queueEvent(MapViewMoveEvent(Direction.Down))
                KeyEvent.VK_LEFT -> this.queueEvent(MapViewMoveEvent(Direction.West))
                KeyEvent.VK_RIGHT -> this.queueEvent(MapViewMoveEvent(Direction.East))
                KeyEvent.VK_UP -> this.queueEvent(MapViewMoveEvent(Direction.North))
                KeyEvent.VK_DOWN -> this.queueEvent(MapViewMoveEvent(Direction.South))
            }
        }
    }
}