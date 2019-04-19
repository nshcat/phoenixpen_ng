package com.phoenixpen.android.rendering

import android.opengl.GLES30
import com.phoenixpen.android.rendering.materials.FullscreenQuadMaterial
import com.phoenixpen.android.rendering.materials.TexturedQuadMaterial

/**
 * A class representing a white quad spanning the whole screen. This is meant for testing purposes.
 */
class FullscreenQuad(material: Material = TexturedQuadMaterial()): Shadeable(material)
{
    override fun render(params: RenderParams)
    {
        // Make sure the material is loaded and setup correctly
        super.render(params)

        // Render the quad using instancing. The material shader will generate the vertices
        // for use based on the instance vertex id.
        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, 6, 1)
    }
}