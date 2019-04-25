package com.phoenixpen.android.rendering

/**
 * A class managing a one dimensional OpenGL texture object backed by a buffer object
 *
 * @property size The buffer size, in bytes
 */
class BufferTexture(size: Int): Texture()
{
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


    }


    /**
     * Activate and use this buffer texture on given texture unit
     *
     * @param textureUnit The texture unit to bind this texture object to
     */
    override fun use(textureUnit: TextureUnit)
    {

    }
}