package com.phoenixpen.android.rendering.materials

import com.phoenixpen.android.rendering.*

/**
 * A material used to render a white full screen quad. This is meant for testing purposes.
 * Note that this is only to be used with [FullscreenQuad], since it required specific render calls
 * to work (it uses instancing in order to generate the vertices directly on the GPU)
 */
class FullscreenQuadMaterial: Material(quadShaderProgram)
{
    /**
     * We do not apply any of the matrices in [RenderParams] here. In fact, we do nothing
     * since the shader program does not contain any uniforms.
     */
    override fun applyParameters(params: RenderParams)
    {
    }

    companion object
    {
        /**
         * The shader program this will be used for this material
         */
        private val quadShaderProgram = ShaderProgram(
                Shader.FromResource(ShaderType.FragmentShader, "res/raw/quad_fs.glsl"),
                Shader.FromResource(ShaderType.VertexShader, "res/raw/quad_vs.glsl")
        )
    }
}