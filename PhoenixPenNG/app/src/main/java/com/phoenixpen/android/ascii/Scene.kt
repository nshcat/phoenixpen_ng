package com.phoenixpen.android.ascii

import android.content.Context
import com.phoenixpen.android.application.ScreenDimensions

/**
 * A class representing a game scene, that can be rendered to an ASCII glyph screen
 */
abstract class Scene(protected val context: Context, protected val dimensions: ScreenDimensions)
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
    abstract fun update(elapsedTicks: Long)
}