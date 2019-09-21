package com.phoenixpen.game.map

import com.phoenixpen.game.ascii.Dimensions

/**
 * A class specifying the dimensions of a map. Note that [width] and [depth] describe the ground
 * plane, while [height] speififies the actual map height (into the sky)
 *
 * @property width Map width
 * @property depth Map depth
 * @property height Map height
 */
data class MapDimensions(val width: Int, val height: Int, val depth: Int)
{
    /**
     * Retrieve dimensions of a single map layer
     */
    fun wh(): Dimensions
    {
        return Dimensions(this.width, this.depth)
    }
}