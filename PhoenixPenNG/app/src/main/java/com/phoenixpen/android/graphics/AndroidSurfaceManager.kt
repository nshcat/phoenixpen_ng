package com.phoenixpen.android.graphics

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.application.AndroidResourceProvider
import com.phoenixpen.android.rendering.RenderParams
import com.phoenixpen.android.rendering.Renderable
import com.phoenixpen.android.rendering.Texture2D
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.game.ascii.plus
import com.phoenixpen.game.graphics.Surface
import com.phoenixpen.game.graphics.SurfaceDimensions
import com.phoenixpen.game.graphics.SurfaceManager

/**
 * Surface manager implementation for desktop
 *
 * @property context The app context
 * @property res The current resource provider
 * @property screenDimensions The device screen dimensions, in pixels
 */
class AndroidSurfaceManager(
        val context: Context,
        val res: AndroidResourceProvider,
        override var screenDimensions: ScreenDimensions
): SurfaceManager, Renderable
{
    /**
     * A collection of all registered surfaces
     */
    private val surfaces = ArrayList<AndroidSurface>()

    /**
     * The texture manager holding and caching glyph tile sets
     */
    private val textureManager = GlyphTextureManager(this.context, this.res)

    /**
     * The shadow texture. This is used by all glyph textures
     */
    private val shadowTexture = Texture2D.FromImageResource(this.context, R.drawable.shadows)

    /**
     * Render to screen using given rendering parameters
     *
     * @param params Rendering parameters to use
     */
    override fun render(params: RenderParams)
    {
        // Render all enabled surfaces in order
        for(surface in this.surfaces.asReversed())
        {
            if(surface.enabled)
                surface.render(params)
        }
    }

    /**
     * Clear all registered surfaces
     */
    fun clearAll()
    {
        for(surface in this.surfaces)
        {
            surface.clear()
        }
    }

    /**
     * Inform the surface manager of a screen reshape.
     *
     * @param newDimensions The new screen dimensions, in pixels
     */
    fun reshape(newDimensions: ScreenDimensions)
    {
        // Drop all surfaces. They are invalidated
        for(surface in this.surfaces)
        {
            surface.drop()
        }

        // Set new dimensions
        this.screenDimensions = newDimensions
    }

    /**
     * Create new surface at given absolute position (in pixels), and with given dimensions (in glyphs)
     *
     * @param position Absolute screen position of the surface, in pixels
     * @param dimensions Dimensions of the surface, in glyphs
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    override fun createSurface(position: Position, dimensions: SurfaceDimensions, glyphSetId: String): Surface
    {
        // Retrieve texture instance from cache
        val texture = this.textureManager.retrieveTexture(glyphSetId, 1.0f)

        // Create actual surface object
        val surface = AndroidSurface(
                this.context,
                position,
                dimensions,
                texture,
                this.shadowTexture
        )

        this.surfaces.add(surface)

        return surface
    }

    /**
     * Create new surface that spans the whole device screen
     *
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    override fun createSurface(glyphSetId: String): Surface
    {
        // Retrieve texture instance from cache
        val texture = this.textureManager.retrieveTexture(glyphSetId, 1.0f)

        // Retrieve glyph dimensions
        val glyphDims = texture.glyphDimensions.dimensions

        // Determine surface dimensions in glyphs
        val surfaceDims = SurfaceDimensions(
                this.screenDimensions.width / glyphDims.width,
                this.screenDimensions.height / glyphDims.height
        )

        // Create actual surface object
        val surface = AndroidSurface(
                this.context,
                Position(0, 0),
                surfaceDims,
                texture,
                this.shadowTexture
        )

        this.surfaces.add(surface)

        return surface
    }

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
    override fun createSurfaceRelative(parent: Surface, position: Position, dimensions: SurfaceDimensions, glyphSetId: String): Surface
    {
        // Calculate offset
        val offset = parent.position + parent.glyphDimensions.positionToPixels(position)

        // Retrieve texture instance from cache
        val texture = this.textureManager.retrieveTexture(glyphSetId, 1.0f)

        // Create actual surface object
        val surface = AndroidSurface(
                this.context,
                offset,
                dimensions,
                texture,
                this.shadowTexture
        )

        this.surfaces.add(surface)

        return surface
    }

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
     * @param position Relative position in parent surface, in glyphs
     * @param dimensions Dimensions of the surface, in glyphs
     * @param glyphSetId Resource ID of the glyph tile set to use
     *
     * @return New surface with requested parameters
     */
    override fun createSurfaceRelative(parent: Surface, tl: Position, br: Position, glyphSetId: String): Surface
    {
        // Some sanity checks
        if(tl.x >= br.x || tl.y >= br.y)
            throw IllegalArgumentException("SurfaceManager::createSurfaceRelative: Illegal TL/BR points")

        // Convert both positions into absolute pixel coordinates
        val tlPixels = parent.glyphDimensions.positionToPixels(tl)
        val brPixels = parent.glyphDimensions.positionToPixels(br)

        // Determine width and height of the surface, in pixels.
        // To fully include the BR point, we need to add the dimensions of the parent surfaces glyphs here.
        val wPixels = (brPixels.x - tlPixels.x) + parent.glyphDimensions.dimensions.width
        val hPixels = (brPixels.y - tlPixels.y) + parent.glyphDimensions.dimensions.height

        // Retrieve texture instance from cache
        val texture = this.textureManager.retrieveTexture(glyphSetId, 1.0f)

        // Find nearest amount of glyphs that fit into it
        val wGlyphs = wPixels / texture.glyphDimensions.dimensions.width
        val hGlyphs = hPixels / texture.glyphDimensions.dimensions.height

        // More sanity checks
        if(wGlyphs <= 0 || hGlyphs <= 0)
            throw IllegalArgumentException("SurfaceManager::createSurfaceRelative: Created surface fits no glyphs at all")

        // Create actual surface object
        val surface = AndroidSurface(
                this.context,
                tlPixels + parent.position,
                SurfaceDimensions(wGlyphs, hGlyphs),
                texture,
                this.shadowTexture
        )

        this.surfaces.add(surface)

        return surface
    }
}