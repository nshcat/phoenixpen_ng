package com.phoenixpen.android.rendering

import android.opengl.GLES30

/**
 * A class encapsulating the main screen as a render target
 */
class ScreenTarget: RenderTarget()
{
    /**
     * Begin rendering to screen
     */
    override fun beginRender()
    {
        // Bind default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)

        // Do viewport application
        super.beginRender()

        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Clear the screen, both color and depth buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
    }

    /**
     * Stop rendering to screen
     */
    override fun endRender()
    {
        // No action required
    }
}