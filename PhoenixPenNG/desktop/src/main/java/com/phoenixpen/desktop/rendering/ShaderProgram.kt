package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4
import com.jogamp.opengl.glu.GLU

/**
 * A class abstracting the concept of an OpenGL shader program
 *
 * @property gl The OpenGL context
 * @param shaders The shader objects to link together
 */
class ShaderProgram (val gl: GL4, vararg shaders: Shader)
{
    /**
     * The native OpenGL handle to this shader program object
     */
    var handle: Int = GL4.GL_NONE
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
        this.handle = gl.glCreateProgram()

        // Check for any failure
        if(this.handle == GL4.GL_NONE)
        {
            // We cant recover from this
            throw IllegalStateException("Could not create program object")
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
        gl.glUseProgram(this.handle)
    }

    /**
     * Attach given shader to this program object.
     */
    protected fun attachShader(shader: Shader)
    {
        gl.glAttachShader(this.handle, shader.handle)
    }

    /**
     * Link all attached shaders into the program object
     */
    protected fun link()
    {
        // Attempt to link program
        gl.glLinkProgram(this.handle)

        // Retrieve linkage log
        //this.log = gl.glGetProgramInfoLog(this.handle)
        val lengthArray = IntArray(1)
        gl.glGetProgramiv(this.handle, GL4.GL_INFO_LOG_LENGTH, lengthArray, 0)
        val length = lengthArray[0]

        if(length > 0)
        {
            val log = ByteArray(length)
            gl.glGetProgramInfoLog(this.handle, length, lengthArray, 0, log, 0)

            this.log = String(log)
        }

        // Retrieve link status
        val status = IntArray(1)
        gl.glGetProgramiv(this.handle, GL4.GL_LINK_STATUS, status, 0)

        // Check for failure
        if(status[0] == GL4.GL_FALSE)
        {
            throw IllegalStateException("Error compiling program:  ${this.log}")
        }
    }
}