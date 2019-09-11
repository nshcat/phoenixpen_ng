package com.phoenixpen.game.math

import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position

/**
 * Abstract class describing the concept of a strategy used to randomly select a number of points from
 * a given two-dimensional, rectangular map slice.
 *
 * @property dimensions Dimensions of the area to sample from, in map cells
 */
abstract class SamplingStrategy(val dimensions: Dimensions)
{
    /**
     * Sample points using the sampling strategy implemented by this class.
     *
     * @return Collection of all generated samples
     */
    abstract fun sample(): Collection<Position>
}