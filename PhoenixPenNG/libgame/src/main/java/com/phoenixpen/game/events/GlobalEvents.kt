package com.phoenixpen.game.events

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.core.Observable

/**
 * Class managing the creation and distribution of in-game event messages, which can have different
 * source categories. It is observable, which means that other components can subscribe in order to be
 * notified of new events.
 */
object GlobalEvents: Observable<EventMessage>()
{
    /**
     * Create and post new event message
     *
     * @param source The source string describing where the event message originated from
     * @param message The event message contents
     * @param color The requested color of the event message text
     */
    fun postEvent(source: String, message: String, color: Color = Color.white)
    {
        this.notifyAll(EventMessage(source, message, color))
    }
}