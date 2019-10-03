package com.phoenixpen.game.ascii

import kotlin.math.min

/**
 * Print given string to screen at given position. Will cut off the string if it wont fit on screen.
 *
 * @param pos Position of the first character
 * @param str String to print
 * @return Position of one character after the last printed one. Useful to print multiple things.
 */
fun Screen.putString(pos: Position, str: String, front: Color = Color.white, back: Color = Color.black): Position
{
    // Check if the initial position is inside the screen
    if(!this.isInBounds(pos))
        return pos

    // Calculate number of characters to draw
    val toDraw = min(this.getDimensions().width-1 - pos.x, str.length)

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

    pos.x += toDraw

    return Position(pos.x + toDraw, pos.y)
}

/**
 * Check whether given position is inside screen boundaries.
 *
 * @param pos Position to check
 * @return Flag indicating whether the position is inside screen boundaries.
 */
fun Screen.isInBounds(pos: Position): Boolean
{
    val dimensions = this.getDimensions()

    return pos.x >= 0 && pos.x < dimensions.width && pos.y >= 0 && pos.y < dimensions.height
}