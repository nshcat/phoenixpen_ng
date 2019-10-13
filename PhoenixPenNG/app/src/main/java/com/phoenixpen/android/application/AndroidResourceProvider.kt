package com.phoenixpen.android.application

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.resources.Bitmap
import com.phoenixpen.game.resources.ResourceProvider
import org.apache.commons.io.FilenameUtils
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * A resource providing based on the Android application resources
 *
 * @property ctx The android application context
 */
class AndroidResourceProvider(val ctx: Context): ResourceProvider
{
    override fun text(id: String): String
    {
        // Retrieve resource id
        val id = this.findResourceId(id, "raw")

        // Get resource and read all text
        return BufferedReader(InputStreamReader(this.ctx.resources.openRawResource(id))).readText()
    }

    override fun json(id: String): String
    {
        // JSON files are stored the same way as ordinary text files on Android
        return this.text(id)
    }

    override fun bitmap(id: String): Bitmap
    {
        // Retrieve resource id
        val id = this.findResourceId(id, "drawable")

        // Make sure the bitmap is no stretched
        val factoryOptions = BitmapFactory.Options().apply { inScaled = false }
        val androidBitmap = BitmapFactory.decodeResource(this.ctx.resources, id, factoryOptions)

        // Create game bitmap
        val bitmap = Bitmap(androidBitmap.width, androidBitmap.height)

        // Copy all pixels
        for(ix in 0 until bitmap.width)
        {
            for(iy in 0 until bitmap.height)
            {
                // Retrieve android bitmap pixel
                val androidPixel = androidBitmap.getPixel(ix, iy)

                // Convert to game color instance
                val color = com.phoenixpen.game.graphics.Color(
                        Color.red(androidPixel),
                        Color.green(androidPixel),
                        Color.blue(androidPixel)
                )

                // Store
                bitmap.setPixelAt(Position(ix, iy), color)
            }
        }

        return bitmap
    }

    /**
     * Strip the extension from given resource id.
     *
     * @param id Resource id to strip file extension from
     * @return Resource id without file extension
     */
    private fun stripExtension(id: String): String
    {
        return FilenameUtils.removeExtension(id)
    }

    private fun findResourceId(id: String, type: String): Int
    {
        // First strip the file extension
        val stripped = this.stripExtension(id)

        // Retrieve android resource id
        return this.ctx.resources.getIdentifier(stripped, type, "com.nshcat.phoenixpen")
    }

    /**
     * Retrieve android resource ID for glyph tile set texture with given file name
     */
    fun getTextureId(filename: String): Int
    {
        return this.findResourceId(filename, "drawable")
    }
}