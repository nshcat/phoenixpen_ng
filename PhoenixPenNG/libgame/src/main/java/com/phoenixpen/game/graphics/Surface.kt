package com.phoenixpen.game.graphics

import com.phoenixpen.game.ascii.*

/**
 * Type used to describe the dimension of a surface, in glyphs
 */
typealias SurfaceDimensions = Dimensions

/**
 * Type used to describe the dimension of a surface, in pixels
 */
typealias SurfacePixelDimensions = Dimensions

/**
 * A surface that can be drawn on by the game. Since each surface can use different tile sets,
 * the game can use different glyph styles and, more importantly, sizes for different parts of the
 * user interface.
 *
 * Once a surface is created, its absolute position and size can not be changed anymore.
 */
interface Surface
{
    /**
     * Information about the glyphs this surface uses
     */
    val glyphDimensions: GlyphDimensions

    /**
     * The size of this surface, in pixels
     */
    val dimensionsInPixels: SurfacePixelDimensions

    /**
     * The size of this surface, in glyphs
     */
    val dimensionsInGlyphs: SurfaceDimensions

    /**
     * Whether [clear] should clear this surface using transparent glyphs,
     * thus allowing underlying surfaces to show through
     */
    val clearWithTransparency: Boolean

    /**
     * Whether this surface is enabled. This controls whether it will be drawn or not.
     */
    val enabled: Boolean

    /**
     * Clear the screen
     */
    fun clear()

    /**
     * Set the glyph of a screen cell
     *
     * @param pos Screen position of glyph to set, in glyphs
     * @param glyph New glyph code. Has to be in range [0, 255]
     */
    fun setGlyph(pos: Position, glyph: Int)

    /**
     * Set the depth value of a screen cell
     *
     * @param pos Screen position of depth to set, in glyphs
     * @param depth New depth value. Has to be in range [0, 255]
     */
    fun setDepth(pos: Position, depth: Int)

    /**
     * Set tile drawing info of a screen cell, which means both glyph and front- and back colour
     * are set at the same time
     *
     * @param pos Screen position of tile to set, in glyphs
     * @param tile Tile drawing information
     */
    fun setTile(pos: Position, tile: DrawInfo)

    /**
     * Set the front color of a screen cell
     *
     * @param pos Screen position of front color to set, in glyphs
     * @param color New front color. Each component has to be in range [0, 255]
     */
    fun setFrontColor(pos: Position, color: Color)

    /**
     * Set the back color of a screen cell
     *
     * @param pos Screen position of back color to set, in glyphs
     * @param color New back color. Each component has to be in range [0, 255]
     */
    fun setBackColor(pos: Position, color: Color)

    /**
     * Set the shadow direction of a screen cell
     *
     * @param pos Screen position of shadow to set, in glyphs
     * @param shadow New shadow direction. Will overwrite old one
     */
    fun setShadow(pos: Position, shadow: ShadowDirection)

    /**
     * Set the shadow directions of a screen cell
     *
     * @param pos Screen position of shadows to set, in glyphs
     * @param shadows Set of shadow directions. Will overwrite old one
     */
    fun setShadows(pos: Position, shadows: ShadowDirections)

    /**
     * Clear all shadow directions of a screen cell
     *
     * @param pos Screen position where shadows should be cleared at
     */
    fun clearShadows(pos: Position)
}