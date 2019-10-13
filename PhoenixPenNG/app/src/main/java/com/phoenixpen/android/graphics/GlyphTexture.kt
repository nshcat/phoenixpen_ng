package com.phoenixpen.android.graphics

import com.phoenixpen.android.rendering.Texture2D
import com.phoenixpen.game.graphics.GlyphDimensions

/**
 * A class managing the glyph texture used to render on a surface
 *
 * @property glyphDimensions Dimensions of the glyphs present on the tile set texture
 * @property openGlTexture OpenGL texture object representing the tile set texture
 */
class GlyphTexture (
        val glyphDimensions: GlyphDimensions,
        val openGlTexture: Texture2D
)