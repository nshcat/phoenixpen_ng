package com.phoenixpen.game.core

/**
 * Extension function that works like a guarded [apply]. If [condition] is true, the given [block]
 * will be executed in the context of the [this] object. If not, nothing happens.
 *
 * @param condition Guard condition that determines if the [block] should be executed
 * @param block Code block to be applied to the [this] object, if [condition] is true
 */
inline fun <T> T.applyIf(condition : Boolean, block : T.() -> Unit) : T = apply {
    if(condition) block(this)
}