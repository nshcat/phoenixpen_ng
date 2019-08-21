package com.phoenixpen.game.input

import com.phoenixpen.game.ascii.Rectangle

/**
 * Area touch mapping that creates [EnumEvent] instances as input events.
 *
 * @param E Enumeration type to use
 *
 * @property value The enumeration value to emit as part of an [EnumEvent] instance when mapping fires
 * @param type Requested touch tap type
 * @param area Rectangular area to check touch input for
 */
class EnumAreaTouchMapping<E>(val value: E, area: Rectangle, type: TouchTapType = TouchTapType.SingleTap)
    : AreaTouchMapping(area, type)
{
    /**
     * Create enumeration input event based on stored enum [value]
     *
     * @return [EnumEvent] instance
     */
    override fun createEvent(): InputEvent
    {
        return EnumEvent(this.value)
    }
}