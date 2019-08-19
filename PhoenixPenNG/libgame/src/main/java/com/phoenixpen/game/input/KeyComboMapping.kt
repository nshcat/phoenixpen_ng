package com.phoenixpen.game.input

import java.util.*
import kotlin.collections.ArrayList

/**
 * An input mapping based on a key combination. A key combination might contain multiple [Key] and [Modifier]
 * instances.
 *
 * @param components A collection of both [Key] and [Modifier] instances
 */
abstract class KeyComboMapping(vararg components: Any): InputMapping()
{
    /**
     * All keys associated with this mapping
     */
    protected val keys = ArrayList<Key>()

    /**
     * All modifiers associated with this mapping
     */
    protected val modifiers = ArrayList<Modifier>()

    /**
     * Initialize state
     */
    init
    {
        // Go through all given key/modifiers. Since [Any] is used here, this might result in
        // illegal objects being supplied.
        for(entry in components)
        {
            // Check for the actual type of this entry
            when(entry)
            {
                is Key ->
                {
                    if(!this.keys.contains(entry))
                        this.keys.add(entry)
                }
                is Modifier ->
                {
                    if(!this.modifiers.contains(entry))
                        this.modifiers.add(entry)
                }
                else ->
                {
                    throw IllegalArgumentException("KeyComboMapping: Only Key and Modifier instances are allowed as constructor arguments!")
                }
            }
        }

        // It has to be at least one key
        if(this.keys.size <= 0)
            throw IllegalArgumentException("KeyComboMapping: At least one key is required")
    }

    /**
     * Try to fire the associated input event. If all requirements are met, a new input event is returned,
     * otherwise an empty value is returned.
     *
     * @return Input event instance if fired, empty otherwise
     */
    override fun tryFire(input: InputProvider): Optional<InputEvent>
    {
        // All keys and all modifiers have to be pressed
        if(this.keys.all { key -> input.isKeyDown(key) } &&
                this.modifiers.all { mod -> input.isKeyModifierDown(mod) })
        {
            return Optional.of(this.createEvent())
        }
        else // At least one key or modifier wasnt pressed. This even can not fire.
        {
            return Optional.empty()
        }
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
        // The weight is just the number of keys and modifiers combined
        return this.keys.size + this.modifiers.size
    }

    /**
     * Actually create the input event for this key combo. This is supposed to be overridden by
     * sub classes according to the needs of the game system associated with the input adapter this
     * mapping belongs to.
     */
    abstract fun createEvent(): InputEvent
}