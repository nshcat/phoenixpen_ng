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
     * @return Flag indicating whether modifier key was pressed
     */
    fun isKeyModifierDown(modifier: Modifier): Boolean

    /**
     * Check whether there is a string of text available.
     *
     * @return Flag indicating whether there currently is text available.
     */
    fun hasText(): Boolean

    /**
     * Retrieve input as text. This is always recorded - every key press is automatically added to the
     * text buffer. The text buffer is automatically cleared after each frame.
     *
     * @return Input as text.
     */
    fun text(): String

    // TODO hasTouchInput, getTouchInputs ... mouse...
}