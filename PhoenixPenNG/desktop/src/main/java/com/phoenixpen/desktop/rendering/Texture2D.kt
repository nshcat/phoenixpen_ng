package com.phoenixpen.desktop.rendering

import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.TextureIO
import com.phoenixpen.desktop.application.DesktopResourceProvider
import com.phoenixpen.game.ascii.ScreenDimensions
import java.awt.image.DataBufferByte
import java.nio.ByteBuffer

/**
 * A semantic alias for texture dimensions.
 */
typealias Texture2DDimensions = ScreenDimensions

/**
 * A class managing a two dimensional OpenGL texture object.
 *
 * @property gl The OpenGL context
 * @property parameters Texture parameters
 */
class Texture2D(val gl: GL4, parameters: Texture2DParameters): Texture()
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
     * "Static" utility functions that allow texture creation from image resources
     */
    companion object
    {
        /**
         * Create texture from given image resource.
         *
         * @param gl The OpenGL context
         * @param res The resource provider
         * @param rid The resource identifier
         *
         * @return Texture created from specified image resource
         */
        fun FromImageResource(gl: GL4, res: DesktopResourceProvider, rid: String): Texture2D
        {
            // TODO if this doesnt work, use TextureIO

            // Retrieve image as bitmap, without scaling
            val bitmap = res.bufferedImage(rid)

            // Create parameters
            val parameters = Texture2DParameters(
                    Texture2DDimensions(bitmap.width, bitmap.height),
                    TextureMagFilter.Nearest,
                    TextureMinFilter.Nearest
            )

            // Create texture
            val tex = Texture2D(gl, parameters)

            // Activate texture and load image data
            tex.use(TextureUnit.Unit0)

            val buffer = bitmap.raster.dataBuffer as DataBufferByte
            val data = buffer.data
            val pixels = Buffers.newDirectByteBuffer(data.size)
            pixels.put(data)
            pixels.flip()

            gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, bitmap.width, bitmap.height,
                    0, GL4.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixels)

            // Generate all needed mipmap levels for this texture
            gl.glGenerateMipmap(GL4.GL_TEXTURE_2D)

            // The texture is fully loaded and ready to use
            return tex
        }
    }

    /**
     * Recreate the texture object with given texture parameters.
     *
     * @param parameters Texture parameters to use
     */
    fun recreate(parameters: Texture2DParameters)
    {
        // Overwrite the current texture parameters
        this.parameters = parameters

        // The current texture object needs to be deleted if it exists.
        if(this.handle != GL4.GL_NONE)
        {
            val textures = intArrayOf(this.handle)

            gl.glDeleteTextures(1, textures, 0)

            this.handle = GL4.GL_NONE
        }

        // Create new texture object
        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)

        // Texture creation might fail, so better check here
        if(textures[0] == GL4.GL_NONE)
            throw IllegalStateException("Could not generate texture object")

        // Save handle
        this.handle = textures[0]

        // Temporarily use texture unit 0 to modify newly create texture.
        // This is fine, since all texture classes will overwrite this upon usage.
        gl.glActiveTexture(GL4.GL_TEXTURE0)

        // Bind the newly created texture to the texture unit
        gl.glBindTexture(GL4.GL_TEXTURE_2D, this.handle)

        // Retrieve dimensions for later easier access
        val dims = parameters.dimensions

        // Create buffer, filled with black pixels
        // TODO: This needs to be customizable, since not all texture formats use 3 bytes per pixel
        val texBuffer = ByteBuffer.allocate(dims.width * dims.height * 3)

        // Setup texture with empty data
        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, dims.width, dims.height,
                0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, texBuffer)

        // Set min/mag filter options
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, parameters.magFilter.nativeValue)
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, parameters.minFilter.nativeValue)
    }

    /**
     * Convenience function that retrieves the current texture dimensions
     *
     * @return Current texture dimensions
     */
    fun dimensions(): Texture2DDimensions = this.parameters.dimensions

    /**
     * Activate the texture object managed by this instance.
     *
     * @param textureUnit Texture unit to bind texture to
     */
    override fun use(textureUnit: TextureUnit)
    {
        gl.glActiveTexture(textureUnit.nativeValue)
        gl.glBindTexture(GL4.GL_TEXTURE_2D, this.handle)
    }
}