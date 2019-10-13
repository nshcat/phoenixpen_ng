package com.phoenixpen.game.graphics

import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.ScreenDimensions

/**
 * An interface for classes that allow the creation of surfaces in a platform dependant way.
 * An instance of this is dependency-injected into the game library by the platform dependant
 * application layer.,
 */
interface SurfaceManager
{
    /**
     * The devices screen dimensions, in pixels
     */
    val screenDimensions: ScreenDimensions

    /**
     * Create new surface at given absolute position (in pixels), and with given dimensions (in glyphs)
     *
     * @param position Absolute screen position of the surface, in pixels
     * @param dimensions Dimensions of the surface, in glyphs
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    fun createSurface(position: Position, dimensions: SurfaceDimensions, glyphSetId: String): Surface

    /**
     * Create new surface that spans the whole device screen
     *
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    fun createSurface(glyphSetId: String): Surface

    /**
     * Create new surface relative to parent surface, with a position on the parent surface (in glyph),
     * that will be internally translated to an absolute screen position.
     *
     * @param parent Parent surface to use for relative position calculations
     * @param position Relative position in parent surface, in glyphs
     * @param dimensions Dimensions of the surface, in glyphs
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    fun createSurfaceRelative(parent: Surface, position: Position, dimensions: SurfaceDimensions, glyphSetId: String): Surface

    /**
     * Create new surface relative to parent surface, given a top left and bottom right point.
     *
     * This will calculate pixel coordinates that are _inclusive_ of both points:
     *
     *  TL-> OXXXX
     *       XXXXX
     *       XXXXO <-BR
     *
     * For example, with a subsurface that has a tile set with half the dimensions (and thus four
     * glyphs fitting in one glyph cell of the parent surface), this means that the subsurface completely
     * covers BOTH the TL and BR cells.
     *
     * @param parent Parent surface to use for relative position calculations
     * @param tl Top left point on the parent surface
     * @param br Bottom right point on the parent surface
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    fun createSurfaceRelative(parent: Surface, tl: Position, br: Position, glyphSetId: String): Surface
}