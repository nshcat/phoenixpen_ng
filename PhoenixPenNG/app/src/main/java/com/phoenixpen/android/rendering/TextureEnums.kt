package com.phoenixpen.android.rendering

import android.opengl.GLES31

/**
 * Allowed values for setting GL_TEXTURE_MIN_FILTER
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureMinFilter(val nativeValue: Int)
{
    Nearest(GLES31.GL_NEAREST),
    Linear(GLES31.GL_LINEAR),
    NearestMipmapNearest(GLES31.GL_NEAREST_MIPMAP_NEAREST),
    LinearMipmapNearest(GLES31.GL_LINEAR_MIPMAP_NEAREST),
    NearestMipmapLinear(GLES31.GL_NEAREST_MIPMAP_LINEAR),
    LinearMipmapLinear(GLES31.GL_LINEAR_MIPMAP_LINEAR);
}

/**
 * Allowed values for setting GL_TEXTURE_MAG_FILTER
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureMagFilter(val nativeValue: Int)
{
    Nearest(GLES31.GL_NEAREST),
    Linear(GLES31.GL_LINEAR);
}

/**
 * Texture units
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureUnit(val nativeValue: Int)
{
    // TODO: More units might be needed in larger applications

    Unit0(GLES31.GL_TEXTURE0),
    Unit1(GLES31.GL_TEXTURE1),
    Unit2(GLES31.GL_TEXTURE2),
    Unit3(GLES31.GL_TEXTURE3),
    Unit4(GLES31.GL_TEXTURE4),
    Unit5(GLES31.GL_TEXTURE5),
    Unit6(GLES31.GL_TEXTURE6);
}