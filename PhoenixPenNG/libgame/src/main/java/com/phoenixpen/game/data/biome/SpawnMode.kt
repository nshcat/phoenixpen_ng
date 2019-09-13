package com.phoenixpen.game.data.biome

/**
 * Enumeration detailing the different ways a type key entry can specify how to spawn a game
 * object
 */
enum class SpawnMode
{
    /**
     * Object will be spawned on each marked map tile
     */
    Place,

    /**
     * All map tiles have the same probability of receiving an object spawn. Is based
     * on the density parameter.
     */
    Density,

    /**
     * Uses poisson disk sampling in order to place objects in a more advanced an visually pleasing
     * way.
     */
    Poisson
}