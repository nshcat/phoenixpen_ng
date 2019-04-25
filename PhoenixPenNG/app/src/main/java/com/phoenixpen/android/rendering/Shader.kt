package com.phoenixpen.android.rendering

import android.content.Context
import android.opengl.GLES31
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.IllegalStateException

/**
 * A class abstracting the concept of an OpenGL shader object.
 *
 * @property type The type of this shader object, e.g. fragment shader
 */
class Shader (val type: ShaderType)
{
    /**
     * The native OpenGL handle of this shader.
     */
    var handle: Int = GLES31.GL_NONE
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
        this.handle = GLES31.glCreateShader(this.type.nativeValue)

        // Check for error
        if(this.handle == GLES31.GL_NONE)
        {
            // We cant recover from this
            Log.e("Shader", "Could not create shader object");
            throw IllegalStateException()
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
         * @param type Shader type
         * @param source Shader source code as string
         * @return Compiled shader object
         */
        fun FromText(type: ShaderType, source: String): Shader
        {
            // Create new shader
            var shader = Shader(type)

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
         * @param type Shader type
         * @param ctx App context
         * @param id Resource ID
         */
        fun FromResource(type: ShaderType, ctx: Context, id: Int): Shader
        {
            // Open resource input stream
            val stream = ctx.resources.openRawResource(id)

            try
            {
                // Create a character-based reader to read the whole source file
                val reader = BufferedReader(InputStreamReader(stream))

                // Delegate to other shader construction method
                return FromText(type, reader.readText())
            }
            catch(ex: IOException)
            {
                Log.e("Shader", "Failed to load shader from resource: ${ex.message}")
                throw ex
            }
        }

        /**
         * Create shader from source file in resource folder "res".. This avoids the usage of
         * the context. This might not work with all Android SDKs!
         *
         * @param type Shader type
         * @param path The path to the resource file holding the source code. Has to be in /res/!
         */
        fun FromResource(type: ShaderType, path: String): Shader
        {
            // Try to retrieve resource stream
            val stream = ::Shader.javaClass.classLoader.getResourceAsStream(path)

            // Create a reader
            val reader = BufferedReader(InputStreamReader(stream))

            // Delegate to other shader construction method
            return FromText(type, reader.readText())
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
        GLES31.glShaderSource(this.handle, source)
    }

    /**
     * Compile the shader. This is only possible if source code was added first.
     */
    protected fun compile()
    {
        // Try to compile the shader
        GLES31.glCompileShader(this.handle)

        // Retrieve the shader compilation log. This might contain messages no matter
        // the compilation status
        this.log = GLES31.glGetShaderInfoLog(this.handle)

        // Check the compilation status to determine if compilation was successful
        val status = IntArray(1)
        GLES31.glGetShaderiv(this.handle, GLES31.GL_COMPILE_STATUS, status, 0)

        if(status[0] == GLES31.GL_FALSE)
        {
            // Something went wrong
            Log.e("Shader", "Failed to compiler shader: " + this.log)
            GLES31.glDeleteShader(this.handle)
            throw IllegalStateException("Shader compilation failed")
        }
    }
}