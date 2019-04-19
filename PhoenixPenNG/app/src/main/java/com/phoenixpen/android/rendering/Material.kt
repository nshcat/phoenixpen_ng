package com.phoenixpen.android.rendering

/**
 * A base class for Materials, that is objects that control how a mesh is rendered and how it
 * reacts to lighting in the scene. Materials can have arbitrary shader programs and attributes
 * attached to them.
 *
 * @property shaderProgram The underlying shader program used for rendering the material
 */
abstract class Material (val shaderProgram: ShaderProgram)
{
    /**
     * Activate this material to be used for rendering. This call
     * has to be followed up by a call to @see applyParameters in
     * order to set all needed parameters and upload attributes.
     */
    fun use()
    {
        this.shaderProgram.use()
    }

    /**
     * Upload all required parameters and attributes to the underlying shader program.
     */
    abstract fun applyParameters(params: RenderParams)
}