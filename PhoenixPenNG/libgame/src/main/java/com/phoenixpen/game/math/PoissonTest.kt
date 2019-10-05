package com.phoenixpen.game.math

import com.phoenixpen.game.ascii.SceneComponent
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.game.core.TickCounter
import com.phoenixpen.game.graphics.Color
import com.phoenixpen.game.graphics.Surface

class PoissonTest(screenDimensions: ScreenDimensions): SceneComponent
{
    private val sampler = PoissonDiskSampler(
            screenDimensions,
            6.15,
            minDistanceMod = NormalDistribution(
                    variance = 2.0,
                    restricted = true,
                    min = -3.0,
                    max = 3.0
            )
        )

    private val badSampler = RandomSampler(screenDimensions, 0.05)

    private var points = sampler.sample()

    private var badPoints = badSampler.sample()

    private val counter = TickCounter(15)

    override fun update(elapsedTicks: Int)
    {
        if(this.counter.update(elapsedTicks) > 0)
        {
            this.points = sampler.sample()
            this.badPoints = badSampler.sample()
        }
    }

    override fun render(surface: Surface) {
        surface.clear()
        /*for(point in this.badPoints)
        {
            screen.setBackColor(point, Color.red)
        }*/

        for(point in this.points)
        {
            surface.setBackColor(point, Color.green)
        }
    }
}