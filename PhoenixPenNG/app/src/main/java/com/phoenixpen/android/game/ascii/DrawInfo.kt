package com.phoenixpen.android.game.ascii

import kotlinx.serialization.Serializable

/**
 * A triple containing glyph id, foreground color and background color.
 * Used in a lot of places, e.g. to specify how an actor is supposed to be drawn.
 */
@Serializable
data class DrawInfo(
        var glyph: Int = 0,
        @Serializable(with=ColorSerializer::class) var foreground: Color = Color.black,
        @Serializable(with=ColorSerializer::class) var background: Color = Color.black)