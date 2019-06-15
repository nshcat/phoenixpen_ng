package com.phoenixpen.android.game.simulation

import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.data.MapDecoration
import com.phoenixpen.android.game.data.MapDecorationManager
import com.phoenixpen.android.game.data.Structure
import com.phoenixpen.android.game.resources.ResourceProvider

/**
 * Class managing all instances of map decorations.
 *
 * @property resources The current resource provider
 */
class MapDecorationSystem(val resources: ResourceProvider): StructureHolder
{
    /**
     * Map decoration type manager
     */
    val decorationManager = MapDecorationManager()

    /**
     * All structures saved in this holder.
     */
    protected val structureCollection = ArrayList<MapDecoration>()

    /**
     * Initialize this system
     */
    init
    {
        // Load all decoration types
        this.decorationManager.loadMapDecorations(this.resources, "map_decorations.json")
    }

    /**
     * Add add a new map decoration.
     *
     * @param position Position of the new map decoration
     * @param type Unique identifier of the requested map decoration type
     */
    fun addDecoration(position: Position3D, type: String)
    {
        // Retrieve decoration type
        val decorationType = this.decorationManager.lookupMapDecoration(type)

        // Create instance
        val instance = MapDecoration.create(decorationType, position)

        // Add to collection
        this.structureCollection.add(instance)
    }

    /**
     * Retrieve all structures stored in this holder
     */
    override fun structures(): Collection<Structure>
    {
        return this.structureCollection
    }
}