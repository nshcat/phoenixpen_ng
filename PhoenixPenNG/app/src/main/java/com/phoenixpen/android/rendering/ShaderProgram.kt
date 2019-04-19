package com.phoenixpen.android.rendering

import android.opengl.GLES30
import android.util.Log

/**
 * A class abstracting the concept of an OpenGL shader program
 */
class ShaderProgram (vararg shaders: Shader)
{
    /**
     * The native OpenGL handle to this shader program object
     */
    var handle: Int = GLES30.GL_NONE
        protected set

    /**
     * The program link log. In the case of failed program linkage this will
     * contain any error message emitted by the program linker. Warnings are also stored
     * in here.
     */
    var log: String = ""
        protected set

    /**
     * Init program object and attach and link shaders
     */
    init
    {
        // At least one shader is needed
        if(shaders.isEmpty())
            throw IllegalArgumentException("At least one shader object is required")

        // Create new program object
        this.handle = GLES30.glCreateProgram()

        // Check for any failure
        if(this.handle == GLES30.GL_NONE)
        {
            // We cant recover from this
            Log.e("ShaderProgram", "Could not create program object");
            throw IllegalStateException()
        }

        // Attach all shaders
        for(shader in shaders)
        {
            this.attachShader(shader)
        }

        // Link them all together
        this.link()
    }

    /**
     * Instruct the OpenGL runtime to use this program for rendering
     */
    fun use()
    {
        GLES30.glUseProgram(this.handle)
    }

    /**
     * Apply given render parameters (mode, view and projection matrices) to this shader program.
     * Note that it needs to be in use, otherwise this operation has no effect.
     *
     * @param rp Render parameters to apply to this shader program
     */
    fun applyParameters(rp: RenderParams)
    {
        this.use()
        uniformMat4f(this, "projection", rp.projection)
        uniformMat4f(this, "view", rp.view)
        uniformMat4f(this, "model", rp.model)
        uniformMat3f(this, "normalTransform", rp.normalTransform)
    }

    /**
     * Attach given shader to this program object.
     */
    protected fun attachShader(shader: Shader)
    {
        GLES30.glAttachShader(this.handle, shader.handle)
    }

    /**
     * Link all attached shaders into the program object
     */
    protected fun link()
    {
        // Attempt to link program
        GLES30.glLinkProgram(this.handle)

        // Retrieve linkage log
        this.log = GLES30.glGetProgramInfoLog(this.handle)

        // Retrieve link status
        val status = IntArray(1)
        GLES30.glGetProgramiv(this.handle, GLES30.GL_LINK_STATUS, status, 0)

        // Check for failure
        if(status[0] == GLES30.GL_FALSE)
        {
            Log.e("ShaderProgram", "Error compiling program: " + this.log)
            throw IllegalStateException("failed to link shader program")
        }
    }
}