package com.phoenixpen.game.data.biome

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.resources.ResourceProvider
import com.phoenixpen.game.simulation.Simulation
import kotlinx.serialization.json.Json
import kotlinx.serialization.map

/**
 * A single biome component that can be applied to a simulation state
 */
interface BiomeComponent
{
    /**
     * Apply this biome component to the given simulation state
     *
     * @param simulation The simulation state to apply this biome component to
     */
    fun apply(simulation: Simulation)
}

/**
 * A biome component that spawns objects based on string type IDs
 *
 * @property template The biome template dictating where types can spawn in which way. Those are basically sets of bitmaps.
 * @property typeKey The type key that associates colours in the biome template with actual spawning info.
 */
abstract class TypeBiomeComponent(
        val template: BiomeTemplate,
        val typeKey: TypeKey
): BiomeComponent
{
    /**
     * Alternative constructor used to create this component based on resource IDs
     *
     * @param res Resource provider instance used to extract JSON files and layer bitmaps
     * @param infoId The ID for the type key
     * @param templateLayerIds IDs for the layers in the biome template
     */
    constructor(res: ResourceProvider, infoId: String, templateLayerIds: Collection<String>):
        this(
            BiomeTemplate.fromBitmaps(res, *(templateLayerIds.toTypedArray())),
            Json.indented.parse(
                (Color.serializer()).to(TypeKeyEntry.serializer()).map,
                res.json(infoId)
            )
        )


    /**
     * Initialize a mapping between colours in the biome template and the associated object spawner instances.
     * This also contains precomputed sampling state in order to speed things up.
     *
     * @param simulation The simulation state to use
     * @return Created mapping
     */
    private fun createSpawners(simulation: Simulation): HashMap<Color, ObjectSpawner>
    {
        val mapping = HashMap<Color, ObjectSpawner>()

        // Retrieve map layer dimensions to construct spawners later
        val dimensions = this.template.dimensions()

        // Create an object spawner for each distinct color in the type key
        for((color, typeEntry) in this.typeKey)
        {
            // Black is not allowed, since it is meant to refer to "no placement here"
            if(color == Color.black)
                throw IllegalStateException("TypeBiomeComponent: Encountered color black in type key")

            // Create spawner
            mapping.put(color, ObjectSpawner.createSpawner(dimensions, typeEntry))
        }

        return mapping
    }


    /**
     * Apply this biome component to the given simulation state
     *
     * @param simulation The simulation state to apply this biome component to
     */
    override fun apply(simulation: Simulation)
    {
        // Create object spawners
        val objectSpawners = this.createSpawners(simulation)

        // Retrieve dimensions of the layers
        val layerDimensions = this.template.dimensions()

        // Work through all layers
        for((iy, layer) in this.template.layers.withIndex())
        {
            // Iterate through all entries in the layer and spawn objects if needed
            for(ix in 0 until layerDimensions.width)
            {
                for(iz in 0 until layerDimensions.height)
                {
                    // Build absolute map position for the possible tree
                    val mapPosition = Position3D(ix, iy, iz)

                    // Position in the layer
                    val layerPosition = Position(ix, iz)

                    // Retrieve layer entry
                    val entry = layer.entryAt(layerPosition)

                    // Ignore if its black
                    if(entry == Color.black)
                        continue

                    // Retrieve object spawner for this color
                    if(!objectSpawners.containsKey(entry))
                        throw IllegalStateException("Unknown biome template color encountered: ${entry.r}:${entry.g}:${entry.b}")

                    val spawner = objectSpawners.getValue(entry)

                    // Check if we are supposed to generate a value here
                    if(spawner.shouldPlace(layerPosition))
                    {
                        // Perform actual spawning
                        this.spawnObject(simulation, mapPosition, spawner.selectType())
                    }
                }
            }
        }
    }

    /**
     * Actually perform the spawning step of the object with given type, whatever category of
     * object that might be.
     *
     * @param simulation The affected simulation to spawn object in
     * @param position The world position to spawn the object at
     * @param type The type of object to spawn. The implementing class specifies which category of object
     * this belongs to.
     */
    abstract fun spawnObject(simulation: Simulation, position: Position3D, type: String)
}