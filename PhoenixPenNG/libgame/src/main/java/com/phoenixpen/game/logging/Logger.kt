package com.phoenixpen.game.logging

/**
 * Base class for classes that provide logging functionality
 */
abstract class Logger
{
    /**
     * Submit log message
     *
     * @param level The severity of the message
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    abstract fun log(level: LogLevel, tag: String, message: String)

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