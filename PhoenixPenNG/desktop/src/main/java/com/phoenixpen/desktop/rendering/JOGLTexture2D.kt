package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.TextureIO
import com.phoenixpen.desktop.application.DesktopResourceProvider

/**
 * A 2d texture implementation using JOGL TextureIO to load the image file
 */
class JOGLTexture2D(val gl: GL4, val tex: com.jogamp.opengl.util.texture.Texture): Texture()
{
    /**
     * Copy handle from Texture object
     */
    init
    {
        this.handle = tex.getTextureObject(gl)
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
        fun FromImageResource(gl: GL4, res: DesktopResourceProvider, rid: String): JOGLTexture2D
        {
            // Retrieve texture path
            val texPath = res.texturePath(rid)

            val joglTex = TextureIO.newTexture(texPath.toFile(), false)

            return JOGLTexture2D(gl, joglTex)
        }
    }


    /**
     * Convenience function that retrieves the current texture dimensions
     *
     * @return Current texture dimensions
     */
    fun dimensions(): Texture2DDimensions = Texture2DDimensions(this.tex.imageWidth, this.tex.imageHeight)

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