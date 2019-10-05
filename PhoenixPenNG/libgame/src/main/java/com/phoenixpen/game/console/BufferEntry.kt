package com.phoenixpen.game.console

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.events.EventMessage
import com.phoenixpen.game.logging.LogLevel
import com.phoenixpen.game.logging.LogMessage
import java.util.*
import kotlin.math.min


/**
 * Enumeration describing the different types of buffer entries
 */
enum class BufferEntryType
{
    LogMessage,
    Event
}

/**
 * An entry in the console buffer. It might contain either an event message or a log message.
 *
 * @property type The type of this buffer entry
 */
class BufferEntry private constructor(val type: BufferEntryType)
{
    /**
     * Age threshold at which the buffer entry is considered to be "old" and thus will only
     * displayed as greyed-out
     */
    private val ageThreshold = 100

    /**
     * Color used for old, and thus greyed out, buffer entries
     */
    private val greyedOutColor = Color(156, 156, 156)

    /**
     * Maximum tag length
     */
    private val maxTagLength = 8

    /**
     * How old this entry is, in ticks. This is used to determine whether the console should
     * be showing this in a dark font, or not.
     */
    var age = 0

    /**
     * Storage space for log message
     */
    private var logMessage: Optional<LogMessage> = Optional.empty()

    /**
     * Storage space for event message
     */
    private var eventMessage: Optional<EventMessage> = Optional.empty()

    /**
     * Check whether this buffer entry is to be considered as being "old".
     *
     * @return Flag indicating whether this buffer entry is old.
     */
    fun isOld() = this.age >= this.ageThreshold

    /**
     * Draw this buffer entry as part of a console
     *
     * @param screen Screen to draw to
     * @param position Position of the first character
     * @param width Maximum width of whole line
     */
    fun render(screen: Screen, position: Position, width: Int)
    {
        // Draw tag, always greyed out
        val tagStr =  this.tagString()
        screen.putString(position, tagStr, front = this.greyedOutColor)

        // Determine remaining width for the text
        val remainingWidth = width - tagStr.length

        // Draw message
        screen.putString(position, this.messageString(remainingWidth), if (this.isOld()) this.greyedOutColor else this.color())
    }

    /**
     * Create string representing the message tag
     */
    private fun tagString(): String
    {
        val contents = when(this.type)
        {
            BufferEntryType.Event -> this.eventMessage().source
            BufferEntryType.LogMessage -> this.logMessage().tag
        }

        return "[${this.trim(contents, this.maxTagLength).padEnd(this.maxTagLength)}] "
    }

    /**
     * Determine message color
     */
    private fun color(): Color
    {
        return when(this.type)
        {
            BufferEntryType.LogMessage -> when(this.logMessage().level)
            {
                LogLevel.Debug -> this.greyedOutColor
                LogLevel.Error -> Color.red
                LogLevel.Warning -> Color.yellow
                LogLevel.Info -> this.greyedOutColor
            }
            BufferEntryType.Event -> this.eventMessage().color
        }
    }

    /**
     * Create string representing the message contents
     */
    private fun messageString(width: Int): String
    {
        val contents = when(this.type)
        {
            BufferEntryType.Event -> this.eventMessage().message
            BufferEntryType.LogMessage -> this.logMessage().message
        }

        return this.trim(contents, width).padEnd(width)
    }

    /**
     * Trim string to be at most [maxLen] long
     */
    private fun trim(string: String, maxLen: Int): String = string.substring(0, min(string.length, maxLen))

    /**
     * Retrieve event message
     */
    private fun eventMessage(): EventMessage = this.eventMessage.get()

    /**
     * Retrieve log message
     */
    private fun logMessage(): LogMessage = this.logMessage.get()

    /**
     * Builder methods
     */
    companion object
    {
        /**
         * Create buffer entry from log message
         */
        fun fromLogMessage(message: LogMessage): BufferEntry
        {
            val entry = BufferEntry(BufferEntryType.LogMessage)
            entry.logMessage = Optional.of(message)
            return entry
        }

        /**
         * Create buffer entry from event
         */
        fun fromEvent(event: EventMessage): BufferEntry
        {
            val entry = BufferEntry(BufferEntryType.Event)
            entry.eventMessage = Optional.of(event)
            return entry
        }
    }
}