package com.phoenixpen.game.input

/**
 * Enumeration of opaque key values
 */
enum class Key
{
    // Arrow keys
    Up,
    Down,
    Left,
    Right,

    // Letter keys
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,

    // Numbers
    Number1, Number2, Number3, Number4, Number5, Number6, Number7, Number8, Number9, Number0,

    // Modifier keys
    LShift, RShift, LAlt, LCtrl, RCtrl,

    PageUp, PageDown, LAngle, RAngle,

    // Special keys
    Return, Backspace, Space, Escape, Super
}

/**
 * Enumeration of opaque key modifier values
 */
enum class Modifier
{
    Shift, Control, Alt, AltGr, Super
}