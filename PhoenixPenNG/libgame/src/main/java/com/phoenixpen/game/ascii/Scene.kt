package com.phoenixpen.game.ascii

import com.phoenixpen.game.input.InputProvider
import com.phoenixpen.game.logging.Logger
import com.phoenixpen.game.resources.ResourceProvider

/**
 * A class representing a game scene, that can be rendered to an ASCII glyph screen
 *
 * @property resources The resource manager to retrieve game data from
 * @property input The input manager providing input events
 * @property logger The logger instance to use
 * @property dimensions The screen dimensions, in glyphs
 * @property dimensionsInPixels The screen dimensions, in pixels
 */
abstract class Scene(
        protected val resources: ResourceProvider,
        protected val input: InputProvider,
        protected val logger: Logger,
        protected val dimensions: ScreenDimensions,
        protected val dimensionsInPixels: ScreenDimensions
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