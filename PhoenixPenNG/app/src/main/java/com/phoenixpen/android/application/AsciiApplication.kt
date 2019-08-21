package com.phoenixpen.android.application

import android.content.Context
import com.phoenixpen.game.ascii.MainScene
import com.phoenixpen.game.ascii.Scene
import com.phoenixpen.game.input.InputProvider
import com.phoenixpen.android.rendering.*

/**
 * A class implementing all the logic needed to execute a ASCII based application.
 * This class uses scenes to separate game logic and drawing from the technical realization
 * of the game rendering.
 *
 * @property context The Android app context, used to extract resources
 */
class AsciiApplication (context: Context, input: InputProvider): Application(context, input)
{
    /**
     * Android logger instance
     */
    private var logger = AndroidLogger()

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
    private lateinit var screen: AndroidScreen

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
     * West over milliseconds that did not make up a whole simulation tick. Will be used
     * in next frame.
     */
    private var leftoverTime: Int = 0

    /**
     * How long a single tick is.
     */
    private val msPerTick: Int = 50

    override fun onScreenChanged(screenDimensions: com.phoenixpen.game.ascii.ScreenDimensions)
    {
        // Update projection state to fit new screen size
        this.orthoProjection.refresh(screenDimensions)

        if(::firstPass.isInitialized)
        {
            // Force the screen to resize
            this.firstPass.updateDimensions(screenDimensions.scaleDown(1.0f))
            this.secondPass.updateDimensions(screenDimensions)
            this.screen.resize(screenDimensions)
            this.orthoProjection.refresh(screenDimensions)

            this.scene = MainScene(this.resources, this.input, this.logger, this.screen.size)
        }
    }

    override fun onScreenCreated()
    {
        // The render target might also have OpenGL state that needs to be lazily created
        this.firstPass = TextureTarget()
        this.secondPass = ScreenTarget()

        // Create an empty screen
        this.screen = AndroidScreen(this.context, com.phoenixpen.game.ascii.ScreenDimensions.empty())

        // Create the fullscreen quad
        this.fullscreenQuad = FullscreenQuad(this.context)

    }

    /**
     * Calculate elapsed ticks since last update based on given elapsed milliseconds
     */
    private fun calculateTicks(elapsedMillis: Double): Int
    {
        // Convert to integral millisecond count and add left over time from last frame
        val milliseconds = elapsedMillis.toInt() + this.leftoverTime

        // Calculate number of elapsed ticks
        val elapsedTicks = milliseconds / this.msPerTick

        // Save new left over time
        this.leftoverTime = milliseconds % this.msPerTick

        return elapsedTicks
    }

    override fun onFrame(elapsedMillis: Double)
    {
        // It might happen that we are requested to draw a frame while we havent been initialized.
        // Prohibit that.
        if(!::firstPass.isInitialized)
            return

        this.scene.update(this.calculateTicks(elapsedMillis))

        // Begin rendering to texture
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
        this.secondPass.endRender()

        // Clear the input provider
        this.input.clear()
    }
}