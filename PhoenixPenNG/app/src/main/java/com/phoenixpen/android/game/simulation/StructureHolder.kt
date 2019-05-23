package com.phoenixpen.android.game.simulation

import com.phoenixpen.android.game.data.Structure

/**
 * An interface describing a type which is responsible for storing and managing structure instances
 * that are part of the game world. Normally, this is implemented in different system classes which manage
 * a single sub type of [Structure], e.g. [PlantSystem] which simulates plant life cycle.
 */
interface StructureHolder
{
    /**
     * Retrieve all structure instances managed by this holder class. This is mainly used by scene
     * components like [MapView] to actually render the structures, or the path finding system.
     */
    fun structures(): Collection<Structure>
}