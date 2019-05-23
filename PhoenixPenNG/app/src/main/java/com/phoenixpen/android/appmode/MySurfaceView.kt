package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.phoenixpen.android.R
import com.phoenixpen.android.application.AsciiApplication
import com.phoenixpen.android.ascii.Color
import com.phoenixpen.android.ascii.DrawInfo
import com.phoenixpen.android.data.MaterialManager
import com.phoenixpen.android.data.MaterialType
import com.phoenixpen.android.map.MapCellState
import com.phoenixpen.android.utility.WeightedList
import com.phoenixpen.android.utility.WeightedPair
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    init
    {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        setRenderer(AsciiApplication(ctx))
    }
}