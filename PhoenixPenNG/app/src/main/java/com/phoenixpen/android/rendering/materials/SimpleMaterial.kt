package com.phoenixpen.android.rendering.materials

import com.phoenixpen.android.rendering.*

/**
 * A simple material that ignores all light sources in the scene and applies a faux lighting
 * to all meshed rendered with it. This should only be used for testing
 */
class SimpleMaterial: Material(simpleShaderProgram)
{
    /**
     * Apply rendering parameters. This type of material does not have any attributes
     * that need uploading.
     */
    override fun applyParameters(params: RenderParams)
    {
        this.shaderProgram.applyParameters(params)
    }

    companion object
    {
        /**
         * The shader program this will be used for this material
         */
        private val simpleShaderProgram = ShaderProgram(
                Shader.FromResource(ShaderType.FragmentShader, "res/raw/simple_fs.glsl"),
                Shader.FromResource(ShaderType.VertexShader, "res/raw/simple_vs.glsl")
        )
    }
}