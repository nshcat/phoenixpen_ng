package com.phoenixpen.game.console

import com.phoenixpen.game.ascii.MainScene
import com.phoenixpen.game.ascii.Rectangle
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.game.input.*

/**
 * Input adapter for the in-game console
 */
class ConsoleInputAdapter(input: InputProvider): InputAdapter(input)
{
    /**
     * Register input mappings
     */
    init
    {
        // Keyboard shortcuts
        this.addMapping(EnumKeyComboMapping(ConsoleInput.CloseConsole, Key.Escape))
        this.addMapping(EnumKeyComboMapping(ConsoleInput.ToggleState, Key.F3))
        this.addMapping(EnumKeyComboMapping(ConsoleInput.ToggleHeightMode, Key.F4))
    }
}