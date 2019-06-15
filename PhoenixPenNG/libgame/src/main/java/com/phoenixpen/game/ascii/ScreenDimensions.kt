package com.phoenixpen.game.ascii

/**
 * A class encapsulating the dimensions of the screen that is the current rendering target.
 *
 * @property width The width of the screen, in pixels
 * @property height The height of the screen, in pixels
 */
class ScreenDimensions(val width: Int, val height: Int)
{
    /**
     * The aspect ratio of the screen
     */
    val aspectRatio: Float = width.toFloat() / height.toFloat()

    /**
     * Check if the screen dimensions describe an empty screen.
     */
    fun isEmpty(): Boolean = this.width == 0 && this.height == 0

    /**
     * Obtain a new screen dimensions instance with scaled-down width and height.
     *
     * @param factor The scaling factor. The dimensions will be divided by this.
     * This means a value of 2.0 will result in half the resolution in both width and height.
     */
    fun scaleDown(factor: Float): ScreenDimensions
    {
        return ScreenDimensions((this.width.toFloat() / factor).toInt(), (this.height.toFloat() / factor).toInt())
    }

    companion object
    {
        /**
         * Create empty screen dimensions instance
         */
        fun empty(): ScreenDimensions = ScreenDimensions(0, 0)
    }
}
