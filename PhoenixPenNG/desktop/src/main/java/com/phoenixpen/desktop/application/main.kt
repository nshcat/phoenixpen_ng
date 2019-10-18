package com.phoenixpen.desktop.application
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import java.awt.Dimension

/**
 * The desktop application entry point class that handles command line argument parsing
 * and launching of the desktop version of the game
 */
class DesktopApp: CliktCommand()
{
    /**
     * Whether the application should be launched in desktop mode
     */
    val desktop: Boolean by option("--desktop", "-d", help = "Use application as animated desktop background").flag()

    /**
     * The width of the window. This will be ignored in desktop mode.
     */
    val width: Int by option("--width", "-w", help = "Window width, in pixels").int().default(540)

    /**
     * The height of the window. This will be ignored in desktop mode.
     */
    val height: Int by option("--height", "-h", help = "Window height, in pixels").int().default(540)

    /**
     * Whether the window is supposed to be resizable. This is incompatible with desktop mode.
     */
    val resizable: Boolean by option("--resizable", "-r", help = "Make window resizable. Incompatible with desktop mode").flag()

    /**
     * Run application, after arguments were parsed
     */
    override fun run()
    {
        // In desktop mode, we need to determine the screen dimensions and use that as
        // window size
        if(this.desktop)
        {
            println("Starting in desktop mode")
            // TODO Determine screen dimensions dynamically
            DesktopApplication(Dimension(2558, 1082), true)
        }
        else
        {
            // Just use values given for width and height, plus 2 (this is needed, because for some
            // reason, the JFrame isn't exactly the requested size)
            DesktopApplication(Dimension(this.width + 2,  this.height + 2))
        }
    }
}

/**
 * Delegate command line arguments to application class
 */
fun main(args: Array<String>) = DesktopApp().main(args)