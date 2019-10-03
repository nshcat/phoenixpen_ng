package com.phoenixpen.game.ascii

import com.phoenixpen.game.console.Console
import com.phoenixpen.game.console.ConsoleState
import com.phoenixpen.game.core.Timer
import com.phoenixpen.game.input.*
import com.phoenixpen.game.map.MapView
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.logging.Logger
import com.phoenixpen.game.math.PoissonTest
import com.phoenixpen.game.resources.ResourceProvider

/**
 * The game main scene, displaying the map and allowing interaction with the game.
 *
 * @param resources The resource manager to retrieve game data from
 * @param input The input manager providing input events
 * @param logger The logger instance to use
 * @param dimensions The screen dimensions, in glyphs
 * @param dimensionsInPixels The screen dimensions, in pixels
 */
class MainScene(
        resources: ResourceProvider,
        input: InputProvider,
        logger: Logger,
        dimensions: ScreenDimensions,
        dimensionsInPixels: ScreenDimensions
): Scene(resources, input, logger, dimensions, dimensionsInPixels)
{
    /**
     * Input event enumeration for this scene
     */
    private enum class MainSceneInput
    {
        // Map view position changes
        MoveMapViewUp,
        MoveMapViewDown,

        MoveMapViewNorth,
        MoveMapViewWest,
        MoveMapViewEast,
        MoveMapViewSouth,

        MoveMapViewNorthFast,
        MoveMapViewWestFast,
        MoveMapViewEastFast,
        MoveMapViewSouthFast,

        ToggleConsoleMode,
        ToggleConsoleHeight
    }

    /**
     * The input adapter for this scene
     *
     * @param input The input provider reference
     * @property dimensions The screen dimensions, in pixels
     */
    private class MainSceneInputAdapter(input: InputProvider, val dimensions: ScreenDimensions):
            InputAdapter(input)
    {
        /**
         * Touch area for the "west" command
         */
        private val touchAreaWest = Rectangle.fromDimensions(ScreenDimensions(this.dimensions.width/4, this.dimensions.height))

        /**
         * Register input mappings
         */
        init
        {
            // Keyboard shortcuts
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewNorth, Key.Up))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewSouth, Key.Down))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewWest, Key.Left))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewEast, Key.Right))

            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewNorthFast, Key.Up, Modifier.Shift))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewSouthFast, Key.Down, Modifier.Shift))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewWestFast, Key.Left, Modifier.Shift))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewEastFast, Key.Right, Modifier.Shift))

            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewUp, Key.PageUp))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.MoveMapViewDown, Key.PageDown))

            this.addMapping(EnumKeyComboMapping(MainSceneInput.ToggleConsoleHeight, Key.F4))
            this.addMapping(EnumKeyComboMapping(MainSceneInput.ToggleConsoleMode, Key.F3))

            // Touch controls
            this.addMapping(EnumAreaTouchMapping(MainSceneInput.MoveMapViewUp, Rectangle.fromDimensions(this.dimensions)))
        }
    }

    /**
     * Initialize global logger
     */
    init
    {
        GlobalLogger.setLogger(logger)
    }

    private val poissonTest = PoissonTest(dimensions)

    /**
     * The input adapter for this scene
     */
    private val inputAdapter = MainSceneInputAdapter(input, this.dimensionsInPixels)

    /**
     * The main simulation state
     */
    val simulationState: Simulation = Simulation(this.resources)

    /**
     * The map view, a scene component tasked with rendering the map to screen
     */
    val mapView: MapView = MapView(this.simulationState, this.dimensions, Position(0, 0), 2)

    /**
     * The user console
     */
    val console = Console(input).apply { currentState = ConsoleState.Log }

    /**
     * Render main scene
     */
    override fun render(screen: Screen)
    {
        // Render the map and all its structures, entities, etc..
        this.mapView.render(screen)

        this.console.render(screen)
        /*var line = 0

        fun logMsg(tag: String, msg: String, clr: Color = Color.white)
        {
            val pos = Position(0, line)
            screen.putString(pos, "[${tag.padEnd(8)}] ", Color(156, 156, 156))
            screen.putString(pos, msg.padEnd(40), front = clr)

            ++line
        }

        logMsg("Weather", "It has started raining", Color(156, 156, 156))
        logMsg("Fauna", "The Jackdaw\u000C chirps at the Magpie\u000C!")
        logMsg("Fauna", "The Toad\u000B eats the Dragonfly\u000C")
        logMsg("Fauna", "The Wolf\u000C succumbs to loneliness", Color.red)
        screen.putString(Position(0, 4), "".padEnd(51, '\u00C4'), Color(156, 156, 156))*/
    }

    /**
     * Update main scene logic
     */
    override fun update(elapsedTicks: Int)
    {
        // Only do input if the console has not grabbed it
        if(!this.console.grabsInput())
            this.doInput()
        else
            this.console.update(elapsedTicks)

        // Update simulation state
        this.simulationState.update(elapsedTicks)

        // Update the map view
        this.mapView.update(elapsedTicks)


        //this.poissonTest.update(elapsedTicks)
    }

    /**
     * Do main scene input
     */
    private fun doInput()
    {
        // Update the local input adapter
        this.inputAdapter.update()

        // Check for input
        if(this.inputAdapter.hasEvents())
        {
            // Consume all events
            val events = this.inputAdapter.consumeEvents()

            for(event in events)
            {
                if(event is EnumEvent<*>)
                {
                    when ((event as EnumEvent<MainSceneInput>).value)
                    {
                        MainSceneInput.MoveMapViewEast -> this.mapView.move(Position.east * 4)
                        MainSceneInput.MoveMapViewSouth -> this.mapView.move(Position.south * 4)
                        MainSceneInput.MoveMapViewNorth -> this.mapView.move(Position.north * 4)
                        MainSceneInput.MoveMapViewWest -> this.mapView.move(Position.west * 4)

                        MainSceneInput.MoveMapViewEastFast -> this.mapView.move(Position.east * 15)
                        MainSceneInput.MoveMapViewSouthFast -> this.mapView.move(Position.south * 15)
                        MainSceneInput.MoveMapViewNorthFast -> this.mapView.move(Position.north * 15)
                        MainSceneInput.MoveMapViewWestFast -> this.mapView.move(Position.west * 15)

                        MainSceneInput.MoveMapViewDown -> this.mapView.moveUp(-1)
                        MainSceneInput.MoveMapViewUp -> this.mapView.moveUp(1)

                        MainSceneInput.ToggleConsoleHeight -> this.console.toggleHeightMode()
                        MainSceneInput.ToggleConsoleMode -> this.console.toggleState()
                    }
                }

            }
        }
    }
}