package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.game.ascii.Position
import com.phoenixpen.android.game.core.AsciiApplication
import com.phoenixpen.android.input.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlinx.serialization.map
import kotlinx.serialization.serializer
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

                val screenDim = ScreenDimensions(v.width, v.height)
                val pos = Position(m.rawX.toInt(), m.rawY.toInt())

                if(pos.y < screenDim.height/4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.Up))
                    Log.d("INPUT", "Emitted UP")
                }
                else if(pos.y >= screenDim.height - screenDim.height/4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.Down))
                    Log.d("INPUT", "Emitted DOWN")
                }
                else if(pos.x <= screenDim.width / 4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.Left))
                    Log.d("INPUT", "Emitted LEFT")
                }
                else if(pos.x >= screenDim.width - screenDim.width/4)
                {
                    this.inputProvider.queueEvent(MapViewMoveEvent(Direction.Right))
                    Log.d("INPUT", "Emitted RIGHT")
                }
            }
        }

        return true
    }
}