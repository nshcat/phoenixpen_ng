package com.phoenixpen.game.math

import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position
import java.util.concurrent.ThreadLocalRandom

/**
 * A sampling strategy that uses standard random number generation to determine the sampled points.
 *
 * @param dimensions Dimensions of the area to sample from, in map cells
 * @property probability The chance of a single tile being sampled
 */
class RandomSampler(dimensions: Dimensions, val probability: Double): SamplingStrategy(dimensions)
{
    override fun sample(): Collection<Position>
    {
        val output = ArrayList<Position>()
        val random = ThreadLocalRandom.current()

        // Generate all points
        for(ix in 0 until dimensions.width)
        {
            for(iy in 0 until dimensions.height)
            {
                if(random.nextDouble(1.0) <= this.probability)
                    output.add(Position(ix, iy))
            }
        }

        return output
    }
}