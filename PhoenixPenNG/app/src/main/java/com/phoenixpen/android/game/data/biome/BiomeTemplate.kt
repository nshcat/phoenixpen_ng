package com.phoenixpen.android.game.data.biome

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.Position

/**
 * A typealias used to describe the dimensions of a single map template layer
 */
typealias LayerDimensions = ScreenDimensions


/**
 * A single layer in a biome template.
 */
class BiomeTemplateLayer(val dimensions: LayerDimensions)
{
    /**
     * All entries in this layer, which are colors
     */
    val entries = ArrayList<Color>(dimensions.width * dimensions.height)

    /**
     * Fill entries with black
     */
    init
    {
        for(i in 0 until dimensions.width * dimensions.height)
            this.entries.add(Color.black)
    }

    /**
     * Retrieve color entry at given 2d position inside this layer.
     *
     * @param position Position to retrieve entry from
     * @return Entry at given position, if inside bounds
     */
    fun entryAt(position: Position): Color
    {
        // Perform bounds check
        if(!this.isInBounds(position))
            throw IllegalArgumentException("BiomeTemplateLayer::entryAt: Position out of layer bounds")

        return this.entries[this.indexFor(position)]
    }

    /**
     * Calculate linear index into [entries] list for given two dimensional position
     *
     * @param position Position to calculate linear index for
     * @return Linear index for given position
     */
    fun indexFor(position: Position): Int
    {
        return (position.y * this.dimensions.width) + position.x
    }

    /**
     * Check whether the given position is inside layer bounds
     *
     * @param position Position to check
     * @return Flag indicating whether given position is inside layer bounds
     */
    fun isInBounds(position: Position): Boolean
    {
        return (position.x >= 0 && position.x < this.dimensions.width) && (position.y >= 0 && position.y < this.dimensions.height)
    }

    companion object
    {
        /**
         * Load a map template layer from given bitmap.
         */
        fun fromBitmap(bitmap: Bitmap): BiomeTemplateLayer
        {
            // Create empty instance
            val layer = BiomeTemplateLayer(LayerDimensions(bitmap.width, bitmap.height))

            // Copy data
            for(ix in 0 until layer.dimensions.width)
            {
                for(iy in 0 until layer.dimensions.height)
                {
                    // Retrieve color
                    val colorEntry = bitmap.getPixel(ix, iy)

                    val color = Color(
                            android.graphics.Color.red(colorEntry),
                            android.graphics.Color.green(colorEntry),
                            android.graphics.Color.blue(colorEntry)
                    )

                    layer.entries[layer.indexFor(Position(ix, iy))] = color
                }
            }

            return layer
        }

        /**
         * Load from bitmap stored in resources
         */
        fun fromBitmap(context: Context, id: Int): BiomeTemplateLayer
        {
            return fromBitmap(BitmapFactory.decodeResource(context.resources, id))
        }
    }
}


/**
 * A class describing how a certain part of a map should be generated. It is made up from multiple [BiomeTemplateLayer] instances.
 */
class BiomeTemplate
{
    /**
     * All layers in this biome template
     */
    val layers = ArrayList<BiomeTemplateLayer>()

    /**
     * Determine dimensions of whole structure. Note that this is only possible if there is at least
     * one layer present.
     *
     * @return Dimensions of the whole structure, if at least one layer is present.
     */
    fun dimensions(): LayerDimensions
    {
        // We cant determine the dimensions if there is not at least one layer present
        if(this.layers.size <= 0)
            throw IllegalStateException("Cant determine dimensions of empty BiomeTemplate")

        // Otherwise just use the dimensions of the first layer, since they are required to be the same
        // for all the layers in this structure.
        val layer = this.layers.first()
        return LayerDimensions(layer.dimensions.width, layer.dimensions.height)
    }

    companion object
    {
        /**
         * Create from set of bitmap resources
         */
        fun fromBitmaps(context: Context, vararg ids: Int): BiomeTemplate
        {
            if(ids.isEmpty())
                throw IllegalArgumentException("at least one layer bitmap is required")

            // Create empty template
            val template = BiomeTemplate()

            // Load all layers
            for(id in ids)
            {
                template.layers.add(BiomeTemplateLayer.fromBitmap(context, id))
            }

            return template
        }
    }
}