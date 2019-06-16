package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4

/**
 * A class encapsulating the main screen as a render target
 */
class ScreenTarget(gl: GL4): RenderTarget(gl)
{
    /**
     * Begin rendering to screen
     */
    override fun beginRender()
    {
        // Bind default framebuffer
        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0)

        // Do viewport application
        super.beginRender()

        // Enable depth testing
        gl.glEnable(GL4.GL_DEPTH_TEST)

        // Clear the screen, both color and depth buffer
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT or GL4.GL_DEPTH_BUFFER_BIT)
    }

    /**
     * Stop rendering to screen
     */
    override fun endRender()
    {
        // No action required
    }
}