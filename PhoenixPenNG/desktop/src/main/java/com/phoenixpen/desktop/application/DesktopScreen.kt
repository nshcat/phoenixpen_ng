package com.phoenixpen.desktop.application

import com.jogamp.opengl.GL4
import com.phoenixpen.desktop.rendering.*
import com.phoenixpen.desktop.rendering.materials.AsciiScreenMaterial
import com.phoenixpen.game.ascii.Screen
import com.phoenixpen.game.ascii.*
import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.ScreenDimensions
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * A screen made up from a matrix of coloured ASCII glyphs.
 *
 * @property size Screen dimensions, in PIXELS
 */
class DesktopScreen(val gl: GL4, val res: DesktopResourceProvider, size: ScreenDimensions): Shadeable(AsciiScreenMaterial(gl, res)), Screen
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
     * The glyph sheet texture
     */
    private val glyphTexture = JOGLTexture2D.FromImageResource(this.gl, this.res, "text.png")

    /**
     * The shadow texture
     */
    private val shadowTexture = JOGLTexture2D.FromImageResource(this.gl, this.res, "shadows.png")

    /**
     * The buffer texture used to hold the screen data. We use a
     */
    private var bufferTexture = BufferTexture(this.gl, this.bufferSize(size))

    /**
     * The data array that is used as backing storage for all screen operations. On render, its contents
     * are copied to the buffer texture.
     */
    private var data = IntArray(this.bufferSize(size))

    /**
     * The size of the screen, in glyphs
     */
    var size: ScreenDimensions = dimensionsInGlyphs(size)

    /**
     * The size of the screen, in pixels
     */
    var pixelSize: ScreenDimensions = size

    /**
     * Whether the screen contents have been changed and need to be reuploaded
     */
    private var dirty: Boolean = true

    /**
     * Initialize the glyph screen
     */
    init
    {
        this.resize(size)
    }

    /**
     * Update the screen size to the given, new screen dimensions (in pixels!)
     */
    fun resize(size: ScreenDimensions)
    {
        this.pixelSize = size

        // Recalculate screen size in glyphs
        this.size = this.dimensionsInGlyphs(size)

        // Resize data array
        this.data = IntArray(this.bufferSize(size))

        // Resize buffer texture
        this.bufferTexture.recreate(this.bufferSize(size))

        // Update the properties in the AsciiMaterial
        val mat = this.material as AsciiScreenMaterial
        mat.apply {
            glyphDimensions = this@DesktopScreen.glyphDimensions()
            screenDimensions = this@DesktopScreen.size
        }

        // A refresh is definitely needed
        this.dirty = true
    }

    /**
     * Calculates the screen dimensions in glyphs
     *
     * @return Dimensions of the screen in glyphs
     */
    fun dimensionsInGlyphs(screenSize: ScreenDimensions): ScreenDimensions
    {
        // Retrieve dimensions of a single glyph
        val glyphDimensions = this.glyphDimensions()

        return ScreenDimensions(
                screenSize.width / glyphDimensions.width,
                screenSize.height / glyphDimensions.height
        )
    }

    /**
     * Calculates the dimensions of a single glyph, in pixels
     *
     * @return Dimensions of a single glyph, in pixels
     */
    fun glyphDimensions(): ScreenDimensions
    {
        // The glyph sheet always contains 16x16 glyphs
        val sheetDimensions = ScreenDimensions(16, 16)

        return ScreenDimensions(
                this.glyphTexture.dimensions().width / sheetDimensions.width,
                this.glyphTexture.dimensions().height / sheetDimensions.height
        )
    }

    /**
     * Calculates the buffer size (in integers) needed to fit the whole screen data.
     *
     * @return Size of the screen data, in integers
     */
    fun bufferSize(screenSize: ScreenDimensions): Int
    {
        val screenDims = this.dimensionsInGlyphs(screenSize)

        // For each glyph, two four dimensional integer vectors are stored
        return screenDims.height * screenDims.width * 2 * 4
    }

    /**
     * Clear the screen
     */
    override fun clear()
    {
        this.data.fill(0)
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
        this.glyphTexture.use(TextureUnit.Unit0)
        this.shadowTexture.use(TextureUnit.Unit1)
        this.bufferTexture.use(TextureUnit.Unit2)

        // Render instanced quad for each glyph on the scree
        this.gl.glDrawArraysInstanced(GL4.GL_TRIANGLES, 0, 6, size.width * size.height)
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

        this.dirty = true
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
        if(depth < 0 || depth > 255)
            throw IllegalArgumentException("Depth value out of range: $depth")

        this.data[this.offsetOf(pos) + OFFSET_DATA] =
                (this.data[this.offsetOf(pos) + OFFSET_DATA] and 0xFF00.inv()) or depth

        this.dirty = true
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
     * Calculate the buffer offset for given screen position, in glyphs
     *
     * @param pos Screen position to convert to linear address, in glyphs
     * @return Linear buffer offset for given position
     */
    private fun offsetOf(pos: Position): Int
    {
        return (2 * 4) * ((this.size.width * pos.y) + pos.x)
    }

    /**
     * Retrieve useful debug information about the screen state
     *
     * @return Debug information, as a string
     */
    fun debugInfo(): String
    {
        return "\tScreen dimensions (in pixels): ${pixelSize.width}x${pixelSize.height}\n\tScreen dimensions (in glyphs):${size.width}x${size.height}\n" +
                "\tLocal buffer size (in ints): ${data.size}\n" +
                "\tGPU buffer texture handle: ${bufferTexture.handle}\n" +
                "\tGPU buffer handle: ${bufferTexture.bufferHandle}\n" +
                "\tGPU buffer size (in ints): ${bufferTexture.size}\n"
    }
}