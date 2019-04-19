package com.phoenixpen.android.rendering

import android.opengl.GLES30

/**
 * Allowed values for setting GL_TEXTURE_MIN_FILTER
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureMinFilter(val nativeValue: Int)
{
    Nearest(GLES30.GL_NEAREST),
    Linear(GLES30.GL_LINEAR),
    NearestMipmapNearest(GLES30.GL_NEAREST_MIPMAP_NEAREST),
    LinearMipmapNearest(GLES30.GL_LINEAR_MIPMAP_NEAREST),
    NearestMipmapLinear(GLES30.GL_NEAREST_MIPMAP_LINEAR),
    LinearMipmapLinear(GLES30.GL_LINEAR_MIPMAP_LINEAR);
}

/**
 * Allowed values for setting GL_TEXTURE_MAG_FILTER
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureMagFilter(val nativeValue: Int)
{
    Nearest(GLES30.GL_NEAREST),
    Linear(GLES30.GL_LINEAR);
}

/**
 * Texture units
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureUnit(val nativeValue: Int)
{
    // TODO: More units might be needed in larger applications

    Unit0(GLES30.GL_TEXTURE0),
    Unit1(GLES30.GL_TEXTURE1),
    Unit2(GLES30.GL_TEXTURE2),
    Unit3(GLES30.GL_TEXTURE3),
    Unit4(GLES30.GL_TEXTURE4),
    Unit5(GLES30.GL_TEXTURE5),
    Unit6(GLES30.GL_TEXTURE6);
}