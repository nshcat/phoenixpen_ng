package com.phoenixpen.desktop.application

import com.phoenixpen.game.logging.LogLevel
import com.phoenixpen.game.logging.LogMessage
import com.phoenixpen.game.logging.Logger

/**
 * Logger implementation for the desktop application
 */
class DesktopLogger: Logger()
{
    /**
     * Print log message to console
     *
     * @param level Message severity
     * @param tag Message tag
     * @param message Message body
     */
    override fun log(message: LogMessage)
    {
        println("${message.tag}: ${message.level}: ${message.message}")
    }
}