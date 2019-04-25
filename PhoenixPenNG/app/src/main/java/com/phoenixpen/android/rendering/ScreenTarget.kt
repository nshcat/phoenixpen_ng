package com.phoenixpen.android.rendering

import android.opengl.GLES31

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
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, 0)

        // Do viewport application
        super.beginRender()

        // Enable depth testing
        GLES31.glEnable(GLES31.GL_DEPTH_TEST)

        // Clear the screen, both color and depth buffer
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT or GLES31.GL_DEPTH_BUFFER_BIT)
    }

    /**
     * Stop rendering to screen
     */
    override fun endRender()
    {
        // No action required
    }
}