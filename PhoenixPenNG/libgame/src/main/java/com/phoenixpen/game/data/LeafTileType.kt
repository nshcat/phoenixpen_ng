package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.TileType
import com.phoenixpen.game.simulation.LeafState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class containing all tiles for the different life stages of a leaf tile
 *
 * @property normalTile Default leaf tile, used as typical green leaves
 * @property autumnalTile Tile for discolored autumnal leaves.
 * @property droppedTile Tile for dropped leaves.
 */
@Serializable
class LeafTileType(
        @SerialName("normal") val normalTile: TileType,
        @SerialName("autumnal") val autumnalTile: TileType,
        @SerialName("dropped") val droppedTile: TileType
)
{
    /**
     * Retrieve the tile type matching the given leaf state
     *
     * @param state Leaf state to retrieve tile type for
     * @return Matching tile type
     */
    fun tileTypeForState(state: LeafState): TileType
    {
        return when(state)
        {
            LeafState.Normal -> this.normalTile
            LeafState.Autumnal -> this.autumnalTile
            LeafState.Dropped -> this.droppedTile
        }
    }
}