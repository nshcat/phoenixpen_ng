package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.map.MapCellState

/**
 * A system managing snow in the game
 */
class SnowSystem(val simulation: Simulation): CoveringHolder
{
    /**
     * All coverings managed by this system
     */
    val coverings = ArrayList<Covering>()

    init
    {
        this.spawnSnow()
    }

    /**
     * Create snow on the whole map.
     */
    private fun spawnSnow()
    {
        // Retrieve map reference
        val map = this.simulation.map

        // Retrieve map dimensions
        val dimensions = map.dimensions

        // Retrieve snow covering type
        val snowType = this.simulation.coveringManager.lookupCovering("snow")

        for(ix in 0 until dimensions.width)
        {
            for(iz in 0 until dimensions.depth)
            {
                // "Raycast" from the sky downwards until something was hit
                for(iy in dimensions.height-1 downTo 0)
                {
                    // Current position
                    val pos = Position3D(ix, iy, iz)

                    // Retrieve map cell at this position
                    val cell = map.cellAt(pos)

                    // If its solid, cancel search for this column
                    if(cell.state == MapCellState.Solid)
                        break

                    // If it is ground, create snow and stop further search
                    if(cell.state == MapCellState.Ground || map.getStructureAtExact(pos).isPresent)
                    {
                        this.coverings.add(Covering.create(snowType, pos))
                        break
                    }
                }
            }
        }
    }

    /**
     * Retrieve all coverings managed by this system
     *
     * @return A collection of all coverings managed by this system
     */
    override fun coverings(): Collection<Covering>
    {
        return this.coverings
    }
}