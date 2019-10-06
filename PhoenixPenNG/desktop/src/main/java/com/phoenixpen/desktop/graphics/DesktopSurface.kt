package com.phoenixpen.desktop.graphics

import com.jogamp.opengl.GL4
import com.phoenixpen.desktop.application.DesktopResourceProvider
import com.phoenixpen.game.graphics.Color
import com.phoenixpen.desktop.rendering.*
import com.phoenixpen.desktop.rendering.materials.AsciiScreenMaterial
import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.ShadowDirection
import com.phoenixpen.game.ascii.ShadowDirections
import com.phoenixpen.game.graphics.DrawInfo
import com.phoenixpen.game.graphics.Surface
import com.phoenixpen.game.graphics.SurfacePixelDimensions
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Surface implementation for OpenGl4 desktop
 */
class DesktopSurface(
        private val gl: GL4,
        res: DesktopResourceProvider,
        override val position: Position,
        override val dimensionsInGlyphs: Dimensions,
        private val glyphTexture: GlyphTexture,
        private val shadowTexture: JOGLTexture2D
    )
    : Shadeable(AsciiScreenMaterial(gl, res)), Surface
{
    // Data offsets for screen elements
    private val OFFSET_FR       = 0
    private val OFFSET_FG       = 1
    private val OFFSET_FB       = 2
    private val OFFSET_GLYPH    = 3
    private val OFFSET_BR       = 4
    private val OFFSET_BG       = 5
    private val OFFSET_BB       = 6
    private val OFFSET_DATA     = 7

    /**
     * Information about the glyphs this surface uses
     */
    override val glyphDimensions = this.glyphTexture.glyphDimensions

    /**
     * The size of this surface, in pixels
     */
    override val dimensionsInPixels: SurfacePixelDimensions

    /**
     * Whether [clear] should clear this surface using transparent glyphs,
     * thus allowing underlying surfaces to show through
     */
    override var clearWithTransparency: Boolean = false

    /**
     * Whether this surface is enabled. This controls whether it will be drawn or not.
     */
    override var enabled: Boolean = true

    /**
     * Whether the surface contents have been changed and need to be reuploaded
     */
    private var dirty: Boolean = true

    /**
     * Size of the internal buffer
     */
    private val bufferSize = this.dimensionsInGlyphs.height * this.dimensionsInGlyphs.width * 2 * 4

    /**
     * The buffer texture used to hold the surface data on the GPU.
     */
    private var bufferTexture = BufferTexture(this.gl, this.bufferSize)

    /**
     * The data array that is used as backing storage for all surface operations. On render, its contents
     * are copied to the buffer texture.
     */
    private val data = IntArray(this.bufferSize)

    /**
     * Initialization
     */
    init
    {
        // Determine dimensions in pixels
        this.dimensionsInPixels = Dimensions(
                this.glyphDimensions.dimensions.width * this.dimensionsInGlyphs.width,
                this.glyphDimensions.dimensions.height * this.dimensionsInGlyphs.height
        )

        // Set the properties in the AsciiMaterial
        val mat = this.material as AsciiScreenMaterial
        mat.apply {
            glyphDimensions = this@DesktopSurface.glyphDimensions
            surfaceDimensions = this@DesktopSurface.dimensionsInGlyphs
            position = this@DesktopSurface.position
        }

        // A refresh is definitely needed
        this.dirty = true
    }

    /**
     * Render ascii matrix to screen
     *
     * @param params Rendering parameters to use
     */
    override fun render(params: RenderParams)
    {
        // Apply material properties
        super.render(params)

        // Upload current screen state to the buffer texture, if needed
        if(this.dirty)
        {
            this.dirty = false

            // Copy local data to temporary integer buffer
            // We need to apply endianess swapping here, since the JVM is BigEndian
            val byteBuffer = ByteBuffer.allocateDirect(this.data.size * 4)
                    .apply { order(ByteOrder.LITTLE_ENDIAN)}

            // Interpret byte buffer as int buffer
            val intBuffer = byteBuffer.asIntBuffer()

            // Insert data and upload
            intBuffer.put(this.data)
            intBuffer.position(0)
            this.bufferTexture.upload(intBuffer)
        }

        // Activate all textures
        this.glyphTexture.openGlTexture.use(TextureUnit.Unit0)
        this.shadowTexture.use(TextureUnit.Unit1)
        this.bufferTexture.use(TextureUnit.Unit2)

        // Render instanced quad for each glyph on the scree
        this.gl.glDrawArraysInstanced(GL4.GL_TRIANGLES, 0, 6, this.dimensionsInGlyphs.width * this.dimensionsInGlyphs.height)
    }

    /**
     * Calculate the buffer offset for given screen position, in glyphs
     *
     * @param pos Screen position to convert to linear address, in glyphs
     * @return Linear buffer offset for given position
     */
    private fun offsetOf(pos: Position): Int
    {
        return (2 * 4) * ((this.dimensionsInGlyphs.width * pos.y) + pos.x)
    }


    /**
     * Clear the screen
     */
    override fun clear()
    {
        this.data.fill(0)
        this.dirty = true

        // TODO make this more effecient?
        if(this.clearWithTransparency)
        {
            for(ix in 0 until this.dimensionsInGlyphs.width)
                for(iy in 0 until this.dimensionsInGlyphs.height)
                    this.setTransparent(Position(ix, iy), true)
        }
    }

    /**
     * Set the depth value of a screen cell
     *
     * @param pos Screen position of depth to set, in glyphs
     * @param depth New depth value. Has to be in range [0, 255]
     */
    override fun setDepth(pos: Position, depth: Int)
    {
        // Check depth range
        if(depth < 0 || depth > 64)
            throw IllegalArgumentException("Depth value out of range: $depth")

        // Prepare depth value. This is done to protect the transparency bit, which is the eighth
        // bit of the depth value.
        val maskedDepth = depth and 0x7F

        this.data[this.offsetOf(pos) + OFFSET_DATA] =
                (this.data[this.offsetOf(pos) + OFFSET_DATA] and 0xFF80) or maskedDepth

        this.dirty = true
    }

    /**
     * Set the transparency status of a surface cell
     *
     * @param pos surface position to set transparency status for
     * @param isTransparent new transparency status
     */
    override fun setTransparent(pos: Position, isTransparent: Boolean)
    {
        val bit = if (isTransparent) 0x1 else 0x0

        this.data[this.offsetOf(pos) + OFFSET_DATA] =
                (this.data[this.offsetOf(pos) + OFFSET_DATA] and 0xFF7F) or (bit shl 7)
    }

    /**
     * Set tile drawing info of a screen cell, which means both glyph and front- and back colour
     * are set at the same time
     *
     * @param pos Screen position of tile to set, in glyphs
     * @param tile Tile drawing information
     */
    override fun setTile(pos: Position, tile: DrawInfo)
    {
        this.setGlyph(pos, tile.glyph)
        this.setFrontColor(pos, tile.foreground)
        this.setBackColor(pos, tile.background)
    }


    /**
     * Set the front color of a screen cell
     *
     * @param pos Screen position of front color to set, in glyphs
     * @param color New front color. Each component has to be in range [0, 255]
     */
    override fun setFrontColor(pos: Position, color: Color)
    {
        // TODO color range checks?
        this.data[this.offsetOf(pos) + OFFSET_FR] = color.r
        this.data[this.offsetOf(pos) + OFFSET_FG] = color.g
        this.data[this.offsetOf(pos) + OFFSET_FB] = color.b

        this.setTransparent(pos, false)

        this.dirty = true
    }

    /**
     * Set the back color of a screen cell
     *
     * @param pos Screen position of back color to set, in glyphs
     * @param color New back color. Each component has to be in range [0, 255]
     */
    override fun setBackColor(pos: Position, color: Color)
    {
        // TODO color range checks?
        this.data[this.offsetOf(pos) + OFFSET_BR] = color.r
        this.data[this.offsetOf(pos) + OFFSET_BG] = color.g
        this.data[this.offsetOf(pos) + OFFSET_BB] = color.b

        this.setTransparent(pos, false)

        this.dirty = true
    }

    /**
     * Set the shadow direction of a screen cell
     *
     * @param pos Screen position of shadow to set, in glyphs
     * @param shadow New shadow direction. Will overwrite old one
     */
    override fun setShadow(pos: Position, shadow: ShadowDirection)
    {
        this.data[this.offsetOf(pos) + OFFSET_DATA] =
                (this.data[this.offsetOf(pos) + OFFSET_DATA] and 0xFF00.inv()) or shadow.nativeValue

        this.dirty = true
    }

    /**
     * Set the shadow directions of a screen cell
     *
     * @param pos Screen position of shadows to set, in glyphs
     * @param shadows Set of shadow directions. Will overwrite old one
     */
    override fun setShadows(pos: Position, shadows: ShadowDirections)
    {
        // Accumulate all shadow directions
        var shadowValue = 0
        for(direction in shadows)
        {
            shadowValue = shadowValue or direction.nativeValue
        }

        this.data[this.offsetOf(pos) + OFFSET_DATA] =
                (this.data[this.offsetOf(pos) + OFFSET_DATA] and 0xFF00.inv()) or shadowValue

        this.dirty = true
    }

    /**
     * Clear all shadow directions of a screen cell
     *
     * @param pos Screen position where shadows should be cleared at
     */
    override fun clearShadows(pos: Position)
    {
        this.data[this.offsetOf(pos) + OFFSET_DATA] =
                (this.data[this.offsetOf(pos) + OFFSET_DATA] and 0xFF00.inv())
    }

    /**
     * Set the glyph of a screen cell
     *
     * @param pos Screen position of glyph to set, in glyphs
     * @param glyph New glyph code. Has to be in range [0, 255]
     */
    override fun setGlyph(pos: Position, glyph: Int)
    {
        // Check glyph range
        if(glyph < 0 || glyph > 255)
            throw IllegalArgumentException("Glyph code out of range: $glyph")

        this.data[this.offsetOf(pos) + OFFSET_GLYPH] = glyph

        this.setTransparent(pos, false)

        this.dirty = true
    }
}