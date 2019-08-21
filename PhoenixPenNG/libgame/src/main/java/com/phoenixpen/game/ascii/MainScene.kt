package com.phoenixpen.game.ascii

import com.phoenixpen.game.input.*
import com.phoenixpen.game.map.MapView
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.logging.Logger
import com.phoenixpen.game.resources.ResourceProvider

/**
 * The game main scene, displaying the map and allowing interaction with the game.
 *
 * @property resources The resource manager to retrieve game data from
 * @property input The input manager providing input events
 * @property logger The logger instance to use
 * @property dimensions The screen dimensions
 */
class MainScene(
        resources: ResourceProvider,
        input: InputProvider,
        logger: Logger,
        dimensions: ScreenDimensions
): Scene(resources, input, logger, dimensions)
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
        MoveMapViewSouthFast
    }

    /**
     * The input adapter for this scene
     */
    private class MainSceneInputAdapter(input: InputProvider):
            InputAdapter(input)
    {
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
        }
    }

    /**
     * Initialize global logger
     */
    init
    {
        GlobalLogger.setLogger(logger)
    }

    /**
     * The input adapter for this scene
     */
    private val inputAdapter = MainSceneInputAdapter(input)

    /**
     * The main simulation state
     */
    val simulationState: Simulation = Simulation(this.resources)

    /**
     * The map view, a scene component tasked with rendering the map to screen
     */
    val mapView: MapView = MapView(this.simulationState, this.dimensions, Position(0, 0), 2)

    /**
     * Render main scene
     */
    override fun render(screen: Screen)
    {
        // Render the map and all its structures, entities, etc..
        this.mapView.render(screen)
    }

    /**
     * Update main scene logic
     */
    override fun update(elapsedTicks: Int)
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
                    }
                }

            }
        }

        // Update simulation state
        this.simulationState.update(elapsedTicks)

        // Update the map view
        this.mapView.update(elapsedTicks)
    }
}