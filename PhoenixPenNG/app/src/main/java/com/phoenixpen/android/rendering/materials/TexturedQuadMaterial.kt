package com.phoenixpen.android.rendering.materials

import com.phoenixpen.android.rendering.*

/**
 * A material used to render a texture to a fullscreen quad. Note that this will ignore any
 * matrices in [RenderParams], and will also ignore any vertex attributes.
 * It expects the source texture to be loaded at texture unit
 * Note that this is only to be used with [FullscreenQuad], since it required specific render calls
 * to work (it uses instancing in order to generate the vertices directly on the GPU)
 */
class TexturedQuadMaterial: Material(quadShaderProgram)
{
    /**
     * We do not apply any of the matrices in [RenderParams] here. We only set the texture sample
     * to unit [TextureUnit.Unit0].
     */
    override fun applyParameters(params: RenderParams)
    {
        // Set the texture uniform to unit 0
        uniformInt(this.shaderProgram, "srcTexture", 0)
    }

    companion object
    {
        /**
         * The shader program this will be used for this material
         */
        private val quadShaderProgram = ShaderProgram(
                Shader.FromResource(ShaderType.FragmentShader, "res/raw/textured_quad_fs.glsl"),
                Shader.FromResource(ShaderType.VertexShader, "res/raw/textured_quad_vs.glsl")
        )
    }
}