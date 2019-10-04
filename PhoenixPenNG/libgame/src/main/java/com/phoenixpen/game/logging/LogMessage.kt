package com.phoenixpen.game.logging

/**
 * A class containing all information about a singular log message
 *
 * @property level The message severity
 * @property tag The source tag string
 * @property message The message body
 */
data class LogMessage(
        val level: LogLevel,
        val tag: String,
        val message: String
)