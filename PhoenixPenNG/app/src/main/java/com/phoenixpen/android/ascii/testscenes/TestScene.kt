package com.phoenixpen.android.ascii.testscenes

import android.content.Context
import com.phoenixpen.android.application.ScreenDimensions
import com.phoenixpen.android.ascii.*

/**
 * A simple test scene
 */
class TestScene(ctx: Context, dimensions: ScreenDimensions): Scene(ctx, dimensions)
{
    override fun render(screen: Screen)
    {
        for(ix in 0 until screen.size.width)
        {
            for (iy in 0 until screen.size.height)
            {
                val pos = Position(ix, iy)

                screen.setBackColor(pos, Color(23, 182, 11))
            }
        }

        for(ix in -2..2)
        {
            for (iy in -2..2)
            {
                var shadows = emptyShadowDirections()

                if(ix == -2)
                    shadows.add(ShadowDirection.West)

                if(ix == 2)
                    shadows.add(ShadowDirection.East)

                if(iy == -2)
                    shadows.add(ShadowDirection.North)

                if(iy == 2)
                    shadows.add(ShadowDirection.South)

                val pos = Position(10, 10) + Position(ix, iy)

                screen.setDepth(pos, 3)
                screen.setShadows(pos, shadows)
            }
        }
    }

    override fun update(elapsedTicks: Long)
    {
        // Do nothing
    }
}