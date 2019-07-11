package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.DrawInfo
import com.phoenixpen.game.ascii.TileInstance
import com.phoenixpen.game.ascii.TileType
import com.phoenixpen.game.ascii.TileTypeSerializer
import com.phoenixpen.game.core.Updateable
import com.phoenixpen.game.simulation.LeafState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Class encapsulating tile instances for all leaf states based on the tile types given in a
 * [LeafTileType].
 */
data class LeafTileInstance(
        val normalInstance: TileInstance,
        val autumnalInstance: TileInstance,
        val droppedInstance: TileInstance
) : Updateable
{
    /**
     * Retrieve tile instance for given [LeafState].
     *
     * @param state Leaf state to retrieve tile instance for
     * @return Tile instance for given leaf state
     */
    fun instanceFor(state: LeafState): TileInstance = when(state)
    {
        LeafState.Normal -> this.normalInstance
        LeafState.Dropped -> this.droppedInstance
        LeafState.Autumnal -> this.autumnalInstance
    }

    /**
     * Perform logic update based on given number of elapsed ticks
     *
     * @param elapsedTicks Number of elapsed ticks since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // Delegate update to all stored tile instances
        this.normalInstance.update(elapsedTicks)
        this.autumnalInstance.update(elapsedTicks)
        this.droppedInstance.update(elapsedTicks)
    }
}


/**
 * A class containing all tiles for the different life stages of a leaf tile
 *
 * @property normalTile Default leaf tile, used as typical green leaves
 * @property autumnalTile Tile for discolored autumnal leaves.
 * @property droppedTile Tile for dropped leaves.
 */
@Serializable
class LeafTileType(
        @Serializable(with = TileTypeSerializer::class) @SerialName("normal") val normalTile: TileType = TileType(),
        @Serializable(with = TileTypeSerializer::class) @SerialName("autumnal") val autumnalTile: TileType = TileType(),
        @Serializable(with = TileTypeSerializer::class) @SerialName("dropped") val droppedTile: TileType = TileType()
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

    /**
     * Check whether this tile should currently be drawn, or not.
     *
     * @param instance The leaf tile instance
     * @param state The current leaf state
     * @return Value indicating whether this tile should be drawn or not
     */
    fun shouldDraw(instance: LeafTileInstance, state: LeafState): Boolean
    {
        return this.tileTypeForState(state).shouldDraw(instance.instanceFor(state))
    }

    /**
     * Retrieve current tile
     *
     * @param instance The leaf tile instance
     * @param state The current leaf state
     * @return Tile draw info instance for this tile
     */
    fun tile(instance: LeafTileInstance, state: LeafState): DrawInfo
    {
        return this.tileTypeForState(state).tile(instance.instanceFor(state))
    }

    /**
     * Create a [LeafTileInstance] based on this [LeafTileType].
     *
     * @param animOffset Animation offset to be used for all tiles in the [LeafTileType]
     * @return [LeafTileInstance] combining all [TileInstance] objects for the various leaf states
     */
    fun createInstance(animOffset: Int = 0): LeafTileInstance
    {
        return LeafTileInstance(
                this.normalTile.createInstance(animOffset),
                this.autumnalTile.createInstance(animOffset),
                this.droppedTile.createInstance(animOffset)
        )
    }
}