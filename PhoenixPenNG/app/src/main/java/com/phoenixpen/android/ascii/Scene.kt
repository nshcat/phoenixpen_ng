package com.phoenixpen.android.ascii

/**
 * A class representing a game scene, that can be rendered to an ASCII glyph screen
 */
interface Scene
{
    /**
     * Render the scene to given ASCII screen.
     *
     * @param screen Screen to render ASCII glyphs to.
     */
    fun render(screen: Screen)

    /**
     * Update scene logic, given an amount of elapsed ticks since last update
     *
     * @param elapsedTicks Ticks elapsed since last update
     */
    fun update(elapsedTicks: Long)
}