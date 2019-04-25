package com.phoenixpen.android.rendering

import android.opengl.GLES31

/**
 * An abstract base class for OpenGL texture objects. Subclasses might implement application specific
 * configuration, e.g. for textures used as frame buffer object backing.
 */
abstract class Texture
{
    /**
     * The native OpenGL handle of the texture object. This is mutable since we want to allow
     * regeneration of the texture, e.g. on screen size change if used as FBO backing texture.
     */
    var handle: Int = GLES31.GL_NONE
        protected set

    /**
     * Activate the texture object managed by this instance.
     *
     * @param textureUnit Texture unit to bind texture to
     */
    abstract fun use(textureUnit: TextureUnit)
}