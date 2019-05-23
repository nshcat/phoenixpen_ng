package com.phoenixpen.android.game.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.io.*

/**
 * A class managing all item type classes.
 */
class ItemManager
{
    /**
     * Map of all known items based on their unique identifier
     */
    private val items = HashMap<String, ItemType>()

    /**
     * Register placeholder item
     */
    init
    {
        this.items.put(ItemType.placeholder.identifier, ItemType.placeholder)
    }

    /**
     * Try to retrieve a item type based on its unique identifier. This will throw
     * if an item type is not known
     *
     * @param key The unique identifier of the item type
     * @return itemType instance matching the given identifier, if it exists
     */
    fun lookupItemType(key: String): ItemType
    {
        // Check if we know this item
        if(!items.containsKey(key))
            throw IllegalArgumentException("Unknown item: \"$key\"")

        // Otherwise just return the item
        return this.items[key] ?: throw IllegalArgumentException("Unknown item: \"$key\"")
    }

    /**
     * Try to retrieve a item type based on its unique identifier. Will return default
     * item if requested item does not exist.
     *
     * @param key The unique identifier of the item type
     * @return itemType instance matching the given identifier, if it exists
     */
    fun lookupItemTypeSafe(key: String): ItemType
    {
        if(!this.items.containsKey(key))
            Log.w("ItemManager", "Missing item \"$key\" replaced with placeholder item")

        return this.items[key] ?: ItemType.placeholder
    }

    /**
     * Load all item types from given JSON document.
     *
     * @param stream Stream containing a JSON document.
     */
    fun loadItems(stream: InputStream)
    {
        // Read all item types contained in the JSON document
        val itemList = Json.parse(ItemType.serializer().list, BufferedReader(InputStreamReader(stream)).readText())

        // Store them all in the hash map for later retrieval
        for(item in itemList)
        {
            // Check for duplicates
            if(this.items.containsKey(item.identifier))
                throw IllegalStateException("Found duplicate item type id: \"${item.identifier}\"")

            this.items.put(item.identifier, item)
        }
    }

    /**
     * Load all item types from given JSON document stored as a resource.
     *
     * @param ctx Android application context
     * @param id Resource id
     */
    fun loadItems(ctx: Context, id: Int)
    {
        this.loadItems(ctx.resources.openRawResource(id))
    }
}