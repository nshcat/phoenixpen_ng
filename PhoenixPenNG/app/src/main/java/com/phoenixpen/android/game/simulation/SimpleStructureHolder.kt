package com.phoenixpen.android.game.simulation

import com.phoenixpen.android.game.data.SimpleStructure
import com.phoenixpen.android.game.data.Structure

/**
 * Class managing all instances of simple structures. This is mainly just an example on how structure
 * holders should work.
 */
class SimpleStructureHolder(): StructureHolder
{
    /**
     * All structures saved in this holder.
     */
    val structureCollection = ArrayList<SimpleStructure>()

    /**
     * Retrieve all structures stored in this holder
     */
    override fun structures(): Collection<Structure>
    {
        return this.structureCollection
    }
}