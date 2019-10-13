package com.phoenixpen.android.rendering.materials

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.android.rendering.*
import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.graphics.GlyphDimensions
import org.joml.Vector4f

/**
 * A material used to render the ASCII glyph screen.
 */
class AsciiScreenMaterial(ctx: Context):
        Material(ShaderProgram(
                Shader.FromResource(ShaderType.FragmentShader, ctx, R.raw.ascii_fs),
                Shader.FromResource(ShaderType.VertexShader, ctx, R.raw.ascii_vs)
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
        uniformMat4f(this.shaderProgram, "proj_mat", params.projection)

        // Set texture samplers
        uniformInt(this.shaderProgram, "tex", 0)            // Main glyph texture
        uniformInt(this.shaderProgram, "shadow_tex", 1)     // Shadow texture
        uniformInt(this.shaderProgram, "input_buffer", 2)   // Buffer texture with screen data

        // Misc uniforms
        uniformVec4f(this.shaderProgram, "fog_color", this.fogColor)
        uniformInt(this.shaderProgram, "surface_width", this.surfaceDimensions.width)

        uniformInt(this.shaderProgram, "sheet_width", this.sheetDimensions.width)
        uniformInt(this.shaderProgram, "sheet_height", this.sheetDimensions.height)

        uniformInt(this.shaderProgram, "glyph_width", this.glyphDimensions.baseDimensions.width)
        uniformInt(this.shaderProgram, "glyph_height", this.glyphDimensions.baseDimensions.height)

        uniformFloat(this.shaderProgram, "glyph_scaling_factor", this.glyphDimensions.scaleFactor)

        uniformInt(this.shaderProgram, "position_x", this.position.x)
        uniformInt(this.shaderProgram, "position_y", this.position.y)

        uniformFloat(this.shaderProgram, "fog_density", this.fogDensity)
    }
}
