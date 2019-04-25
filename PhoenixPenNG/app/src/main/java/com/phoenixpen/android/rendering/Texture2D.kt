package com.phoenixpen.android.rendering

import android.opengl.GLES31
import com.phoenixpen.android.application.ScreenDimensions
import java.nio.ByteBuffer

/**
 * A semantic alias for texture dimensions.
 */
typealias Texture2DDimensions = ScreenDimensions

/**
 * A class managing a two dimensional OpenGL texture object.
 *
 * @property parameters Texture parameters
 */
class Texture2D(parameters: Texture2DParameters): Texture()
{
    /**
     * The current texture parameters. Note that this cannot be changed from outside this class,
     * use [recreate] to recreate the texture object managed by this class with new parameters.
     */
    var parameters: Texture2DParameters
        protected set

    /**
     * Initialize texture object with initial parameters
     */
    init
    {
        // Save parameters for later. We have to do this here in order to make the
        // compiler happy.
        this.parameters = parameters

        // The recreate method already implements all logic, so just delegate
        this.recreate(this.parameters)
    }

    /**
     * Recreate the texture object with given texture parameters.
     *
     * @param parameters Texture parameters to use
     */
    open fun recreate(parameters: Texture2DParameters)
    {
        // Overwrite the current texture parameters
        this.parameters = parameters

        // The current texture object needs to be deleted if it exists.
        if(this.handle != GLES31.GL_NONE)
        {
            val textures = intArrayOf(this.handle)

            GLES31.glDeleteTextures(1, textures, 0)

            this.handle = GLES31.GL_NONE
        }

        // Create new texture object
        val textures = IntArray(1)
        GLES31.glGenTextures(1, textures, 0)

        // Texture creation might fail, so better check here
        if(textures[0] == GLES31.GL_NONE)
            throw IllegalStateException("Could not generate texture object")

        // Save handle
        this.handle = textures[0]

        // Temporarily use texture unit 0 to modify newly create texture.
        // This is fine, since all texture classes will overwrite this upon usage.
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)

        // Bind the newly created texture to the texture unit
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, this.handle)

        // Retrieve dimensions for later easier access
        val dims = parameters.dimensions

        // Create buffer, filled with black pixels
        // TODO: This needs to be customizable, since not all texture formats use 3 bytes per pixel
        val texBuffer = ByteBuffer.allocate(dims.width * dims.height * 3)

        // Setup texture with empty data
        GLES31.glTexImage2D(GLES31.GL_TEXTURE_2D, 0, GLES31.GL_RGB, dims.width, dims.height,
                0, GLES31.GL_RGB, GLES31.GL_UNSIGNED_BYTE, texBuffer)

        // Set min/mag filter options
        GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MAG_FILTER, parameters.magFilter.nativeValue)
        GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MIN_FILTER, parameters.minFilter.nativeValue)
    }

    /**
     * Activate the texture object managed by this instance.
     *
     * @param textureUnit Texture unit to bind texture to
     */
    override fun use(textureUnit: TextureUnit)
    {
        GLES31.glActiveTexture(textureUnit.nativeValue)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, this.handle)
    }
}