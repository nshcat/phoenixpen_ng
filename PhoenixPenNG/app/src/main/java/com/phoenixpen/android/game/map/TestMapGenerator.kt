package com.phoenixpen.android.game.map

import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.data.MaterialManager

/**
 * Simple map generator.
 *
 * @property materialManager Reference to the material manager
 */
class TestMapGenerator(val materialManager: MaterialManager): MapLoader
{
    /**
     * Generate simple 5x5x5 materials map
     */
    override fun load(): Map
    {
        // Create empty map
        val map = Map(MapDimensions(20, 8, 20))

        // Lookup the materials we will be using
        val grass = this.materialManager.lookupMaterial("grass")
        val stone = this.materialManager.lookupMaterial("stone")

        // Generate terrain
        for(ix in 0 until 20)
        {
            for(iz in 0 until 20)
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


        val cell = map.cellAt(Position3D(8, 2, 8))
        cell.material.setMaterial(grass)
        cell.state = MapCellState.Ground

        return map
    }
}