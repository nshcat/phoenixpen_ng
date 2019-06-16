package com.phoenixpen.desktop.rendering.materials

import com.jogamp.opengl.GL4
import com.phoenixpen.desktop.rendering.*
import com.phoenixpen.desktop.application.DesktopResourceProvider

/**
 * A material used to render a texture to a fullscreen quad. Note that this will ignore any
 * matrices in [RenderParams], and will also ignore any vertex attributes.
 * It expects the source texture to be loaded at texture unit
 * Note that this is only to be used with [FullscreenQuad], since it required specific render calls
 * to work (it uses instancing in order to generate the vertices directly on the GPU)
 *
 * @property gl The OpenGL context
 * @param res The Resource provider to load shader source from
 */
class TexturedQuadMaterial(val gl: GL4, res: DesktopResourceProvider):
        Material(ShaderProgram(
                gl,
                Shader.FromResource(gl, ShaderType.FragmentShader, res, "quad_fs.glsl"),
                Shader.FromResource(gl, ShaderType.VertexShader, res, "quad_vs.glsl")
        ))
{
    /**
     * We do not apply any of the matrices in [RenderParams] here. We only set the texture sample
     * to unit [TextureUnit.Unit0].
     */
    override fun applyParameters(params: RenderParams)
    {
        // Set the texture uniform to unit 0
        uniformInt(gl, this.shaderProgram, "srcTexture", 0)
    }
}