package com.phoenixpen.game.data.biome

import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position
import java.util.concurrent.ThreadLocalRandom

/**
 * A class that manages object spawning for a single spawn configuration based on a [TypeKeyEntry]
 *
 * @property dimensions The dimensions of a single world layer
 * @property typeEntry The [TypeKeyEntry] this object spawner is based on
 */
abstract class ObjectSpawner(
        val dimensions: Dimensions,
        val typeEntry: TypeKeyEntry
)
{
    /**
     * Check whether a object should be placed at the given position.
     *
     * @param pos Position to check
     * @return Flag indicating whether an object should be spawned at given position
     */
    abstract fun shouldPlace(pos: Position): Boolean

    /**
     * Select a random type to spawn from the weighted list of types
     *
     * @return Type ID string of the randomly selected type
     */
    fun selectType(): String
    {
        return this.typeEntry.types.drawElement()
    }

    companion object
    {
        /**
         * Create a [ObjectSpawner] instance based on the given type key entry
         *
         * @param typeEntry Type key entry to base creation on
         * @return Created object spawner instance
         */
        fun createSpawner(dimensions: Dimensions, typeEntry: TypeKeyEntry): ObjectSpawner
        {
            return when(typeEntry.mode)
            {
                SpawnMode.Poisson -> PoissonSpawner(dimensions, typeEntry)
                SpawnMode.Place -> PlaceSpawner(dimensions, typeEntry)
                SpawnMode.Density -> RandomSpawner(dimensions, typeEntry)
            }
        }
    }
}

/**
 * An object spawner based on a poisson disk sampler
 */
class PoissonSpawner(dimensions: Dimensions, typeEntry: TypeKeyEntry): ObjectSpawner(dimensions, typeEntry)
{
    /**
     * The actual poisson sampler instance
     */
    private val sampler = this.typeEntry.poissonSampler(this.dimensions)

    /**
     * The precomputed samples
     */
    private val samples: HashSet<Position>

    /**
     * Sample points to be used for spawning later
     */
    init
    {
        this.samples = this.sampler.sample().toHashSet()
    }

    /**
     * Check whether a object should be placed at the given position.
     *
     * @param pos Position to check
     * @return Flag indicating whether an object should be spawned at given position
     */
    override fun shouldPlace(pos: Position): Boolean
    {
        return this.samples.contains(pos)
    }
}

/**
 * An object spawner based on simple random chance. This emulates the old behaviour
 */
class RandomSpawner(dimensions: Dimensions, typeEntry: TypeKeyEntry): ObjectSpawner(dimensions, typeEntry)
{
    /**
     * Check whether a object should be placed at the given position.
     *
     * @param pos Position to check
     * @return Flag indicating whether an object should be spawned at given position
     */
    override fun shouldPlace(pos: Position): Boolean
    {
        // Generate a random value
        val value = ThreadLocalRandom.current().nextDouble(1.0)

        return value <= this.typeEntry.density
    }
}

/**
 * An object spawner that always places objects. Used if the user wants to exactly mark the spawn
 * positions on the biome layers.
 */
class PlaceSpawner(dimensions: Dimensions, typeEntry: TypeKeyEntry): ObjectSpawner(dimensions, typeEntry)
{
    /**
     * Check whether a object should be placed at the given position.
     *
     * @param pos Position to check
     * @return Flag indicating whether an object should be spawned at given position
     */
    override fun shouldPlace(pos: Position): Boolean
    {
        return true
    }
}