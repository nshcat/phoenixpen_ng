package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4


/**
 * Allowed values for setting GL_TEXTURE_MIN_FILTER
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureMinFilter(val nativeValue: Int)
{
    Nearest(GL4.GL_NEAREST),
    Linear(GL4.GL_LINEAR),
    NearestMipmapNearest(GL4.GL_NEAREST_MIPMAP_NEAREST),
    LinearMipmapNearest(GL4.GL_LINEAR_MIPMAP_NEAREST),
    NearestMipmapLinear(GL4.GL_NEAREST_MIPMAP_LINEAR),
    LinearMipmapLinear(GL4.GL_LINEAR_MIPMAP_LINEAR);
}

/**
 * Allowed values for setting GL_TEXTURE_MAG_FILTER
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureMagFilter(val nativeValue: Int)
{
    Nearest(GL4.GL_NEAREST),
    Linear(GL4.GL_LINEAR);
}

/**
 * Texture units
 *
 * @property nativeValue The native OpenGL value
 */
enum class TextureUnit(val nativeValue: Int)
{
    // TODO: More units might be needed in larger applications

    Unit0(GL4.GL_TEXTURE0),
    Unit1(GL4.GL_TEXTURE1),
    Unit2(GL4.GL_TEXTURE2),
    Unit3(GL4.GL_TEXTURE3),
    Unit4(GL4.GL_TEXTURE4),
    Unit5(GL4.GL_TEXTURE5),
    Unit6(GL4.GL_TEXTURE6);
}