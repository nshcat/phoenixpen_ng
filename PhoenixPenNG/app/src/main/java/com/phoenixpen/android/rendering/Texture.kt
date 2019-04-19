package com.phoenixpen.android.rendering

import android.opengl.GLES30
import com.phoenixpen.android.application.ScreenDimensions
import java.nio.ByteBuffer

/**
 * A semantic alias for texture dimensions.
 */
typealias TextureDimensions = ScreenDimensions

/**
 * An abstract base class for OpenGL texture objects. Subclasses might implement application specific
 * configuration, e.g. for textures used as frame buffer object backing.
 */
/*abstract*/ class Texture(parameters: TextureParameters)
{
    // TODO: refactor this, make 2DTexture a subclass of this.

    /**
     * The native OpenGL handle of the texture object. This is mutable since we want to allow
     * regeneration of the texture, e.g. on screen size change if used as FBO backing texture.
     */
    var handle: Int = GLES30.GL_NONE
        protected set

    /**
     * The current texture parameters. Note that this cannot be changed from outside this class,
     * use [recreate] to recreate the texture object managed by this class with new parameters.
     */
    var parameters: TextureParameters
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
    open fun recreate(parameters: TextureParameters)
    {
        // Overwrite the current texture parameters
        this.parameters = parameters

        // The current texture object needs to be deleted if it exists.
        if(this.handle != GLES30.GL_NONE)
        {
            val textures = intArrayOf(this.handle)

            GLES30.glDeleteTextures(1, textures, 0)

            this.handle = GLES30.GL_NONE
        }

        // Create new texture object
        val textures = IntArray(1)
        GLES30.glGenTextures(1, textures, 0)

        // Texture creation might fail, so better check here
        if(textures[0] == GLES30.GL_NONE)
            throw IllegalStateException("Could not generate texture object")

        // Save handle
        this.handle = textures[0]

        // Temporarily use texture unit 0 to modify newly create texture.
        // This is fine, since all texture classes will overwrite this upon usage.
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)

        // Bind the newly created texture to the texture unit
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.handle)

        // Retrieve dimensions for later easier access
        val dims = parameters.dimensions

        // Create buffer, filled with black pixels
        // TODO: This needs to be customizable, since not all texture formats use 3 bytes per pixel
        val texBuffer = ByteBuffer.allocate(dims.width * dims.height * 3)

        // Setup texture with empty data
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB, dims.width, dims.height,
                0, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, texBuffer)

        // Set min/mag filter options
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, parameters.magFilter.nativeValue)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, parameters.minFilter.nativeValue)
    }

    /**
     * Activate the texture object managed by this instance.
     *
     * @param textureUnit Texture unit to bind texture to
     */
    open fun use(textureUnit: TextureUnit)
    {
        GLES30.glActiveTexture(textureUnit.nativeValue)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.handle)
    }
}