package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import com.phoenixpen.android.game.ascii.ColorSerializer
import com.phoenixpen.android.game.core.AsciiApplication
import com.phoenixpen.android.game.data.PlaceholderDefinition
import com.phoenixpen.android.game.data.PlaceholderDefinitionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class Test(@Serializable(with= PlaceholderDefinitionSerializer::class) val def: PlaceholderDefinition)

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    init
    {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        //setRenderer(AsciiApplication(ctx))

        val test = Json.parse(Test.serializer(), "{ \"def\": { \"trunk\": \"test\" } }")

        android.os.Debug.waitForDebugger()

        val x = 0
    }
}