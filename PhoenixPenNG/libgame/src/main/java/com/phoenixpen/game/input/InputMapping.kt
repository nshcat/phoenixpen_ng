package com.phoenixpen.game.input

import java.util.*

/**
 * Base class for all single input mappings. Input mappings are the definition for a single
 * input event.
 */
abstract class InputMapping()
{
    /**
     * Try to fire the associated input event. If all requirements are met, a new input event is returned,
     * otherwise an empty value is returned.
     *
     * @return Input event instance if fired, empty otherwise
     */
    abstract fun tryFire(input: InputProvider): Optional<InputEvent>

    /**
     * Determine the weight of this input mapping. The weight is a value used to compare the
     * 'complexity' of different input mappings in order to implement greedy matching.
     *
     * For example, this could be the number of expected unique keys in a key combination.
     *
     * @return Weight for this input mapping
     */
    abstract fun weight(): Int
}