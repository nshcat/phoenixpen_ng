package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.phoenixpen.android.game.core.AsciiApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlinx.serialization.map
import kotlinx.serialization.serializer

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    init
    {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        //setRenderer(AsciiApplication(ctx))

        val test = HashMap<String, Int>()

        test.put("test", 3)
        test.put("test2", 4)
        test.put("meow", 11)

        val serializer = (String.serializer() to Int.serializer()).map

        val json = Json.indented.toJson(serializer, test).toString()

        Log.d("JSON", json)
    }
}