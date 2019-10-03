package com.phoenixpen.game.console

import com.phoenixpen.game.ascii.*
import com.phoenixpen.game.core.Observer
import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.events.EventMessage
import com.phoenixpen.game.events.GlobalEvents
import com.phoenixpen.game.input.EnumEvent
import com.phoenixpen.game.input.InputProvider
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.logging.LogMessage
import java.util.*
import kotlin.math.min

/**
 * Enumeration describing the current state of a console component
 */
enum class ConsoleState
{
    /**
     * Console widget is hidden
     */
    Hidden,

    /**
     * Console widget just shows log messages, but does not grab input (other than the mode switch key)
     */
    Log,

    /**
     * Console widget is shown and is fully interactive, with a command prompt. All input is grabbed.
     */
    Interactive
}

/**
 * Enumeration detailing in which height mode the console widget currently is
 */
enum class ConsoleHeightMode
{
    /**
     * Just show a few lines
     */
    Compact,

    /**
     * Show more lines
     */
    Full
}

/**
 * A scene component that can be used to show log and event messages and interact with the game using
 * textual commands
 */
class Console(val input: InputProvider): SceneComponent
{
    /**
     * Tick counter used to blink the prompt cursor
     */
    private val cursorBlinker = TickCounter(14)

    /**
     * Whether to show the prompt cursor right now
     */
    private var promptShowCursor = true

    /**
     * The input adapter that is used when in interactive mode
     */
    private val adapter = ConsoleInputAdapter(input)

    /**
     * Buffer holding log and event messages. New messages are are attached to the front.
     */
    private val buffer = LinkedList<BufferEntry>()

    /**
     * The maximum buffer size. If the buffer exceeds this value, old entries will be discarded.
     */
    private val maxBufferSize = 256

    /**
     * Whether to show log messages in console
     */
    var showLogMessages: Boolean = false

    /**
     * Whether to show event messages in console
     */
    var showEventMessages : Boolean = true

    /**
     * The current console state
     */
    var currentState = ConsoleState.Hidden

    /**
     * The current height mode of the console
     */
    var heightMode = ConsoleHeightMode.Compact

    /**
     * Height in compact mode
     */
    var heightCompact = 5

    /**
     * Height in full mode
     */
    var heightFull = 15

    /**
     * The current input buffer
     */
    var inputBuffer: String = ""

    /**
     * Color used for old, and thus greyed out, buffer entries
     */
    private val textColor = Color(156, 156, 156)

    /**
     * Create observers
     */
    init
    {
        GlobalLogger.instance().subscribe(object : Observer<LogMessage> {
            override fun notify(value: LogMessage) {
                if(showLogMessages) {
                    addBufferEntry(BufferEntry.fromLogMessage(value))
                }
            }
        })

        GlobalEvents.subscribe(object : Observer<EventMessage> {
            override fun notify(value: EventMessage) {
                if(showEventMessages) {
                    addBufferEntry(BufferEntry.fromEvent(value))
                }
            }
        })

        GlobalLogger.d("Console", "Console initialized")
    }

    /**
     * Add given buffer entry to the console buffer
     *
     * @param entry Entry to add to buffer
     */
    private fun addBufferEntry(entry: BufferEntry)
    {
        this.buffer.addFirst(entry)

        if(this.buffer.size > this.maxBufferSize)
            this.buffer.removeLast()
    }

    /**
     * Check whether this component currently grabs full input
     */
    fun grabsInput() = this.currentState == ConsoleState.Interactive

    /**
     * Perform internal logic update based on given number of elapsed ticks
     *
     * @param elapsedTicks Number of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // We need to update the age of buffer entries, but only up until the point we find an entry
        // that is already considered to be old.
        for(entry in this.buffer)
        {
            if(entry.isOld())
                break

            entry.age += elapsedTicks
        }

        when(this.currentState)
        {
            ConsoleState.Interactive ->
            {
                this.handleInput()

                // Handle cursor blinking
                this.blinkCursor(elapsedTicks)
            }
            else -> return
        }
    }

    /**
     * Draw console to screen
     *
     * @param screen Screen to draw to
     */
    override fun render(screen: Screen)
    {
        when(this.currentState)
        {
            ConsoleState.Hidden -> return
            ConsoleState.Log -> this.drawMessages(screen)
            ConsoleState.Interactive ->
            {
                this.drawMessages(screen)
                this.drawPrompt(screen)
            }
        }
    }

    /**
     * Perform cursor blinking
     */
    private fun blinkCursor(elapsedTicks: Int)
    {
        if(this.cursorBlinker.update(elapsedTicks) > 0)
            this.promptShowCursor = !this.promptShowCursor
    }

    /**
     * Draw interactive mode prompt
     */
    private fun drawPrompt(screen: Screen)
    {
        // Determine y pos of prompt
        var yPos = if (this.heightMode == ConsoleHeightMode.Full) this.heightFull else this.heightCompact

        // Draw static prompt
        screen.putString(Position(0, yPos), ">", front = this.textColor)

        // Draw user input
        val maxInputLen = screen.getDimensions().width - 1

        val displayString = this.inputBuffer.substring(0, min(maxInputLen, this.inputBuffer.length))
                .padEnd(maxInputLen)

        screen.putString(Position(1, yPos), displayString, front = this.textColor)


        // Draw cursor
        if(this.inputBuffer.length < maxInputLen)
        {
            val xPos = 1 + this.inputBuffer.length

            val color = if (this.promptShowCursor) this.textColor else Color.black
            screen.putString(Position(xPos, yPos), "_", front = color)
        }

    }

    /**
     * Handle input in interactive mode
     */
    private fun handleInput()
    {
        this.adapter.update()

        // TODO find way to fix this without this hack
        var wasSpecialKey = false

        if(this.adapter.hasEvents())
        {
            // Consume all events
            val events = this.adapter.consumeEvents()

            for(event in events)
            {
                if(event is EnumEvent<*>)
                {
                    when ((event as EnumEvent<ConsoleInput>).value)
                    {
                        ConsoleInput.CloseConsole ->
                        {
                            this.currentState = ConsoleState.Hidden
                            return
                        }

                        ConsoleInput.ToggleState ->
                        {
                            this.toggleState()

                            if(this.currentState != ConsoleState.Interactive)
                                return
                        }

                        ConsoleInput.ToggleHeightMode -> this.toggleHeightMode()

                        ConsoleInput.ExecuteCommand -> wasSpecialKey = true
                        ConsoleInput.EraseCharacter ->
                        {
                            wasSpecialKey = true
                            if(this.inputBuffer.isNotEmpty())
                                this.inputBuffer = this.inputBuffer.substring(0, this.inputBuffer.length - 1)

                        }

                    }
                }

            }
        }

        // Handle text input. [wasBackspace] is a hack to avoid the numerical code of special keys to appear
        if(!wasSpecialKey && this.input.hasText())
        {
            this.inputBuffer += this.input.text()
        }
    }

    /**
     * Switch to next console state
     */
    fun toggleState()
    {
        this.currentState = when(this.currentState)
        {
            ConsoleState.Hidden -> ConsoleState.Log
            ConsoleState.Log -> ConsoleState.Interactive
            ConsoleState.Interactive -> ConsoleState.Hidden
        }

        // Clear input buffer if entered interactive mode
        if(this.currentState == ConsoleState.Interactive)
            this.inputBuffer = ""
    }

    /**
     * Switch to next height mode
     */
    fun toggleHeightMode()
    {
        this.heightMode = when(this.heightMode)
        {
            ConsoleHeightMode.Full -> ConsoleHeightMode.Compact
            ConsoleHeightMode.Compact -> ConsoleHeightMode.Full
        }
    }

    /**
     * Draw as many messages as possible
     */
    private fun drawMessages(screen: Screen)
    {
        // Calculate how many messages we can fit
        val numMessages = if (this.heightMode == ConsoleHeightMode.Full) this.heightFull else this.heightCompact

        // Where to start drawing messages, from bottom to top
        var startIndex = numMessages - 1

        // Where we need to start blanking (if not enough log messages are available)
        var blankStartIndex = numMessages

        // Do we have enough messages?
        if(this.buffer.size < numMessages)
        {
            // Determine difference
            val diff = numMessages - this.buffer.size

            // Adjust index
            startIndex -= diff

            blankStartIndex = startIndex + 1
        }

        var i = startIndex
        for(entry in this.buffer)
        {
            entry.render(screen, Position(0, i), screen.getDimensions().width)

            i--
            if(i < 0)
                break
        }

        for(i in blankStartIndex until numMessages)
            screen.putString(Position(0, i), "".padEnd(screen.getDimensions().width))

        this.drawDivider(screen)
    }

    /**
     * Draw the divider that is located at the bottom of the console
     */
    private fun drawDivider(screen: Screen)
    {
        var yPos = if (this.heightMode == ConsoleHeightMode.Full) this.heightFull else this.heightCompact

        // In interactive mode, the command prompt moves the divider one glyph further down
        if(this.currentState == ConsoleState.Interactive)
            ++yPos

        screen.putString(Position(0, yPos), "\u00C4".repeat(screen.getDimensions().width), front = this.textColor)
    }
}