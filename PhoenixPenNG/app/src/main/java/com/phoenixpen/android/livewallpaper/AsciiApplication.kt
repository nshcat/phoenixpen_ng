package com.phoenixpen.android.livewallpaper

import android.content.Context
import com.phoenixpen.android.application.Application
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.Scene
import com.phoenixpen.android.ascii.Screen
import com.phoenixpen.android.ascii.TestScene
import com.phoenixpen.android.rendering.*


class AsciiApplication (context: Context): Application(context)
{
    /**
     * The currently active scene. This will receive updates and get rendered
     * to the screen.
     */
    private var scene: Scene = TestScene()

    /**
     * The screen our scenes are going to draw to.
     */
    private lateinit var screen: Screen

    /**
     * The device screen as our only render pass
     */
    private lateinit var renderPass: ScreenTarget

    /**
     * The projection we are using to render the ASCII screen
     */
    private lateinit var projection: OrthographicProjection

    override fun onScreenChanged(screenDimensions: ScreenDimensions)
    {
        if(::renderPass.isInitialized)
        {
            // Force the screen to resize
            this.renderPass.updateDimensions(screenDimensions.scaleDown(1.0f))
            this.screen.resize(screenDimensions)

            // Update projection state to fit new screen size
            this.projection.refresh(screenDimensions)
        }
    }

    override fun onScreenCreated()
    {
        // The render target might also have OpenGL state that needs to be lazily created
        this.renderPass = ScreenTarget()

        this.screen = Screen(this.context, ScreenDimensions.empty())

        this.projection = OrthographicProjection()

    }

    override fun onFrame(elapsedSeconds: Double)
    {
        // It might happen that we are requested to draw a frame while we havent been initialized.
        // Prohibit that.
        if(!::renderPass.isInitialized)
            return

        // TODO: update scene, calculate ticks

        // Begin rendering
        this.renderPass.beginRender()

            // Make sure the screen is clear
            this.screen.clear()

            // Draw current scene to screen
            this.scene.render(this.screen)

            // Display screen to device screen
            this.screen.render(this.projection.toRenderParams())

        // Begin rendering to main screen
        this.renderPass.endRender()
    }
}