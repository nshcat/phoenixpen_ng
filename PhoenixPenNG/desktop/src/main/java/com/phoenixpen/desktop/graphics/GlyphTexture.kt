package com.phoenixpen.desktop.graphics

import com.phoenixpen.desktop.rendering.JOGLTexture2D
import com.phoenixpen.desktop.rendering.Texture2D
import com.phoenixpen.game.graphics.GlyphDimensions

/**
 * A class managing the glyph texture used to render on a surface
 *
 * @property glyphDimensions Dimensions of the glyphs present on the tile set texture
 * @property openGlTexture OpenGL texture object representing the tile set texture
 */
class GlyphTexture (
        val glyphDimensions: GlyphDimensions,
        val openGlTexture: JOGLTexture2D
)