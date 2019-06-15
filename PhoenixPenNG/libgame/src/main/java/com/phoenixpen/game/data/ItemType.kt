package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.DrawInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An enumeration describing all the possible item categories.
 */
enum class ItemCategory
{
    /**
     * General item category
     */
    Unspecified
}

/**
 * A class describing the properties of an item, using the type class pattern.
 *
 * @property identifier Unique identifier of this item type
 * @property displayName A string used as a name for this item in in-game UI
 * @property description A short description text describing this type of item
 * @property category Item category, e.g. food
 * @property isDrawn Whether the item is drawn when it is outside of an inventory
 * @property tile Tile draw info representing this item. Only used if [isDrawn] is true.#
 * @property tileFancy Tile draw info representing this item, for fancy graphics mode
 */
@Serializable
data class ItemType(
        val identifier: String,
        @SerialName("display_name") val displayName: String,
        val description: String = "",
        val category: ItemCategory = ItemCategory.Unspecified,
        @SerialName("is_drawn") val isDrawn: Boolean = false,
        val tile: DrawInfo = DrawInfo(),
        @SerialName("tile_fancy") val tileFancy: DrawInfo = DrawInfo()
)
{
    companion object
    {
        /**
         * Placeholder type used for missing item types
         */
        val placeholder: ItemType = ItemType(
                "placeholder",
                "UNKNOWN ITEM",
                "Missing item data"
        )
    }
}
