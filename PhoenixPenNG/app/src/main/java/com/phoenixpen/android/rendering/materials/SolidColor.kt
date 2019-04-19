package com.phoenixpen.android.rendering.materials

import com.phoenixpen.android.rendering.*

/**
 * A simple material that ignores all light sources in the scene and displays the colors at full
 * intensity, without any faux lighting.
 */
class SolidColor: Material(solidShaderProgram)
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
        private val solidShaderProgram = ShaderProgram(
                Shader.FromResource(ShaderType.FragmentShader, "res/raw/solid_fs.glsl"),
                Shader.FromResource(ShaderType.VertexShader, "res/raw/solid_vs.glsl")
        )
    }
}