package com.phoenixpen.android.ascii

import android.content.Context
import android.opengl.GLES31
import com.phoenixpen.android.R
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.rendering.*
import com.phoenixpen.android.rendering.materials.AsciiScreenMaterial
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

/**
 * A screen made up from a matrix of coloured ASCII glyphs.
 *
 * @property size Screen dimensions, in PIXELS
 */
class Screen(val context: Context, var size: ScreenDimensions): Shadeable(AsciiScreenMaterial())
{
    /**
     * The glyph sheet texture
     */
    private val glyphTexture = Texture2D.FromImageResource(this.context, R.drawable.text)

    /**
     * The shadow texture
     */
    private val shadowTexture = Texture2D.FromImageResource(this.context, R.drawable.shadows)

    /**
     * The buffer texture used to hold the screen data. We use a
     */
    private var bufferTexture = BufferTexture(this.bufferSize())

    /**
     * The data array that is used as backing storage for all screen operations. On render, its contents
     * are copied to the buffer texture.
     */
    private var data = IntArray(this.bufferSize())

    /**
     * The size of the screen, in glyphs
     */
    var sizeInGlyphs: ScreenDimensions = dimensionsInGlyphs()

    /**
     * Whether the screen contents have been changed and need to be reuploaded
     */
    private var dirty: Boolean = true

    /**
     * Initialize the glyph screen
     */
    init
    {
        this.resize(this.size)
    }

    /**
     * Update the screen size to the given, new screen dimensions (in pixels!)
     */
    fun resize(size: ScreenDimensions)
    {
        // Set the current size
        this.size = size

        // Recalculate screen size in glyphs
        this.sizeInGlyphs = this.dimensionsInGlyphs()

        // Resize data array
        this.data = IntArray(this.bufferSize())

        // Resize buffer texture
        this.bufferTexture.recreate(this.bufferSize())

        // Update the properties in the AsciiMaterial
        val mat = this.material as AsciiScreenMaterial
        mat.apply {
            glyphDimensions = this@Screen.glyphDimensions()
            screenDimensions = this@Screen.sizeInGlyphs
        }

        // A refresh is definitely needed
        this.dirty = true
    }

    /**
     * Calculates the screen dimensions in glyphs
     *
     * @return Dimensions of the screen in glyphs
     */
    fun dimensionsInGlyphs(): ScreenDimensions
    {
        // Retrieve dimensions of a single glyph
        val glyphDimensions = this.glyphDimensions()

        return ScreenDimensions(
                this.size.width / glyphDimensions.width,
                this.size.height / glyphDimensions.height
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
    fun bufferSize(): Int
    {
        val screenDims = this.dimensionsInGlyphs()

        // For each glyph, two four dimensional integer vectors are stored
        return screenDims.height * screenDims.width * 2 * 4
    }

    /**
     * Clear the screen
     */
    fun clear()
    {
        val first: Int = 0xFFFFFF13U.toInt()

        for(ix in 0 until this.sizeInGlyphs.width * this.sizeInGlyphs.height)
        {
            data[(ix*8)] = 73
            data[(ix*8)+1] = 246
            data[(ix*8)+2] = 194

            data[(ix*8)+3] = 140

            data[(ix*8)+4] = 0
            data[(ix*8)+5] = 0
            data[(ix*8)+6] = 0
            data[(ix*8)+7] = 0

        }


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
            val byteBuffer = ByteBuffer.allocateDirect(this.bufferSize() * 4)
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
        GLES31.glDrawArraysInstanced(GLES31.GL_TRIANGLES, 0, 6, sizeInGlyphs.width * sizeInGlyphs.height)
    }
}