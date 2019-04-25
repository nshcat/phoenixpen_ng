package com.phoenixpen.android.livewallpaper

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.application.Application
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.rendering.*
import com.phoenixpen.android.rendering.materials.FullscreenQuadMaterial

class TestApplication (context: Context): Application(context)
{
    /**
     * A texture render target as our first render pass
     */
    private lateinit var firstPass: TextureTarget

    /**
     * The screen as our second render pass
     */
    private lateinit var secondPass: ScreenTarget

    /**
     * A texture object used to test the Texture2D class.
     */
    private lateinit var testTexture2D: Texture2D

    /**
     * A full screen quad we use to render the scene to screen in the second pass
     */
    private lateinit var fullscreenQuad: FullscreenQuad

    /**
     * A white full screen quad, for testing purposes
     */
    private lateinit var whiteQuad: FullscreenQuad

    /**
     * Our orthographic projection
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
        }
    }

    override fun onScreenCreated()
    {
        // The render target might also have OpenGL state that needs to be lazily created
        this.firstPass = TextureTarget()
        this.secondPass = ScreenTarget()

        // Create the test texture
        this.testTexture2D = Texture2D.FromImageResource(this.context, R.drawable.graphics)

        // Create the fullscreen quad
        this.fullscreenQuad = FullscreenQuad()
        this.whiteQuad = FullscreenQuad(FullscreenQuadMaterial())

    }

    override fun onFrame(elapsedSeconds: Double)
    {
        // It might happen that we are requested to draw a frame while we havent been initialized.
        // Prohibit that.
        if(!::firstPass.isInitialized)
            return

        // Begin rendering to texture
        this.firstPass.beginRender()

        // Render the white quad. We do not need any matrices for this.
        this.whiteQuad.render(RenderParams.empty())

        // Begin rendering to main screen
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