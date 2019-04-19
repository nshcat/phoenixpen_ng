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
        // We use a simple orthographic projection.
        this.projection.setOrtho(
                0.0f, dimensions.aspectRatio,
                0.0f, 1.0f,
                0.0f, 100.0f
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