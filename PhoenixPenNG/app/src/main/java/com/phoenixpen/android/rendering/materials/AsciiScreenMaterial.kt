package com.phoenixpen.android.rendering.materials

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.game.ascii.ScreenDimensions
import com.phoenixpen.android.rendering.*
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
     * The dimensions of the screen, in glyphs (not pixels)
     */
    var screenDimensions: com.phoenixpen.game.ascii.ScreenDimensions = com.phoenixpen.game.ascii.ScreenDimensions.empty()

    /**
     * The dimensions of the glyph texture sheet, in glyphs.
     *
     * This is currently fixed to 16x16 glyphs.
     */
    val sheetDimensions: com.phoenixpen.game.ascii.ScreenDimensions = com.phoenixpen.game.ascii.ScreenDimensions(16, 16)

    /**
     * The dimensions of a single glyph, in pixels.
     *
     * Currently, all glyphs have to be of the same dimensions.
     */
    var glyphDimensions: com.phoenixpen.game.ascii.ScreenDimensions = com.phoenixpen.game.ascii.ScreenDimensions.empty()

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
        uniformInt(this.shaderProgram, "screen_width", this.screenDimensions.width)
        uniformInt(this.shaderProgram, "screen_height", this.screenDimensions.height)

        uniformInt(this.shaderProgram, "sheet_width", this.sheetDimensions.width)
        uniformInt(this.shaderProgram, "sheet_height", this.sheetDimensions.height)

        uniformInt(this.shaderProgram, "glyph_width", this.glyphDimensions.width)
        uniformInt(this.shaderProgram, "glyph_height", this.glyphDimensions.height)

        uniformFloat(this.shaderProgram, "fog_density", this.fogDensity)
    }

    companion object
    {
        /**
         * The shader program this will be used for this material
         */
        /*private val asciiShaderProgram = ShaderProgram(
                Shader.FromResource(ShaderType.FragmentShader, "res/raw/ascii_fs.glsl"),
                Shader.FromResource(ShaderType.VertexShader, "res/raw/ascii_vs.glsl")
        )*/
    }
}
