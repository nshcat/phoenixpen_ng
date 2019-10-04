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

    // Functions keys
    F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12,

    // Numbers
    Number1, Number2, Number3, Number4, Number5, Number6, Number7, Number8, Number9, Number0,

    // Modifier keys
    Shift, Alt, Ctrl, AltGr,

    PageUp, PageDown, Less,

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