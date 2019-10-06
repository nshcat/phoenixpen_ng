package com.phoenixpen.desktop.rendering.materials

import com.jogamp.opengl.GL4
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.desktop.rendering.*
import com.phoenixpen.desktop.application.DesktopResourceProvider
import com.phoenixpen.desktop.rendering.Material
import com.phoenixpen.desktop.rendering.Shader
import com.phoenixpen.desktop.rendering.ShaderProgram
import com.phoenixpen.desktop.rendering.ShaderType
import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.graphics.GlyphDimensions
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
     * The dimensions of the surface, in glyphs (not pixels)
     */
    var surfaceDimensions: Dimensions = Dimensions.empty()

    /**
     * The dimensions of the glyph texture sheet, in glyphs.
     *
     * This is currently fixed to 16x16 glyphs.
     */
    val sheetDimensions: Dimensions = Dimensions(16, 16)

    /**
     * Glyph dimension data
     */
    var glyphDimensions: GlyphDimensions = GlyphDimensions()

    /**
     * The absolute position of the top left corner on the device screen, in pixels
     */
    var position: Position = Position()

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
        uniformInt(gl, this.shaderProgram, "surface_width", this.surfaceDimensions.width)
        //uniformInt(gl, this.shaderProgram, "screen_height", this.surfaceDimensions.height)

        uniformInt(gl, this.shaderProgram, "sheet_width", this.sheetDimensions.width)
        uniformInt(gl, this.shaderProgram, "sheet_height", this.sheetDimensions.height)

        uniformInt(gl, this.shaderProgram, "glyph_width", this.glyphDimensions.baseDimensions.width)
        uniformInt(gl, this.shaderProgram, "glyph_height", this.glyphDimensions.baseDimensions.height)

        uniformFloat(gl, this.shaderProgram, "glyph_scaling_factor", this.glyphDimensions.scaleFactor)

        uniformInt(gl, this.shaderProgram, "position_x", this.position.x)
        uniformInt(gl, this.shaderProgram, "position_y", this.position.y)

        uniformFloat(gl, this.shaderProgram, "fog_density", this.fogDensity)
    }
}
