package com.phoenixpen.game.events

import com.phoenixpen.game.graphics.Color

/**
 * A singular event message.
 *
 * @property source Where the event originated from, for example "Weather"
 * @property message The actual message
 * @property color The requested message text color
 */
data class EventMessage(
        val source: String,
        val message: String,
        val color: Color
)