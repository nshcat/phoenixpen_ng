package com.phoenixpen.desktop.graphics

import com.jogamp.opengl.GL4
import com.phoenixpen.desktop.application.DesktopResourceProvider
import com.phoenixpen.desktop.rendering.JOGLTexture2D
import com.phoenixpen.desktop.rendering.Texture2D
import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.graphics.GlyphDimensions

/**
 * A class used to manage the glyph texture instances used when rendering surfaces. This allows
 * resource sharing.
 *
 * @property gl The OpenGL context
 * @property resourceProvider The resource provider
 */
class GlyphTextureManager(val gl: GL4, val resourceProvider: DesktopResourceProvider)
{
    /**
     * All active and cached tile set textures.
     */
    private val textureCache = HashMap<String, JOGLTexture2D>()

    /**
     * Retrieve glyph texture
     */
    fun retrieveTexture(id: String, scaleFactor: Float): GlyphTexture
    {
        // First look if there is an OpenGL texture cached for this resource id
        /*if(this.textureCache.containsKey(id))
        {
            // Retrieve texture and use to make wrapper
            return this.makeTextureWrapper(this.textureCache.getValue(id), scaleFactor)
        }
        else
        {*/
            // Create texture
            val tex = JOGLTexture2D.FromImageResource(gl, this.resourceProvider, id)

            // Cache for later use
            this.textureCache.put(id, tex)

            return this.makeTextureWrapper(tex, scaleFactor)
        //}
    }

    private fun makeTextureWrapper(tex: JOGLTexture2D, scaleFactor: Float): GlyphTexture
    {
        // Determine glyph dimensions
        val dims = tex.dimensions()

        // There are 16 glyphs in horizontal and vertical direction
        val glyphBaseWidth = dims.width / 16
        val glyphBaseHeight = dims.height / 16

        val glyphDims = GlyphDimensions(Dimensions(glyphBaseWidth, glyphBaseHeight), scaleFactor)

        return GlyphTexture(glyphDims, tex)
    }
}