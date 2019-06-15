package com.phoenixpen.android.game.ascii

import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.input.InputProvider
import com.phoenixpen.android.resources.ResourceProvider

/**
 * A class representing a game scene, that can be rendered to an ASCII glyph screen
 *
 * @property resources The resource manager to retrieve game data from
 * @property input The input manager providing input events
 * @property dimensions The screen dimensions
 */
abstract class Scene(
        protected val resources: ResourceProvider,
        protected val input: InputProvider,
        protected val dimensions: ScreenDimensions
)
{
    /**
     * Render the scene to given ASCII screen.
     *
     * @param screen Screen to render ASCII glyphs to.
     */
    abstract fun render(screen: Screen)

    /**
     * Update scene logic, given an amount of elapsed ticks since last update
     *
     * @param elapsedTicks Ticks elapsed since last update
     */
    abstract fun update(elapsedTicks: Int)
}