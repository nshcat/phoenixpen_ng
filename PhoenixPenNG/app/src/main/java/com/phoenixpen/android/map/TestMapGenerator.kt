package com.phoenixpen.android.map

import com.phoenixpen.android.ascii.Position3D
import com.phoenixpen.android.data.MaterialManager

/**
 * Simple map generator.
 *
 * @property materialManager Reference to the material manager
 */
class TestMapGenerator(val materialManager: MaterialManager): MapLoader
{
    /**
     * Generate simple 5x5x5 test map
     */
    override fun load(): Map
    {
        // Create empty map
        val map = Map(MapDimensions(15, 6, 15))

        // Lookup the materials we will be using
        val grass = this.materialManager.lookupMaterial("grass")
        val stone = this.materialManager.lookupMaterial("stone")

        // Generate terrain
        for(ix in 0 until 15)
        {
            for(iz in 0 until 15)
            {
                // Fill the ground with stone
                for(iy in 0 until 2)
                {
                    val cell = map.cellAt(Position3D(ix, iy, iz))

                    cell.material.setMaterial(stone)
                    cell.state = MapCellState.Solid
                }

                // Fill one layer with ground
                val cell = map.cellAt(Position3D(ix, 2, iz))
                cell.material.setMaterial(grass)
                cell.state = MapCellState.Ground
            }
        }

        // Generate lower area in middle
        for(ix in 5 until 10)
        {
            for (iz in 5 until 10)
            {
                val cell = map.cellAt(Position3D(ix, 1, iz))
                cell.material.setMaterial(grass)
                cell.state = MapCellState.Ground

                val cell2 = map.cellAt(Position3D(ix, 2, iz))
                cell2.material.clear()
                cell2.state = MapCellState.Air
            }
        }

        return map
    }
}