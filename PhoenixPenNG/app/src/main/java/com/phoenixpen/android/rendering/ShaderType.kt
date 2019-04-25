package com.phoenixpen.android.rendering

import android.opengl.GLES31

/**
 * An enumeration describing all supported types of OpenGL shader
 *
 * @property nativeValue The native OpenGL value of the shader type
 */
enum class ShaderType (val nativeValue: Int)
{
    VertexShader(GLES31.GL_VERTEX_SHADER),
    FragmentShader(GLES31.GL_FRAGMENT_SHADER)
}