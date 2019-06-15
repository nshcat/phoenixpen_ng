package com.phoenixpen.android.application

/**
 * A class implementing FPS calculation using a moving average
 */
class FpsCounter
{
    /**
     * The current FPS value.
     */
    var fps: Double = 0.0

    /**
     * Current position in the sample window
     */
    private var currentPos: Int = 0

    /**
     * The width of the sample window
     */
    private val windowWidth: Int = 32

    /**
     * The sample window
     */
    private val window: IntArray = IntArray(this.windowWidth)

    /**
     * Incorporate a new frame duration sample into the FPS calculation
     *
     * @param sample New frame duration sample
     */
    fun addSample(sample: Int)
    {
        // Store sample in window, possibly overwriting the oldest value
        this.window[this.currentPos] = sample

        // Adjust position in window for next sample
        this.currentPos = (currentPos + 1) % this.windowWidth

        // Recalculate FPS
        val totalFrameTime = this.window.sum().toDouble() / 1000.0

        if(totalFrameTime == 0.0)
            this.fps = 0.0
        else
            this.fps = this.windowWidth.toDouble() / totalFrameTime
    }
}