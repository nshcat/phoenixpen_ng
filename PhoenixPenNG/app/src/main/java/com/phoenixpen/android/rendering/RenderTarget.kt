package com.phoenixpen.android.rendering

import android.opengl.GLES31
import com.phoenixpen.game.ascii.ScreenDimensions

/**
 * An entity that can be rendered to using OpenGL, for example the screen framebuffer or a render
 * target texture
 */
abstract class RenderTarget
{
    /**
     * The current dimensions of the render target. Used to set the OpenGL viewport before rendering.
     */
    protected var renderDimensions: com.phoenixpen.game.ascii.ScreenDimensions = com.phoenixpen.game.ascii.ScreenDimensions.empty()

    /**
     * Force the render target to change its dimensions
     *
     * @param renderDimensions The new dimensions of the render target
     */
    open fun updateDimensions(renderDimensions: com.phoenixpen.game.ascii.ScreenDimensions)
    {
        this.renderDimensions = renderDimensions
    }

    /**
     * Begin rendering to this target. All following render calls will result in this render
     * target being used.
     */
    open fun beginRender()
    {
        // Since there might be any number of render targets in use, we need to always
        // set the OpenGL viewport before rendering
        GLES31.glViewport(0, 0, this.renderDimensions.width, this.renderDimensions.height)
    }

    /**
     * Stop rendering to this target.
     */
    abstract fun endRender()
}

