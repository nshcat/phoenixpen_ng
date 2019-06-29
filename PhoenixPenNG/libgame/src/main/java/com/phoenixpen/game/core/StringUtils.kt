package com.phoenixpen.game.core

/**
 * Format given double value with given number of digits.
 */
fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)