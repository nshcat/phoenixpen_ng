package com.phoenixpen.game.data.biome

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.simulation.Simulation
import com.phoenixpen.game.resources.ResourceProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.map
import java.util.concurrent.ThreadLocalRandom


/**
 * A class implementing tree generation for biomes.
 *
 * @param resources The currently active resource provider instance used to load biome data
 * @param ids Class holding all IDs used to spawn trees in a biome
 */
class TreeComponent(resources: ResourceProvider, cfg: BiomeGenerationConfiguration):
        TypeBiomeComponent(resources, cfg.treeInfoId, cfg.treeLayerIds)
{
    /**
     * Actually perform the tree spawning at the selected position and with the selected tree type
     *
     * @param simulation The simulation instance
     * @param position The world position of the new tree to spawn
     * @param type The type ID string of the tree to spawn
     */
    override fun spawnObject(simulation: Simulation, position: Position3D, type: String)
    {
        simulation.treeHolder.generateTree(position, type)
    }
}
