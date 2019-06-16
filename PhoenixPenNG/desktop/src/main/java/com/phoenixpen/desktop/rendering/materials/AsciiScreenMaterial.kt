package com.phoenixpen.desktop.rendering.materials

import com.jogamp.opengl.GL4
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.desktop.rendering.*
import com.phoenixpen.desktop.application.DesktopResourceProvider
import com.phoenixpen.desktop.rendering.Material
import com.phoenixpen.desktop.rendering.Shader
import com.phoenixpen.desktop.rendering.ShaderProgram
import com.phoenixpen.desktop.rendering.ShaderType
import org.joml.Vector4f

/**
 * A material used to render the ASCII glyph screen.
 *
 * @property gl The OpenGL context
 */
class AsciiScreenMaterial(val gl: GL4, res: DesktopResourceProvider):
        Material(ShaderProgram(
                gl,
                Shader.FromResource(gl, ShaderType.FragmentShader, res, "ascii_fs.glsl"),
                Shader.FromResource(gl, ShaderType.VertexShader, res, "ascii_vs.glsl")
        ))
{
    /**
     * The fog density value. Affects how fast the fog thickens with increasing depth values.
     */
    var fogDensity: Float = .15f

    /**
     * The color of the maximum fog value.
     */
    var fogColor: Vector4f = Vector4f(0.1f, 0.1f, 0.3f, 1f)

    /**
     * The dimensions of the screen, in glyphs (not pixels)
     */
    var screenDimensions: ScreenDimensions = ScreenDimensions.empty()

    /**
     * The dimensions of the glyph texture sheet, in glyphs.
     *
     * This is currently fixed to 16x16 glyphs.
     */
    val sheetDimensions: ScreenDimensions = ScreenDimensions(16, 16)

    /**
     * The dimensions of a single glyph, in pixels.
     *
     * Currently, all glyphs have to be of the same dimensions.
     */
    var glyphDimensions: ScreenDimensions = ScreenDimensions.empty()

    /**
     * Apply uniform data. We only need to set the required textures here.
     */
    override fun applyParameters(params: RenderParams)
    {
        // Set the projection matrix
        uniformMat4f(gl, this.shaderProgram, "proj_mat", params.projection)

        // Set texture samplers
        uniformInt(gl, this.shaderProgram, "tex", 0)            // Main glyph texture
        uniformInt(gl, this.shaderProgram, "shadow_tex", 1)     // Shadow texture
        uniformInt(gl, this.shaderProgram, "input_buffer", 2)   // Buffer texture with screen data

        // Misc uniforms
        uniformVec4f(gl, this.shaderProgram, "fog_color", this.fogColor)
        uniformInt(gl, this.shaderProgram, "screen_width", this.screenDimensions.width)
        uniformInt(gl, this.shaderProgram, "screen_height", this.screenDimensions.height)

        uniformInt(gl, this.shaderProgram, "sheet_width", this.sheetDimensions.width)
        uniformInt(gl, this.shaderProgram, "sheet_height", this.sheetDimensions.height)

        uniformInt(gl, this.shaderProgram, "glyph_width", this.glyphDimensions.width)
        uniformInt(gl, this.shaderProgram, "glyph_height", this.glyphDimensions.height)

        uniformFloat(gl, this.shaderProgram, "fog_density", this.fogDensity)
    }
}
