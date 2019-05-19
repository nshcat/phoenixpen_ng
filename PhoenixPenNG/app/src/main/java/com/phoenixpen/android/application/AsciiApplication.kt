package com.phoenixpen.android.application

import android.content.Context
import com.phoenixpen.android.ascii.Scene
import com.phoenixpen.android.ascii.Screen
import com.phoenixpen.android.ascii.testscenes.MapTestScene
import com.phoenixpen.android.ascii.testscenes.TestScene
import com.phoenixpen.android.rendering.*

/**
 * A class implementing all the logic needed to execute a ASCII based application.
 * This class uses scenes to separate game logic and drawing from the technical realization
 * of the game rendering.
 *
 * @property context The Android app context, used to extract resources
 */
class AsciiApplication (context: Context): Application(context)
{
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
    private lateinit var screen: Screen

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

    override fun onScreenChanged(screenDimensions: ScreenDimensions)
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

            this.scene = MapTestScene(this.context, this.screen.size)
        }
    }

    override fun onScreenCreated()
    {
        // The render target might also have OpenGL state that needs to be lazily created
        this.firstPass = TextureTarget()
        this.secondPass = ScreenTarget()

        // Create an empty screen
        this.screen = Screen(this.context, ScreenDimensions.empty())

        // Create the fullscreen quad
        this.fullscreenQuad = FullscreenQuad(this.context)

    }

    override fun onFrame(elapsedSeconds: Double)
    {
        // It might happen that we are requested to draw a frame while we havent been initialized.
        // Prohibit that.
        if(!::firstPass.isInitialized)
            return

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
    }
}