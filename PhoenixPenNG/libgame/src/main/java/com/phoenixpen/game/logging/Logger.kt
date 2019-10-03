package com.phoenixpen.game.logging

import com.phoenixpen.game.core.Observable

/**
 * Base class for classes that provide logging functionality
 */
abstract class Logger: Observable<LogMessage>()
{
    /**
     * Submit log message
     *
     * @param level The severity of the message
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    protected fun log(level: LogLevel, tag: String, message: String)
    {
        val logMessage = LogMessage(level, tag, message)

        this.notifyAll(logMessage)

        this.log(logMessage)
    }

    /**
     * Submit and process log message in platform dependant way
     *
     * @param message The log message
     */
    protected abstract fun log(message: LogMessage)

    /**
     * Submit an error message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun e(tag: String, message: String)
    {
        this.log(LogLevel.Error, tag, message)
    }

    /**
     * Submit a info message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun i(tag: String, message: String)
    {
        this.log(LogLevel.Info, tag, message)
    }

    /**
     * Submit a warning message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun w(tag: String, message: String)
    {
        this.log(LogLevel.Warning, tag, message)
    }

    /**
     * Submit a debug message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun d(tag: String, message: String)
    {
        this.log(LogLevel.Debug, tag, message)
    }
}