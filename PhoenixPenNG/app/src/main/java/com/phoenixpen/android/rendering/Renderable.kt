package com.phoenixpen.android.rendering

/**
 * A base interface for types that can be rendered to the screen.
 */
interface Renderable {

    /**
     * Render this mesh to screen using given rendering parameters
     *
     * @param params Rendering parameters to use
     */
    fun render(params: RenderParams)
}