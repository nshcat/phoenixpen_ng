package com.phoenixpen.android.game.ascii

/**
 * An interface describing a component of a scene that does receive logic/state updates and can
 * be rendered to an ascii screen.
 */
interface SceneComponent
{
    /**
     * Update scene based on given amount of elapsed ticks
     *
     * @param elapsedTicks Amount of elapsed ticks since last update
     */
    fun update(elapsedTicks: Int)

    /**
     * Render component to screen
     *
     * @param screen Screen to render component to
     */
    fun render(screen: Screen)
}