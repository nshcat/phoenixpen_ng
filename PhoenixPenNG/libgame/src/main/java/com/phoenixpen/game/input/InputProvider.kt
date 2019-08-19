package com.phoenixpen.game.input


/**
 * Interface for classes that provide raw input to the game.
 */
interface InputProvider
{
    /**
     * Check whether given key is currently pressed.
     *
     * @param key Key to check
     * @return Flag indicating whether key is currently pressed
     */
    fun isKeyDown(key: Key): Boolean

    /**
     * Check whether given modifier was pressed as part of a key stroke
     *
     * @param modifier Key modifer to check
     * @return Flag indicating whether modifer key was pressed
     */
    fun isKeyModifierDown(modifier: Modifier): Boolean

    // TODO hasTouchInput, getTouchInputs ... mouse...
}