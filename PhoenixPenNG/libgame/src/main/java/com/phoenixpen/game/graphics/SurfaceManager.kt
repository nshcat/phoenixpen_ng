package com.phoenixpen.game.graphics

import com.phoenixpen.game.ascii.Position

/**
 * An interface for classes that allow the creation of surfaces in a platform dependant way.
 * An instance of this is dependency-injected into the game library by the platform dependant
 * application layer.,
 */
interface SurfaceManager
{
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
     * Create new surface at given glyph position relative to the parent.
     *
     * This method will calculate the absolute pixel coordinates of the given position in the parent
     * surface, and create a new surface with that position.
     *
     * @param parent Parent surface to use for relative position calculations
     * @param position Relative position in parent surface, in glyphs
     * @param dimensions Dimensions of the surface, in glyphs
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    fun createSurfaceRelative(parent: Surface, position: Position, dimensions: SurfaceDimensions, glyphSetId: String): Surface
}