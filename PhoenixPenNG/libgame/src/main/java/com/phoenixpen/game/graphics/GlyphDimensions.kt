package com.phoenixpen.game.graphics

import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position

/**
 * A simple class containing information about the glyph dimensions of a surface
 *
 * @property baseDimensions The glyph dimensions derived from the texture sheet
 * @property scaleFactor The factor of which to scale the glyphs with
 */
data class GlyphDimensions(
        val baseDimensions: Dimensions,
        val scaleFactor: Float = 1.0f
)
{
    /**
     * The actual dimensions, which result from the [baseDimensions] scaled according
     * to the [scaleFactor]
     */
    val dimensions = Dimensions(
            (this.baseDimensions.width * this.scaleFactor).toInt(),
            (this.baseDimensions.height * this.scaleFactor).toInt())

    /**
     * Calculate the relative pixel coordinates of a given cell position inside a surface
     * with these glyph dimensions
     */
    fun positionToPixels(position: Position): Position
    {
        return Position(position.x * this.dimensions.width, position.y * this.dimensions.height)
    }
}