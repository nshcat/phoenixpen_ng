package com.phoenixpen.android.rendering

import android.opengl.GLES30
import android.util.Log
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import java.nio.ByteBuffer
import java.nio.FloatBuffer

/**
 * Upload 4x4 float matrix as uniform value
 *
 * @param program ShaderProgram the uniform is contained in
 * @param name Uniform name
 * @param mat Matrix to upload
 */
fun uniformMat4f(program: ShaderProgram, name: String, mat: Matrix4f)
{
    // Try to find the uniform location
    val location = checkedUniformLocation(program, name)

    // Allocate a buffer of appropiate size
    val buffer = allocateFloatBuffer(16)

    // Save matrix contents
    mat.get(buffer)

    buffer.position(0)

    // Upload contents
    GLES30.glUniformMatrix4fv(location, 1, false, buffer)
}

/**
 * Upload 3x3 float matrix as uniform value
 *
 * @param program ShaderProgram the uniform is contained in
 * @param name Uniform name
 * @param mat Matrix to upload
 */
fun uniformMat3f(program: ShaderProgram, name: String, mat: Matrix3f)
{
    // Try to find the uniform location
    val location = checkedUniformLocation(program, name)

    // Allocate a buffer of appropiate size
    val buffer = allocateFloatBuffer(9)

    // Save matrix contents
    mat.get(buffer)

    buffer.position(0)

    // Upload contents
    GLES30.glUniformMatrix3fv(location, 1, false, buffer)
}

/**
 * Upload 3d float vector as uniform value
 *
 * @param program ShaderProgram the uniform is contained in
 * @param name Uniform name
 * @param vec Vector
 */
fun uniformVec3f(program: ShaderProgram, name: String, vec: Vector3f)
{
    // Try to find the uniform location
    val location = checkedUniformLocation(program, name)

    // Allocate a buffer of appropiate size
    val buffer = allocateFloatBuffer(3)

    // Save matrix contents
    vec.get(buffer)

    // Upload contents
    GLES30.glUniform3fv(location, 1, buffer)
}

/**
 * Upload 4d float vector as uniform value
 *
 * @param program ShaderProgram the uniform is contained in
 * @param name Uniform name
 * @param vec Vector
 */
fun uniformVec4f(program: ShaderProgram, name: String, vec: Vector4f)
{
    // Try to find the uniform location
    val location = checkedUniformLocation(program, name)

    // Allocate a buffer of appropiate size
    val buffer = allocateFloatBuffer(4)

    // Save matrix contents
    vec.get(buffer)

    // Upload contents
    GLES30.glUniform4fv(location, 1, buffer)
}

/**
 * Upload a single integer as uniform value
 *
 * @param program Shader program the uniform is contained in
 * @param name Uniform name
 * @param value Value to upload
 */
fun uniformInt(program: ShaderProgram, name: String, value: Int)
{
    // Try to find the uniform location
    val location = checkedUniformLocation(program, name)

    // Upload contents
    GLES30.glUniform1i(location, value)
}

/**
 * Allocate a float buffer for use with uniform upload
 *
 * @param size Size of the buffer in number of floats
 * @return Allocated FloatBuffer of requested size
 */
private fun allocateFloatBuffer(size: Int): FloatBuffer
{
    val buf = ByteBuffer.allocateDirect(size * 4)
    return buf.asFloatBuffer()
}

/**
 * Tries to receive the uniform location for given named uniform in given program.
 * This will emit a debug message and throw an exception if the location wasnt found.
 *
 * @param program Shader program to search uniform in
 * @param name Name of the uniform
 * @return OpenGL uniform location value
 */
private fun checkedUniformLocation(program: ShaderProgram, name: String): Int
{
    // Retrieve uniform location
    val location = GLES30.glGetUniformLocation(program.handle, name)

    // Check if location is known
    if(location == -1)
    {
        Log.e("setUniform", "Unknown uniform location: \"$name\"")
        throw IllegalArgumentException("unknown uniform location")
    }
    else return location
}