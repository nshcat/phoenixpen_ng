package com.phoenixpen.game.simulation

import com.phoenixpen.game.data.Covering

/**
 * An interface describing a type which is responsible for storing and managing covering instances
 * that are part of the game world. Normally, this is implemented in different system classes which manage
 * a single sub type of [Covering], e.g. [SnowSystem] which simulates snowy weather.
 */
interface CoveringHolder
{
    /**
     * Retrieve all covering instances managed by this holder class. This is mainly used by scene
     * components like [MapView] to actually render the covering.
     */
    fun coverings(): Collection<Covering>
}