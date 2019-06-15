package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.android.application.AsciiApplication
import com.phoenixpen.game.input.*
import java.util.*

class SurfaceViewInputProvider: InputProvider
{
    /**
     * The currently pending input events
     */
    val currentEvents: Queue<InputEvent> = ArrayDeque<InputEvent>()

    override fun hasEvents(): Boolean
    {
        return !this.currentEvents.isEmpty()
    }

    override fun peekEvents(): Iterable<InputEvent>
    {
        return this.currentEvents
    }

    override fun consumeEvents(): Iterable<InputEvent>
    {
        val elements = this.currentEvents.filter { true }

        this.currentEvents.clear()

        return elements
    }

    override fun queueEvent(event: InputEvent)
    {
        this.currentEvents.add(event)
    }

}

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    val inputProvider = SurfaceViewInputProvider()

    init
    {
        this.setOnTouchListener(::handleTouchEvent)

        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        setRenderer(AsciiApplication(ctx, this.inputProvider))
    }

    private fun handleTouchEvent(v: View, m: MotionEvent): Boolean
    {
        if(v.width == 0 || v.height == 0)
            return false

        when (m.actionMasked)
        {
            MotionEvent.ACTION_DOWN ->
            {
                Log.d("INPUT", "Found ACTION_DOWN")

                val screenDim = com.phoenixpen.game.ascii.ScreenDimensions(v.width, v.height)
                val pos = Position(m.rawX.toInt(), m.rawY.toInt())

                if(pos.y < screenDim.height/4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.North))
                    Log.d("INPUT", "Emitted NORTH")
                }
                else if(pos.y >= screenDim.height - screenDim.height/4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.South))
                    Log.d("INPUT", "Emitted SOUTH")
                }
                else if(pos.x <= screenDim.width / 4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.West))
                    Log.d("INPUT", "Emitted WEST")
                }
                else if(pos.x >= screenDim.width - screenDim.width/4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.East))
                    Log.d("INPUT", "Emitted EAST")
                }
                else if(pos.x >= screenDim.width/4 && pos.x <= (screenDim.width - screenDim.width/4))
                {
                    if(pos.y <= screenDim.height/2)
                    {
                        this.inputProvider.queueEvent(MapViewMoveEvent(Direction.Up))
                        Log.d("INPUT", "Emitted UP")
                    }
                    else
                    {
                        this.inputProvider.queueEvent(MapViewMoveEvent(Direction.Down))
                        Log.d("INPUT", "Emitted DOWN")
                    }
                }
            }
        }

        return true
    }
}