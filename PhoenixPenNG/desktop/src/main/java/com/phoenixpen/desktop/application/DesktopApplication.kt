package com.phoenixpen.desktop.application

import com.jogamp.opengl.*
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.util.Animator
import com.phoenixpen.desktop.graphics.DesktopSurfaceManager
import com.phoenixpen.desktop.rendering.*
import com.phoenixpen.game.ascii.MainScene
import com.phoenixpen.game.ascii.Scene
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.game.settings.AppSettings
import java.awt.Dimension
import java.nio.file.Paths
import javax.swing.JFrame


/**
 * The main desktop application class. Manages OpenGL rendering and rendering.
 *
 * @property dimensions The window dimensions
 */
class DesktopApplication(val dimensions: Dimension, desktopMode: Boolean = false): JFrame("phoenixpen_ng"), GLEventListener
{
    /**
     * The animator instance that renders our scene
     */
    private var animator: Animator

    /**
     * The last frame time, in milliseconds. This is used to calculate the delta time supplied
     * to [onFrame].
     */
    private var lastFrameTime: Long = 0

    /**
     * Whether the next frame is the first frame. This is important since division by zero
     * has to be avoided when calculating the delta time for the call to [onFrame].
     */
    private var isFirstFrame: Boolean = true

    /**
     * West over milliseconds that did not make up a whole simulation tick. Will be used
     * in next frame.
     */
    private var leftoverTime: Int = 0

    /**
     * How long a single tick is.
     */
    private val msPerTick: Int = 50

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
     * The surface manager holding all surfaces
     */
    private lateinit var surfaceManager: DesktopSurfaceManager

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

        canvas.addKeyListener(this.inputProvider)

        // Add canvas to frame
        this.contentPane.add(canvas)

        // Setup window
        this.addKeyListener(this.inputProvider)
        this.size = this.dimensions
        this.setLocationRelativeTo(null)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        isResizable = false

        // If desktop mode was requested, use XLib to move window to the background
        if(desktopMode)
        {
            X11DesktopModeHelper.setToDesktopMode(this)
        }

        // Create animator
        this.animator = Animator(canvas)
        this.animator.start()



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
     * Calculate elapsed ticks since last update based on given elapsed milliseconds
     */
    private fun calculateTicks(): Int
    {
        // Special logic is required if this is the frame. We cannot calculate a meaningful
        // delta time
        if(this.isFirstFrame)
        {
            this.isFirstFrame = false

            // Record last frame time anyways for following frames
            this.lastFrameTime = System.currentTimeMillis()

            // No ticks elapsed in first frame
            return 0
        }
        else
        {
            // We do have a last frame time. Retrieve that
            val lastTime = this.lastFrameTime

            // Determine current time to calculate delta, and at the same time
            // save it for following frames
            this.lastFrameTime = System.currentTimeMillis()

            // Calculate delta in milliseconds. The integral time values are in milliseconds
            val deltaTime = this.lastFrameTime - lastTime

            // Convert to integral millisecond count and add left over time from last frame
            val milliseconds = (deltaTime + this.leftoverTime).toInt()

            // Calculate number of elapsed ticks
            val elapsedTicks = milliseconds / this.msPerTick

            // Save new left over time
            this.leftoverTime = milliseconds % this.msPerTick

            return elapsedTicks
        }
    }

    /**
     * Initialize all rendering components
     */
    override fun init(drawable: GLAutoDrawable?)
    {
        // Retrieve context
        val gl = this.retrieveContext(drawable)

        gl.glEnable(GL.GL_BLEND)
        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA)

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
        this.surfaceManager = DesktopSurfaceManager(gl, this.resourceProvider, ScreenDimensions(this.dimensions.width, this.dimensions.height))
        this.fullscreenQuad = FullscreenQuad(gl, this.resourceProvider)

        this.firstPass.updateDimensions(dimensions)
        this.secondPass.updateDimensions(dimensions)

        this.scene = MainScene(this.resourceProvider, this.inputProvider, this.logger, this.surfaceManager, AppSettings())
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
        this.scene.update(this.calculateTicks())

        // Retrieve GL4 context
        val gl = this.retrieveContext(drawable)

        // Clear the screen
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT or GL4.GL_DEPTH_BUFFER_BIT)

        // Render everything to screen
        this.secondPass.beginRender()

            // Clear the screen
            this.surfaceManager.clearAll()

            // Draw current scene. This will cause the scene to write data to various surfaces
            // it created.
            this.scene.render()

            // Now render all surfaces to the window
            this.surfaceManager.render(this.orthoProjection.toRenderParams())

        // Finish rendering
        this.secondPass.endRender()

        gl.glFlush()

        // Clear the input provider
        this.inputProvider.clear()
    }
}