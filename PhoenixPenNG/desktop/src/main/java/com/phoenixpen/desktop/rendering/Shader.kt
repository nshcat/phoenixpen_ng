package com.phoenixpen.desktop.rendering

import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.GL4
import com.phoenixpen.desktop.application.DesktopResourceProvider
import java.io.IOException
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * A class abstracting the concept of an OpenGL shader object.
 *
 * @property gl The OpenGL context
 * @property type The type of this shader object, e.g. fragment shader
 */
class Shader (val gl: GL4, val type: ShaderType)
{
    /**
     * The native OpenGL handle of this shader.
     */
    var handle: Int = GL4.GL_NONE
        protected set

    /**
     * The shader compilation log. In the case of failed shader compilation this will
     * contain any error message emitted by the shader compiler. Warnings are also stored
     * in here.
     */
    var log: String = ""
        protected set

    /**
     * Shader object initialisation
     */
    init
    {
        // Create new OpenGL shader object
        this.handle = gl.glCreateShader(this.type.nativeValue)

        // Check for error
        if(this.handle == GL4.GL_NONE)
        {
            // We cant recover from this
            throw IllegalStateException("Could not create shader object")
        }
    }

    /**
     * "Static" methods that allow construction from source code string
     */
    companion object
    {
        /**
         * Create a new OpenGL shader object from given shader source code.
         *
         * @param gl The OpenGL context
         * @param type Shader type
         * @param source Shader source code as string
         * @return Compiled shader object
         */
        fun FromText(gl: GL4, type: ShaderType, source: String): Shader
        {
            // Create new shader
            var shader = Shader(gl, type)

            // Attach shader source
            shader.addSource(source)

            // Compile it
            shader.compile()

            // Shader is now fully compiled and ready for use
            return shader
        }

        /**
         * Create shader from source file in resources
         *
         * @param gl The OpenGL context
         * @param type Shader type
         * @param res Resource provider
         * @param id Resource ID
         */
        fun FromResource(gl: GL4, type: ShaderType, res: DesktopResourceProvider, id: String): Shader
        {
            try
            {
                // Read shader from resources
                return FromText(gl, type,  res.shader(id))
            }
            catch(ex: IOException)
            {
                throw IllegalStateException("Failed to load shader from resource: ${ex.message}", ex)
            }
        }
    }

    /**
     * Attach given OpenGL shader source code to this shader object
     *
     * @param source Shader source code
     */
    protected fun addSource(source: String)
    {
        // Empty source is not allowed
        if(source.isEmpty())
            throw IllegalArgumentException("Shader source cannot be empty")

        // Attach it to the shader object
        gl.glShaderSource(this.handle, 1, arrayOf(source), null)
    }

    /**
     * Compile the shader. This is only possible if source code was added first.
     */
    protected fun compile()
    {
        // Try to compile the shader
        gl.glCompileShader(this.handle)

        // Retrieve the shader compilation log. This might contain messages no matter
        // the compilation status
        val lengthArray = IntArray(1)
        gl.glGetShaderiv(this.handle, GL4.GL_INFO_LOG_LENGTH, lengthArray, 0)
        val length = lengthArray[0]

        if(length > 0)
        {
            val log = ByteArray(length)
            gl.glGetShaderInfoLog(this.handle, length, lengthArray, 0, log, 0)

            this.log = String(log)
        }

        // Check the compilation status to determine if compilation was successful
        val status = IntArray(1)
        gl.glGetShaderiv(this.handle, GL4.GL_COMPILE_STATUS, status, 0)

        if(status[0] == GL4.GL_FALSE)
        {
            gl.glDeleteShader(this.handle)
            throw IllegalStateException("Shader compilation failed: ${this.log}")
        }
    }
}