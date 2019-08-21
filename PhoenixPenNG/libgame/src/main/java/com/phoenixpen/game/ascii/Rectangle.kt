package com.phoenixpen.game.ascii

/**
 * A class describing a 2D rectangle, given a top left and a bottom right position.
 *
 * @property topLeft The top left point of the rectangle
 * @property bottomRight The bottom right point of the rectangle
 */
class Rectangle(var topLeft: Position, var bottomRight: Position)
{
    /**
     * Check if a given point lies inside this rectangle
     *
     * @param point Point to check
     * @return Flag indicating whether the given point lies inside the rectangle
     */
    fun isInside(point: Position): Boolean
    {
        return point.x >= this.topLeft.x && point.x <= this.bottomRight.x
                && point.y >= this.topLeft.y && point.y <= this.bottomRight.y
    }

    /**
     * Helper builder functions and constants
     */
    companion object
    {
        /**
         * Create a rectangle with a top left corner in (0,0) and given dimensions
         *
         * @param dimensions Dimensions of the rectangle
         * @return Rectangle based on given dimensions
         */
        fun fromDimensions(dimensions: ScreenDimensions): Rectangle
        {
            return Rectangle(Position(), dimensions.toPosition())
        }
    }
}