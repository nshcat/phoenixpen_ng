package com.phoenixpen.desktop.application

import com.jogamp.opengl.*
import com.jogamp.opengl.awt.GLCanvas
import com.phoenixpen.desktop.rendering.*
import com.phoenixpen.desktop.rendering.materials.AsciiScreenMaterial
import com.phoenixpen.game.ascii.MainScene
import com.phoenixpen.game.ascii.Scene
import com.phoenixpen.game.ascii.ScreenDimensions
import java.awt.Dimension
import java.nio.file.Paths
import javax.swing.JFrame

/**
 * The main desktop application class. Manages OpenGL rendering and rendering.
 *
 * @property dimensions The window dimensions
 */
class DesktopApplication(val dimensions: Dimension): JFrame("phoenixpen_ng"), GLEventListener
{
    /**
     * The resource provider for our desktop application
     */
    private val resourceProvider = DesktopResourceProvider(Paths.get("").toAbsolutePath().resolve("desktop_resources"))

    /**
     * The desktop input provider
     */
    private val inputProvider = DesktopInputProvider()

    /**
     * The desktop logger
     */
    private val logger = DesktopLogger()

    /**
     * A texture render target as our first render pass
     */
    private lateinit var firstPass: TextureTarget

    /**
     * The screen as our second and final render pass
     */
    private lateinit var secondPass: ScreenTarget

    /**
     * The glyph matrix used to render the game scene to
     */
    private lateinit var screen: DesktopScreen

    /**
     * A full screen quad we use to render the scene to screen in the second pass
     */
    private lateinit var fullscreenQuad: FullscreenQuad

    /**
     * The currently active ASCII game scene
     */
    private lateinit var scene: Scene

    /**
     * Our orthographic projection. It causes the y-axis to be flipped, making (0,0) the top left
     * corner and origin of our coordinate system.
     */
    private var orthoProjection = OrthographicProjection()

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

    /**
     * Retrieve screen dimensions from drawable reference
     */
    private fun retrieveDimensions(drawable: GLAutoDrawable?): ScreenDimensions
    {
        val width = drawable?.surfaceWidth ?: throw IllegalStateException("Failed to retrieve screen dimensions")
        val height = drawable.surfaceHeight

        return ScreenDimensions(width, height)
    }

    /**
     * Initialize all rendering components
     */
    override fun init(drawable: GLAutoDrawable?)
    {
        // Retrieve context
        val gl = this.retrieveContext(drawable)

        // Print debug information to console
        this.printOpenGLVersion(gl)

        println("${drawable?.surfaceWidth}x${drawable?.surfaceHeight}")

        // Determine screen dimensions
        val dimensions = this.retrieveDimensions(drawable)

        // Update projection
        this.orthoProjection.refresh(dimensions)

        // Init all components
        this.firstPass = TextureTarget(gl)
        this.secondPass = ScreenTarget(gl)
        this.screen = DesktopScreen(gl, resourceProvider, dimensions)
        this.screen.resize(dimensions)
        this.fullscreenQuad = FullscreenQuad(gl, resourceProvider)

        this.firstPass.updateDimensions(dimensions)
        this.secondPass.updateDimensions(dimensions)

        this.scene = MainScene(this.resourceProvider, this.inputProvider, this.logger, this.screen.size)
    }

    override fun dispose(drawable: GLAutoDrawable?)
    {

    }

    override fun reshape(drawable: GLAutoDrawable?, x: Int, y: Int, width: Int, height: Int)
    {

    }

    override fun display(drawable: GLAutoDrawable?)
    {
        // Update the scene
        // this.scene.update(...)

        // Retrieve GL4 context
        val gl = this.retrieveContext(drawable)

        // Clear the screen
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT or GL4.GL_DEPTH_BUFFER_BIT)

        /*// Begin rendering to texture
        this.firstPass.beginRender()

            this.screen.clear()

            // Draw current scene to screen
            this.scene.render(this.screen)

            // Render the ASCII matrix to device screen
            this.screen.render(this.orthoProjection.toRenderParams())

        // We are done with the first pass
        this.firstPass.endRender()

        // Now do the second rendering pass where we render to a full screen quad
        this.secondPass.beginRender()

            // Use the texture the first pass rendered to. The full screen quad material
            // expects it to be bound to the first texture unit.
            this.firstPass.texture.use(TextureUnit.Unit0)

            // Render the fullscreen quad. The material doesnt use any of the rendering parameters,
            // so we just pass an empty instance here.
            this.fullscreenQuad.render(RenderParams.empty())

        // Finish rendering
        this.secondPass.endRender()*/


        // Now do the second rendering pass where we render to a full screen quad
        this.secondPass.beginRender()

            //
            this.screen.clear()

            // Draw current scene to screen
            this.scene.render(this.screen)

            // Render the ASCII matrix to device screen
            this.screen.render(this.orthoProjection.toRenderParams())

        // Finish rendering
        this.secondPass.endRender()

        gl.glFlush()
    }
}