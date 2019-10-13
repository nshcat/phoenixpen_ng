package com.phoenixpen.android.graphics


import android.content.Context
import com.phoenixpen.android.application.AndroidResourceProvider
import com.phoenixpen.android.rendering.Texture2D
import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.graphics.GlyphDimensions

/**
 * A class used to manage the glyph texture instances used when rendering surfaces. This allows
 * resource sharing.
 *
 * @property context The android app context
 * @property resourceProvider The resource provider
 */
class GlyphTextureManager(private val context: Context, private val resourceProvider: AndroidResourceProvider)
{
    /**
     * All active and cached tile set textures.
     */
    private val textureCache = HashMap<String, Texture2D>()

    /**
     * Retrieve glyph texture
     */
    fun retrieveTexture(id: String, scaleFactor: Float): GlyphTexture
    {
        // First look if there is an OpenGL texture cached for this resource id
        if(this.textureCache.containsKey(id))
        {
            // Retrieve texture and use to make wrapper
            return this.makeTextureWrapper(this.textureCache.getValue(id), scaleFactor)
        }
        else
        {
            // Create texture
            val tex = Texture2D.FromImageResource(this.context, this.resourceProvider.getTextureId(id))

            // Cache for later use
            this.textureCache.put(id, tex)

            return this.makeTextureWrapper(tex, scaleFactor)
        }
    }

    private fun makeTextureWrapper(tex: Texture2D, scaleFactor: Float): GlyphTexture
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