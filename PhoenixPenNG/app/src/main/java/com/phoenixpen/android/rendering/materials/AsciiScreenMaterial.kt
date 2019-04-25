package com.phoenixpen.android.rendering.materials

import com.phoenixpen.android.rendering.*

/**
 * A material used to render the ASCII glyph screen.
 */
class AsciiScreenMaterial: Material(asciiShaderProgram)
{
    /**
     * Apply uniform data. We only need to set the required textures here.
     */
    override fun applyParameters(params: RenderParams)
    {

    }

    companion object
    {
        /**
         * The shader program this will be used for this material
         */
        private val asciiShaderProgram = ShaderProgram(
                Shader.FromResource(ShaderType.FragmentShader, "res/raw/ascii_fs.glsl"),
                Shader.FromResource(ShaderType.VertexShader, "res/raw/ascii_vs.glsl")
        )
    }
}
