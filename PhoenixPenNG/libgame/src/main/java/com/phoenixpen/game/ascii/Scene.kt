package com.phoenixpen.game.ascii

import com.phoenixpen.game.graphics.SurfaceManager
import com.phoenixpen.game.input.InputProvider
import com.phoenixpen.game.logging.Logger
import com.phoenixpen.game.resources.ResourceProvider
import com.phoenixpen.game.settings.AppSettings

/**
 * A class representing a game scene, that can be rendered to an ASCII glyph screen
 *
 * @property resources The resource manager to retrieve game data from
 * @property input The input manager providing input events
 * @property logger The logger instance to use
 * @property surfaceManager The surface manager to use for surface creation
 * @property settings The application settings
 */
abstract class Scene(
        protected val resources: ResourceProvider,
        protected val input: InputProvider,
        protected val logger: Logger,
        protected val surfaceManager: SurfaceManager,
        protected val settings: AppSettings
)
{
    /**
     * Render the scene. The Scene is expected to create at least one surface to render to.
     */
    abstract fun render()

    /**
     * Update scene logic, given an amount of elapsed ticks since last update
     *
     * @param elapsedTicks Ticks elapsed since last update
     */
    abstract fun update(elapsedTicks: Int)

    /**
     * Called whenever the display dimensions or orientation change, i.e. when on android on screen
     * tilt. The scene has to consider all registered surfaces to be dropped and recreate them.
     */
    abstract fun reshape()
}