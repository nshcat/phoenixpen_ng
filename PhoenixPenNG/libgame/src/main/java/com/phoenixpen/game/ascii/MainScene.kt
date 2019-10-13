package com.phoenixpen.game.ascii

import com.phoenixpen.game.console.Console
import com.phoenixpen.game.console.ConsoleState
import com.phoenixpen.game.core.Timer
import com.phoenixpen.game.events.GlobalEvents
import com.phoenixpen.game.graphics.*
import com.phoenixpen.game.input.*
import com.phoenixpen.game.map.MapView
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.logging.Logger
import com.phoenixpen.game.math.PoissonTest
import com.phoenixpen.game.resources.ResourceProvider
import com.phoenixpen.game.settings.AppSettings

/**
 * The game main scene, displaying the map and allowing interaction with the game.
 *
 * @param resources The resource manager to retrieve game data from
 * @param input The input manager providing input events
 * @param logger The logger instance to use
 * @param surfaceManager The surface manager to use for surface creation
 * @param settings The application settings
 */
class MainScene(
        resources: ResourceProvider,
        input: InputProvider,
        logger: Logger,
        surfaceManager: SurfaceManager,
        settings: AppSettings
): Scene(resources, input, logger, surfaceManager, settings)
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
        ToggleConsoleHeight,
        QuitConsole
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
            this.addMapping(EnumKeyComboMapping(MainSceneInput.QuitConsole, Key.Escape))

            // Touch controls
            this.addMapping(EnumAreaTouchMapping(MainSceneInput.MoveMapViewUp, Rectangle.fromDimensions(this.dimensions)))
        }
    }

    /**
     * The root drawing surface, used to render the map view.
     */
    private lateinit var rootSurface: Surface

    /**
     * The surface used to draw the console
     */
    private lateinit var consoleSurface: Surface

    /**
     * Initialize global logger
     */
    init
    {
        GlobalLogger.setLogger(logger)

        // Init surfaces
        this.reshape()
    }

    /**
     * The input adapter for this scene
     */
    private val inputAdapter = MainSceneInputAdapter(input, this.surfaceManager.screenDimensions)

    /**
     * The main simulation state
     */
    val simulationState: Simulation = Simulation(this.resources)

    /**
     * The map view, a scene component tasked with rendering the map to screen
     */
    val mapView: MapView = MapView(this.simulationState, this.rootSurface.dimensionsInGlyphs, Position(0, 0), 2)

    /**
     * The user console
     */
    val console = Console(input)

    /**
     * Recreate all surfaces
     */
    override fun reshape()
    {
        this.rootSurface = this.surfaceManager.createSurface(this.settings.mainTileSetId)

        this.consoleSurface = this.surfaceManager.createSurface(this.settings.consoleTileSetId).apply {
            clearWithTransparency = true
        }
    }

    /**
     * Render main scene
     */
    override fun render()
    {
        // Render the map and all its structures, entities, etc..
        this.mapView.render(this.rootSurface)

        this.console.render(this.consoleSurface)
    }

    /**
     * Update main scene logic
     */
    override fun update(elapsedTicks: Int)
    {
        // Only do input if the console has not grabbed it
        if(!this.console.grabsInput())
        {
            this.console.update(elapsedTicks)
            this.doInput()
        }
        else
            this.console.update(elapsedTicks)

        // Update simulation state
        this.simulationState.update(elapsedTicks)

        // Update the map view
        this.mapView.update(elapsedTicks)
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

                        MainSceneInput.ToggleConsoleHeight ->
                        {
                            this.console.toggleHeightMode()
                            GlobalEvents.postEvent("Console", "Changed height mode")
                        }
                        MainSceneInput.ToggleConsoleMode -> this.console.toggleState()
                        MainSceneInput.QuitConsole ->
                        {
                            if(this.console.currentState == ConsoleState.Log)
                                this.console.currentState = ConsoleState.Hidden
                        }
                    }
                }
            }
        }
    }
}