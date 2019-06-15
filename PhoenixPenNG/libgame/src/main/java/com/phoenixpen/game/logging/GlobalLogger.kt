package com.phoenixpen.game.logging

import java.util.*

/**
 * A global logger instance, which makes access to logging facilities from anywhere in the game logic
 * easier. Must be initialized using [setLogger] before usage.
 */
object GlobalLogger
{
    /**
     * The logger instance
     */
    private var loggerInstance: Optional<Logger> = Optional.empty()

    /**
     * Retrieve logger instance. Will throw if logger wasn't set using [setLogger].
     *
     * @return The current logger instance
     */
    fun instance(): Logger
    {
        if(this.loggerInstance.isPresent)
            return this.loggerInstance.get()
        else throw IllegalStateException("GlobalLogger: logger not set")
    }

    /**
     * Set given logger instance as global logger
     *
     * @param logger Logger instance to set as global logger
     */
    fun setLogger(logger: Logger)
    {
        this.loggerInstance = Optional.of(logger)
    }

    /**
     * Submit an error message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun e(tag: String, message: String)
    {
        this.instance().e(tag, message)
    }

    /**
     * Submit a info message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun i(tag: String, message: String)
    {
        this.instance().i(tag, message)
    }

    /**
     * Submit a warning message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun w(tag: String, message: String)
    {
        this.instance().w(tag, message)
    }

    /**
     * Submit a debug message
     *
     * @param tag The identifying tag, can be used to signal source of message
     * @param message The actual message
     */
    fun d(tag: String, message: String)
    {
        this.instance().d(tag, message)
    }
}