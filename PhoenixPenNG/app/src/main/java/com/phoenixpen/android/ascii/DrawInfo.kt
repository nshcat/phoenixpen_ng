package com.phoenixpen.android.ascii

import kotlinx.serialization.Serializable

/**
 * A triple containing glyph id, foreground color and background color.
 * Used in a lot of places, e.g. to specify how an actor is supposed to be drawn.
 *
 * @p
 */
@Serializable
data class DrawInfo(var glyph: Int, @Serializable(with=ColorSerializer::class) var foreground: Color, @Serializable(with=ColorSerializer::class) var background: Color)