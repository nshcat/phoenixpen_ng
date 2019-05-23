package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.DrawInfo

/**
 * A specific item instance existing in the game world, which has exactly one item type.
 * Items can either be in the inventory of structures or actors, or be present on ground cells.
 *
 * @property type The type of this item
 */
class Item(val type: ItemType)
{
    /**
     * Retrieve item tile. This will throw if [ItemType.isDrawn] is not set to true.
     *
     * @return Current item tile draw info
     */
    fun tile(fancyMode: Boolean = true): DrawInfo
    {
        // Check that this item actually needs to be drawn
        if(!this.type.isDrawn)
            throw IllegalStateException("Can't retrieve tile for item which is set to \"no draw\"")

        // Return correct draw info
        return when(fancyMode)
        {
            true -> this.type.tileFancy
            false -> this.type.tile
        }
    }
}