package com.phoenixpen.android.application

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.SystemClock
import com.phoenixpen.game.input.InputProvider
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * An abstract class intended as base class for all OpenGL live wallpaper applications.
 * It supplies screen, input and render events that the applications can react to.
 * This class is for Android only!
 *
 * @property context The current android app context
 * @property input The current input provider
 */
abstract class Application (val context: Context, val input: InputProvider): GLSurfaceView.Renderer
{
    /**
     * The resource provider used in different parts of the game
     */
    val resources = AndroidResourceProvider(context)

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
     * FPS counter
     */
    var fpsCounter = FpsCounter()

    /**
     * The current dimensions of the screen
     */
    protected var screenDimensions: com.phoenixpen.game.ascii.ScreenDimensions = com.phoenixpen.game.ascii.ScreenDimensions.empty()

    /**
     * This method is called any time the device screen properties change.
     * Normally, this entails a viewport change on part of the application.
     *
     * @param screenDimensions The new screen dimensions
     */
    abstract fun onScreenChanged(screenDimensions: com.phoenixpen.game.ascii.ScreenDimensions)

    /**
     * This method is called every time the app is forced to recreate the drawing surface, e.g.
     * after the user switched back to the main screen after using an app. This requires the OpenGL
     * state to be recreated from scratch, as if the app was restarted completely.
     */
    abstract fun onScreenCreated()

    /**
     * Called every time the application is requested to draw another frame. Normally this
     * also entails an update of the simulation by the given amount of elapsed seconds.
     *
     * @param elapsedSeconds The number of seconds elapsed since the last frame
     */
    abstract fun onFrame(elapsedSeconds: Double)


    //region Implementation of the OpenGL surface renderer interface

    /**
     * Implement frame elapsed time computation and delegati#on to implementation of [onFrame]
     */
    override fun onDrawFrame(p0: GL10?)
    {
        // Special logic is required if this is the frame. We cannot calculate a meaningful
        // delta time
        if(this.isFirstFrame)
        {
            this.isFirstFrame = false

            // Record last frame time anyways for following frames
            this.lastFrameTime = SystemClock.uptimeMillis()

            // Do nothing
            return
        }
        else
        {
            // We do have a last frame time. Retrieve that
            val lastTime = this.lastFrameTime

            // Determine current time to calculate delta, and at the same time
            // save it for following frames
            this.lastFrameTime = SystemClock.uptimeMillis()

            // Calculate delta in milliseconds. The integral time values are in milliseconds
            val deltaTime = this.lastFrameTime - lastTime
            val deltaTimeDouble: Double = (deltaTime).toDouble()

            // Update FPS counter
            this.fpsCounter.addSample(deltaTime.toInt())

            // Delegate to application implementation
            this.onFrame(deltaTimeDouble)
        }
    }

    /**
     * Delegate to [onScreenCreated] to notify the application of the (re)creation of the
     * wallpaper surface
     */
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?)
    {
        this.onScreenCreated()
    }

    /**
     * Delegate to [onScreenChanged] to notify the application of any possible screen size changes
     */
    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int)
    {
        // Create screen dimensions instance
        val dimensions = com.phoenixpen.game.ascii.ScreenDimensions(width, height)

        // Only apply if they are not empty
        if(!dimensions.isEmpty())
        {
            this.screenDimensions = dimensions
            this.onScreenChanged(this.screenDimensions)
        }
    }

    //endregion
}