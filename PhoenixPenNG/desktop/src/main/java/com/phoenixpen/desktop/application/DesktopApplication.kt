package com.phoenixpen.desktop.application

import com.jogamp.opengl.*
import com.jogamp.opengl.awt.GLCanvas
import java.awt.Dimension
import javax.swing.JFrame

/**
 * The main desktop application class. Manages OpenGL rendering and rendering.
 *
 * @property dimensions The window dimensions
 */
class DesktopApplication(val dimensions: Dimension): JFrame("phoenixpen_ng"), GLEventListener
{
    /**
     * Initialize OpenGL context and window
     */
    init
    {
        // Initialize OpenGL
        val profile = GLProfile.get(GLProfile.GL4)
        val capabilities = GLCapabilities(profile)

        // Create GL canvas
        val canvas = GLCanvas(capabilities)
        canvas.addGLEventListener(this)

        // Add canvas to frame
        this.contentPane.add(canvas)

        // Setup window
        this.size = this.dimensions
        this.setLocationRelativeTo(null)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        isResizable = false
        canvas.requestFocusInWindow()
    }

    /**
     * Print debug information about the currently active OpenGL version
     *
     * @param gl The current OpenGL context
     */
    private fun printOpenGLVersion(gl: GL4)
    {
        println("OpenGL ${gl.glGetString(GL4.GL_VERSION)}")
    }

    /**
     * Safely retrieve OpenGL 4.x context object from given drawable event arguments
     *
     * @param Drawable event args to extract GL context from
     * @return Extracted GL context
     */
    private fun retrieveContext(drawable: GLAutoDrawable?): GL4
    {
        // Retrieve GL object
        val gl = drawable?.gl
        val gl4 = gl?.gL4

        return gl4 ?: throw IllegalStateException("Could not retrieve OpenGL 4 context")
    }


    override fun init(drawable: GLAutoDrawable?)
    {
        // Retrieve context
        val gl = this.retrieveContext(drawable)

        // Print debug information to console
        this.printOpenGLVersion(gl)
    }

    override fun dispose(drawable: GLAutoDrawable?)
    {

    }

    override fun reshape(drawable: GLAutoDrawable?, x: Int, y: Int, width: Int, height: Int)
    {

    }

    override fun display(drawable: GLAutoDrawable?)
    {
        // Retrieve GL4 context
        val gl = this.retrieveContext(drawable)

        gl.glClear(GL4.GL_COLOR_BUFFER_BIT or GL4.GL_DEPTH_BUFFER_BIT)

        gl.glFlush()
    }
}