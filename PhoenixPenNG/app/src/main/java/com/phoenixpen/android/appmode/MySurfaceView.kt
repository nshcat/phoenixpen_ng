package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.phoenixpen.android.application.AsciiApplication
import com.phoenixpen.android.ascii.Color
import com.phoenixpen.android.ascii.DrawInfo
import com.phoenixpen.android.data.MaterialInfo
import com.phoenixpen.android.data.MaterialType
import com.phoenixpen.android.map.MapCellState
import com.phoenixpen.android.utility.WeightedList
import com.phoenixpen.android.utility.WeightedPair
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
class Test(val test: WeightedList<Color>)

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    init
    {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        //setRenderer(AsciiApplication(ctx))

        val info = DrawInfo(139, Color.red, Color.green)
        val json = Json.indented.stringify(DrawInfo.serializer(), info)

        val info2 = Json.indented.parse(DrawInfo.serializer(), json)

        Log.d("nya", "\n$json")
        Log.d("nya", "\n$info2")
    }
}