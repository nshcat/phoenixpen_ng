package com.phoenixpen.game.input

/**
 * Input provider implementation that does nothing.
 */
class NullInputProvider: InputProvider
{
    override fun isKeyDown(key: Key): Boolean
    {
        return false
    }

    override fun isKeyModifierDown(modifier: Modifier): Boolean
    {
        return false
    }

    override fun hasText(): Boolean
    {
        return false
    }

    override fun text(): String
    {
         throw IllegalStateException("text() called with no active input text")
    }

    override fun hasTouchInput(): Boolean
    {
        return false
    }

    override fun getTouchInput(): Iterable<TouchInput>
    {
        throw IllegalStateException("getTouchInput() called with no active touch input")
    }

    override fun clear()
    {
        // Do nothing
    }
}