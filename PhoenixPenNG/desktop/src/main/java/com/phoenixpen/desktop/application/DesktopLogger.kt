package com.phoenixpen.desktop.application

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
     * @param message Log message to print
     */
    override fun log(message: LogMessage)
    {
        println("${message.tag}: ${message.level}: ${message.message}")
    }
}