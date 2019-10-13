package com.phoenixpen.game.graphics

import com.phoenixpen.game.ascii.Position
import kotlin.math.min

/**
 * Print given string to surface at given position. Will cut off the string if it wont fit on screen.
 *
 * @param pos Position of the first character. This is modified to point to the glyph after the last printed one, if [modifyInput] is set to true.
 * @param str String to print
 * @param front Front color
 * @param back Back color
 * @param modifyInput Whether to modify the given position to point at the glyph after the last printed one
 * @return Position of one character after the last printed one. Useful to print multiple things.
 */
fun Surface.putString(pos: Position, str: String, front: Color = Color.white, back: Color = Color.black, modifyInput: Boolean = true): Position
{
    // Check if the initial position is inside the screen
    if(!this.isInBounds(pos))
        return pos

    // Calculate number of characters to draw
    val toDraw = min(this.dimensionsInGlyphs.width - pos.x, str.length)

    for(i in 0 until toDraw)
    {
        // Get ascii value of character
        val glyph = str[i].toInt()
        val pos = Position(pos.x + i, pos.y)

        this.setGlyph(pos, glyph)
        this.setFrontColor(pos, front)
        this.setBackColor(pos, back)
        this.setDepth(pos, 0)
        this.clearShadows(pos)
    }

    if(modifyInput)
        pos.x += toDraw

    return Position(pos.x + toDraw, pos.y)
}

/**
 * Check whether given glyph position is inside surface boundaries.
 *
 * @param pos Position to check
 * @return Flag indicating whether the position is inside screen boundaries.
 */
fun Surface.isInBounds(pos: Position): Boolean
{
    val dimensions = this.dimensionsInGlyphs

    return pos.x >= 0 && pos.x < dimensions.width && pos.y >= 0 && pos.y < dimensions.height
}