package com.phoenixpen.android.rendering

import com.phoenixpen.android.application.ScreenDimensions
import org.joml.Matrix4f

/**
 * A simple class managing the construction of the view and projection matrices for
 * a very simple orthographic projection.
 */
class OrthographicProjection
{
    /**
     * The current view matrix.
     */
    val view = Matrix4f()

    /**
     * The current projection matrix.
     */
    val projection = Matrix4f()

    /**
     * Refresh the projection state by recalculating the stored matrices
     *
     * @param dimensions New screen dimensions.
     */
    fun refresh(dimensions: ScreenDimensions)
    {
        // We flip the y-axis, in order to have (0,0) at the top left of the screen
        this.projection.setOrtho(
                0.0f, dimensions.width.toFloat(),
                dimensions.height.toFloat(), 0.0f,
                0.0f, 1.0f
        )
    }

    /**
     * Retrieve rendering parameters based on current projection state.
     *
     * @return Rendering parameters base on current projection state.
     */
    fun toRenderParams(): RenderParams
    {
        return RenderParams(this.view, this.projection)
    }
}