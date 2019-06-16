package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4


/**
 * An enumeration describing all supported types of OpenGL shader
 *
 * @property nativeValue The native OpenGL value of the shader type
 */
enum class ShaderType (val nativeValue: Int)
{
    VertexShader(GL4.GL_VERTEX_SHADER),
    FragmentShader(GL4.GL_FRAGMENT_SHADER)
}