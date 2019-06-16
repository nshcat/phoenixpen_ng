package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4
import java.nio.IntBuffer

/**
 * A class managing a one dimensional OpenGL texture object backed by a buffer object
 *
 * @property gl The OpenGL context
 * @property size The buffer size, in integers
 */
class BufferTexture(val gl: GL4, var size: Int): Texture()
{
    /**
     * The handle to the buffer object that is backing the buffer texture
     */
    var bufferHandle: Int = GL4.GL_NONE

    /**
     * Initialize buffer texture by creating it using initial size
     */
    init
    {
        this.recreate(size)
    }

    /**
     * Recreate the texture and buffer objects based on given size.
     *
     * @param size The new buffer size
     */
    fun recreate(size: Int)
    {
        // Negative buffer sizes are obviously not allowed here
        if(size < 0)
            throw IllegalArgumentException("A negative size is not allowed here")

        this.size = size

        // The current texture and buffer objects need to be deleted if they already exist
        if(this.handle != GL4.GL_NONE)
        {
            // Free texture object
            val textures = intArrayOf(this.handle)
            gl.glDeleteTextures(1, textures, 0)
            this.handle = GL4.GL_NONE

            // Free buffer object
            val buffers = intArrayOf(this.bufferHandle)
            gl.glDeleteBuffers(1, buffers, 0)
            this.bufferHandle = GL4.GL_NONE
        }

        // Create texture object
        gl.glActiveTexture(GL4.GL_TEXTURE0)
        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)

        // Texture creation might fail, so better check here
        if(textures[0] == GL4.GL_NONE)
            throw IllegalStateException("Could not generate texture object")

        // Save handle
        this.handle = textures[0]

        // Create the buffer object
        val buffers = IntArray(1)
        gl.glGenBuffers(1, buffers, 0)

        if(buffers[0] == GL4.GL_NONE)
            throw IllegalStateException("Could not generate buffer object")

        // Also save buffer handle
        this.bufferHandle = buffers[0]

        // Bind the buffer to the texture buffer target
        gl.glBindBuffer(GL4.GL_TEXTURE_BUFFER, this.bufferHandle)

        // Initialize the buffer data with zero. An integer is 4 bytes
        gl.glBufferData(GL4.GL_TEXTURE_BUFFER, (this.size * 4).toLong(), null, GL4.GL_DYNAMIC_DRAW)

        // Bind the texture that was created earlier and declare the current buffer as the backing buffer
        gl.glBindTexture(GL4.GL_TEXTURE_BUFFER, this.handle)
        gl.glTexBuffer(GL4.GL_TEXTURE_BUFFER, GL4.GL_RGBA32UI, this.bufferHandle)
    }

    /**
     * Activate and use this buffer texture on given texture unit
     *
     * @param textureUnit The texture unit to bind this texture object to
     */
    override fun use(textureUnit: TextureUnit)
    {
        // Both the buffer aswell as the texture need to be bound
        gl.glBindBuffer(GL4.GL_TEXTURE_BUFFER, this.bufferHandle)
        gl.glActiveTexture(textureUnit.nativeValue)
        gl.glBindTexture(GL4.GL_TEXTURE_BUFFER, this.handle)
    }

    /**
     * Upload data in given buffer to the buffer object backing this texture
     *
     * @param buffer Integer buffer containing new buffer data
     */
    fun upload(buffer: IntBuffer)
    {
        // Check buffer size
        if(this.size != buffer.remaining())
            throw IllegalArgumentException("Buffer has invalid size")

        // Bind the buffer in order to allow data changes
        gl.glBindBuffer(GL4.GL_TEXTURE_BUFFER, this.bufferHandle)

        // Replace the buffer contents with new data.
        // This expects size in bytes, so the size stored in this class needs to be multiplied by 4.
        gl.glBufferSubData(GL4.GL_TEXTURE_BUFFER, 0, (this.size * 4).toLong(), buffer)
    }
}