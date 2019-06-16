package com.phoenixpen.desktop.rendering

/**
 * An abstract base class for all renderable entities that use a material.
 * All materials need to expect the projection, view and model matrix as an uniform argument.
 *
 * @property material The material to use for rendering
 */
abstract class Shadeable (val material: Material): Renderable
{
    /**
     * Activate the material and apply render parameters
     */
    override fun render(params: RenderParams)
    {
        this.material.use()
        this.material.applyParameters(params)
    }
}
