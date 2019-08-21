package com.phoenixpen.desktop.application

import com.phoenixpen.game.input.*
import java.awt.Event
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * Input provider for the desktop app. Does nothing for now.
 */
class DesktopInputProvider: InputProvider, KeyAdapter()
{
    /**
     * Set of all currently pressed keys.
     */
    private val pressedKeys = HashSet<Key>()

    /**
     * Set of all currently pressed keys
     */
    private val activeModifiers = HashSet<Modifier>()

    /**
     * Mapping between AWT key codes and the platform agnostic [Key] enumeration provided
     * by libgame
     */
    private val keyCodeMapping: HashMap<Int, Key>

    /**
     * A character buffer used to store text input
     */
    private var textBuffer = ArrayList<Char>(128)

    /**
     * Initialize internal state
     */
    init
    {
        // Initialize mapping between AWT key codes and the platform agnostic [Key] enumeration
        this.keyCodeMapping = hashMapOf(
                // Number keys
                KeyEvent.VK_0 to Key.Number0,
                KeyEvent.VK_1 to Key.Number1,
                KeyEvent.VK_2 to Key.Number2,
                KeyEvent.VK_3 to Key.Number3,
                KeyEvent.VK_4 to Key.Number4,
                KeyEvent.VK_5 to Key.Number5,
                KeyEvent.VK_6 to Key.Number6,
                KeyEvent.VK_7 to Key.Number7,
                KeyEvent.VK_8 to Key.Number8,
                KeyEvent.VK_9 to Key.Number9,

                // Letter keys
                KeyEvent.VK_A to Key.A,
                KeyEvent.VK_B to Key.B,
                KeyEvent.VK_C to Key.C,
                KeyEvent.VK_D to Key.D,
                KeyEvent.VK_E to Key.E,
                KeyEvent.VK_F to Key.F,
                KeyEvent.VK_G to Key.G,
                KeyEvent.VK_H to Key.H,
                KeyEvent.VK_I to Key.I,
                KeyEvent.VK_J to Key.J,
                KeyEvent.VK_K to Key.K,
                KeyEvent.VK_L to Key.L,
                KeyEvent.VK_M to Key.M,
                KeyEvent.VK_N to Key.N,
                KeyEvent.VK_O to Key.O,
                KeyEvent.VK_P to Key.P,
                KeyEvent.VK_Q to Key.Q,
                KeyEvent.VK_R to Key.R,
                KeyEvent.VK_S to Key.S,
                KeyEvent.VK_T to Key.T,
                KeyEvent.VK_U to Key.U,
                KeyEvent.VK_V to Key.V,
                KeyEvent.VK_W to Key.W,
                KeyEvent.VK_X to Key.X,
                KeyEvent.VK_X to Key.Y,
                KeyEvent.VK_Z to Key.Z,

                // Special keys
                KeyEvent.VK_LEFT to Key.Left,
                KeyEvent.VK_RIGHT to Key.Right,
                KeyEvent.VK_UP to Key.Up,
                KeyEvent.VK_DOWN to Key.Down,
                KeyEvent.VK_CONTROL to Key.Ctrl,
                KeyEvent.VK_SHIFT to Key.Shift,
                KeyEvent.VK_BACK_SPACE to Key.Backspace,
                KeyEvent.VK_ENTER to Key.Return,
                KeyEvent.VK_ALT to Key.Alt,
                KeyEvent.VK_ALT_GRAPH to Key.AltGr,
                KeyEvent.VK_SPACE to Key.Space,
                KeyEvent.VK_ESCAPE to Key.Escape,
                KeyEvent.VK_WINDOWS to Key.Super,
                KeyEvent.VK_LESS to Key.Less,
                KeyEvent.VK_PAGE_UP to Key.PageUp,
                KeyEvent.VK_PAGE_DOWN to Key.PageDown
        )
    }

    /**
     * React to key events from the [KeyAdapter]
     *
     * @param event Event describing the key press that occurred
     */
    override fun keyPressed(event: KeyEvent?)
    {
        // The event might be null. Check for that.
        if(event != null)
        {
            // Retrieve key code
            val keyCode = event.keyCode

            // Can we deal with this key here? If not, ignore.
            if(this.keyCodeMapping.containsKey(keyCode))
            {
                // Retrieve associated [Key] enumeration value. We are sure
                // that it exists here.
                val key = this.keyCodeMapping.getValue(keyCode)

                // Store in pressed key set
                this.pressedKeys.add(key)

                // Extract all modifiers and add to the active modifiers set
                this.extractModifiers(event)
            }
        }
    }

    /**
     * Handle key text input
     *
     * @param event Key typed event to process
     */
    override fun keyTyped(event: KeyEvent?)
    {
        // Event might be null
        if(event != null)
        {
            // Handle event as text input
            this.handleTextInput(event)
        }
    }

    /**
     * Handle given key press event as text input, if possible.
     *
     * @param event Key press event to process
     */
    private fun handleTextInput(event: KeyEvent)
    {
        // Retrieve key character. This automatically applies things like shift (shift + a will result
        // in an uppercase A)
        val char = event.keyChar

        // Store in text buffer
        this.textBuffer.add(char)
    }

    /**
     * Extract all active key modifiers (e.h. shift) from given key press event.
     *
     * @param event Key press event to process
     */
    private fun extractModifiers(event: KeyEvent)
    {
        // This is quite messy, but it works for now
        if(event.isAltDown)
            this.activeModifiers.add(Modifier.Alt)
        if(event.isAltGraphDown)
            this.activeModifiers.add(Modifier.AltGr)
        if(event.isShiftDown)
            this.activeModifiers.add(Modifier.Shift)
        if(event.isMetaDown)
            this.activeModifiers.add(Modifier.Super)
        if(event.isControlDown)
            this.activeModifiers.add(Modifier.Control)
    }

    /**
     * Check whether given key is currently pressed.
     *
     * @param key Key to check
     * @return Flag indicating whether key is currently pressed
     */
    override fun isKeyDown(key: Key): Boolean
    {
        return this.pressedKeys.contains(key)
    }

    /**
     * Check whether given modifier was pressed as part of a key stroke
     *
     * @param modifier Key modifer to check
     * @return Flag indicating whether modifier key was pressed
     */
    override fun isKeyModifierDown(modifier: Modifier): Boolean
    {
        return this.activeModifiers.contains(modifier)
    }

    /**
     * Check whether there is a string of text available.
     *
     * @return Flag indicating whether there currently is text available.
     */
    override fun hasText(): Boolean
    {
        return this.textBuffer.isNotEmpty()
    }

    /**
     * Retrieve input as text. This is always recorded - every key press is automatically added to the
     * text buffer. The text buffer is automatically cleared after each frame.
     *
     * @return Input as text.
     */
    override fun text(): String
    {
        if(this.textBuffer.isEmpty())
            throw IllegalStateException("Can't retrieve text - input text buffer is empty")
        else
            return this.textBuffer.joinToString()
    }

    /**
     * Clear all remaining input. This should be called every time a frame is rendered.
     */
    override fun clear()
    {
        // Clear all currently active key and modifier pressed
        this.activeModifiers.clear()
        this.pressedKeys.clear()

        // Clear text buffer
        this.textBuffer.clear()
    }

    /**
     * Check whether there currently is touch input available
     *
     * @return Flag indicating whether there is touch input available
     */
    override fun hasTouchInput(): Boolean
    {
        return false
    }

    /**
     * Retrieve stored touch input. This can only be called if [hasTouchInput] returned true in the
     * current frame.
     *
     * @return A collection of [TouchInput] instances
     */
    override fun getTouchInput(): Iterable<TouchInput>
    {
        throw IllegalStateException("Called getTouchInput when no touch input was available")
    }
}