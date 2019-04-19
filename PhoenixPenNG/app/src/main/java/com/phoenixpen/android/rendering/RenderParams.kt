package com.phoenixpen.android.rendering

import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*

/**
 * A class implementing a matrix stack used in rendering with hierarchical transformations
 *
 * @property view The view Matrix
 * @property projection The projection Matrix
 */
class RenderParams (val view: Matrix4f,  val projection: Matrix4f)
{
    // TODO: In all methods, check if the transformation is the identity function (e.g. scaling of 1.0)
    // TODO: and if so, dont do anything

    /**
     * The current model matrix.
     */
    var model: Matrix4f = Matrix4f()

    /**
     * The current normal vector transformation matrix. This is derived from the model matrix.
     */
    val normalTransform: Matrix3f
        get() {

            // We disregard translations when adjusting normal vectors, so just extract
            // the upper 3x3 matrix here
            val dest = Matrix3f()
            this.model.get3x3(dest)

            // According to literature, just inverting and then transposing the matrix will
            // result in a correct normal transformation matrix that also can deal with
            // non-uniform scaling
            dest.invert()
            dest.transpose()

            return dest
        }

    /**
     * The matrix stack used to implement hierarchical transformations
     */
    protected var stack: Deque<Matrix4f> = ArrayDeque()

    companion object
    {
        /**
         * Create empty render parameters for use with materials that dont care about the matrices
         */
        fun empty() = RenderParams(Matrix4f(), Matrix4f())
    }

    /**
     * Replace current model matrix with the topmost matrix on the stack and
     * popping that matrix.
     */
    fun popMatrix()
    {
        if(this.stack.isEmpty())
            throw IllegalStateException("Matrix stack is empty")
        else
        {
            // Retrieve topmost matrix from stack
            val matrix = this.stack.pop()

            // Replace model matrix with it
            this.model = matrix;
        }
    }

    /**
     * Push the current model matrix onto the matrix stack for later
     * retrieval.
     */
    fun pushMatrix()
    {
        // Save current model matrix on matrix stack
        this.stack.push(this.model)
    }

    /**
     * Add a translation to the current model matrix
     *
     * @param vec Vector to translate by
     */
    fun translate(vec: Vector3f)
    {
        this.model *= Matrix4f().translation(vec)
    }

    /**
     * Rotate around the Z-axis by given angle
     *
     * @param angle Angle to rotate by, in radians
     */
    fun rotateZ(angle: Float)
    {
        this.model *= Matrix4f().rotateZ(angle)
    }

    /**
     * Rotate around the Y-axis by given angle
     *
     * @param angle Angle to rotate by, in radians
     */
    fun rotateY(angle: Float)
    {
        this.model *= Matrix4f().rotateY(angle)
    }

    /**
     * Rotate around the X-axis by given angle
     *
     * @param angle Angle to rotate by, in radians
     */
    fun rotateX(angle: Float)
    {
        this.model *= Matrix4f().rotateX(angle)
    }

    /**
     * Rotate by given quaternion
     *
     * @param rot Rotation expressed as quaternion
     */
    fun rotate(rot: Quaternionf)
    {
        this.model *= Matrix4f().rotate(rot)
    }


    /**
     * Apply uniform scaling to model
     *
     * @param factor Scaling factor
     */
    fun scale(factor: Float)
    {
        this.model *= Matrix4f().scale(factor)
    }

    /**
     * Apply scaling in X direction to model
     *
     * @param factor Scaling factor
     */
    fun scaleX(factor: Float)
    {
        this.model *= Matrix4f().scale(factor, 1f, 1f)
    }

    /**
     * Apply scaling in Y direction to model
     *
     * @param factor Scaling factor
     */
    fun scaleY(factor: Float)
    {
        this.model *= Matrix4f().scale(1f, factor, 1f)
    }

    /**
     * Apply scaling in Z direction to model
     *
     * @param factor Scaling factor
     */
    fun scaleZ(factor: Float)
    {
        this.model *= Matrix4f().scale(1f, 1f, factor)
    }
}