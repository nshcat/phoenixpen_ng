package com.phoenixpen.android.appmode

import android.opengl.GLSurfaceView
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A placeholder fragment containing a simple view.
 */
class OpenGLFragment: Fragment()
{
    private lateinit var glView: GLSurfaceView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        this.glView = MySurfaceView(this.activity)
        return this.glView
    }
}
