package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import com.phoenixpen.android.application.AsciiApplication

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    init
    {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        setRenderer(AsciiApplication(ctx))
    }
}