package com.phoenixpen.game.input

import com.phoenixpen.game.ascii.Rectangle
import java.util.*

/**
 * An input mapping that reacts to touch tap input in a given rectangular area of the screen
 *
 * @property type The tap type required, i.e. singular or double tap
 * @property area The rectangular area to test touch input for
 */
abstract class AreaTouchMapping(val type: TouchTapType, val area: Rectangle): InputMapping()
{
    /**
     * Try to fire the associated input event. If all requirements are met, a new input event is returned,
     * otherwise an empty value is returned.
     *
     * @return Input event instance if fired, empty otherwise
     */
    override fun tryFire(input: InputProvider): Optional<InputEvent>
    {
        // Do we have any active touch inputs?
        if(input.hasTouchInput())
        {
            // Go through all touch inputs to see if there are any tap inputs
            for(touchInput in input.getTouchInput())
            {
                // The touch input needs to be a tap and of correct tap type
                if(touchInput is TouchTapInput && touchInput.type == this.type)
                {
                    // Retrieve touch position
                    val touchPos = touchInput.position

                    // Perform hit check. If successful, create actual input event and return.
                    if(this.area.isInside(touchPos))
                    {
                        return Optional.of(this.createEvent())
                    }
                }
            }
        }

        // This event cant fire.
        return Optional.empty()
    }

    /**
     * Determine the weight of this input mapping. The weight is a value used to compare the
     * 'complexity' of different input mappings in order to implement greedy matching.
     *
     * For example, this could be the number of expected unique keys in a key combination.
     *
     * @return Weight for this input mapping
     */
    override fun weight(): Int
    {
        // We assign a fairly low weight to touch events.
        return 1
    }

    /**
     * Actually create the input event for this area touch mapping. This is supposed to be overridden by
     * sub classes according to the needs of the game system associated with the input adapter this
     * mapping belongs to.
     */
    protected abstract fun createEvent(): InputEvent
}