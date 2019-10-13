package com.phoenixpen.game.resources

import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.graphics.Color

/**
 * A class containing bitmap data. This is done to allow platform independence.
 *
 * @property width The width of the bitmap, in pixels
 * @property height The height of the bitmap, in pixels
 */
class Bitmap(val width: Int, val height: Int)
{
    /**
     * Collection of all pixels in this bitmap.
     */
    val pixels = ArrayList<Color>(width * height)

    /**
     * Initialize all pixels to black.
     */
    init
    {
        for(i in 0 until width * height)
        {
            this.pixels.add(Color.black)
        }
    }

    /**
     * Retrieve pixel at given position
     *
     * @param position Position to retrieve pixel from
     * @return Pixel at given position, if in bounds
     */
    fun pixelAt(position: Position): Color
    {
        // Perform bounds check
        if(!this.isInBounds(position))
            throw IllegalArgumentException("Bitmap::pixelAt: Position out of bitmap boundfs")

        return this.pixels[this.indexFor(position)]
    }

    /**
     * Set pixel at given position
     *
     * @param position Position to set pixel at
     * @param pixel New pixel data
     */
    fun setPixelAt(position: Position, pixel: Color)
    {
        // Perform bounds check
        if(!this.isInBounds(position))
            throw IllegalArgumentException("Bitmap::setPixelAt: Position out of bitmap boundfs")

        this.pixels[this.indexFor(position)] = pixel
    }

    /**
     * Calculate linear index into [pixels] list for given two dimensional position
     *
     * @param position Position to calculate linear index for
     * @return Linear index for given position
     */
    fun indexFor(position: Position): Int
    {
        return (position.y * this.width) + position.x
    }

    /**
     * Check whether the given position is inside bitmap bounds
     *
     * @param position Position to check
     * @return Flag indicating whether given position is inside bitmap bounds
     */
    fun isInBounds(position: Position): Boolean
    {
        return (position.x >= 0 && position.x < this.width) && (position.y >= 0 && position.y < this.height)
    }
}