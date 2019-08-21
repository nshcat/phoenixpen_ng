package com.phoenixpen.game.input

/**
 * A key combo input mapping that emits a [EnumEvent] with a given constant enumeration value.
 */
class EnumKeyComboMapping<E>(private val eventValue: E, vararg components: Any):
    KeyComboMapping(*components)
{
    /**
     * Create event instance
     *
     * @return New event instance
     */
    override fun createEvent(): InputEvent
    {
        return EnumEvent(this.eventValue)
    }
}