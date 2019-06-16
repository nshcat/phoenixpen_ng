package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4
import com.phoenixpen.desktop.application.DesktopResourceProvider
import com.phoenixpen.desktop.rendering.materials.TexturedQuadMaterial

/**
 * A class representing a white quad spanning the whole screen. This is meant for testing purposes.
 */
class FullscreenQuad(val gl: GL4, res: DesktopResourceProvider, material: Material = TexturedQuadMaterial(gl, res)): Shadeable(material)
{
    override fun render(params: RenderParams)
    {
        // Make sure the material is loaded and setup correctly
        super.render(params)

        // Render the quad using instancing. The material shader will generate the vertices
        // for use based on the instance vertex id.
        gl.glDrawArraysInstanced(GL4.GL_TRIANGLES, 0, 6, 1)
    }
}