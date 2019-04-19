package com.phoenixpen.android.rendering

import android.opengl.GLES30
import com.phoenixpen.android.application.ScreenDimensions

/**
 * A render target that renders the scene to a GPU texture. This is useful if any postprocessing
 * must be applied to the rendered image before it is shown to the user.
 */
class TextureTarget: RenderTarget()
{
    /**
     * The actual texture that will be rendered to. This will be initialized upon the first
     * screen resize that is not empty. This is done because in Android apps, the screen is created
     * with an initial size of (0,0), and later resized to fit the screen.
     */
    lateinit var texture: Texture

    /**
     * The handle of the frame buffer object used to render to [texture]
     */
    protected var fboHandle: Int = GLES30.GL_NONE

    /**
     * The handle of our depth test buffer
     */
    protected var depthBufferHandle: Int = GLES30.GL_NONE

    /**
     * Initialize frame buffer object
     */
    init
    {
        // Try to create a new FBO
        val fboHandles = IntArray(1)
        GLES30.glGenFramebuffers(1, fboHandles, 0)

        // Check for success
        if(fboHandles[0] == GLES30.GL_NONE)
            throw IllegalStateException("Could not create new FBO")

        // Otherwise just save the handle for later
        this.fboHandle = fboHandles[0]
    }

    /**
     * Handle screen resize by resizing the dimensions of the underlying texture and depth buffer
     *
     * @param renderDimensions The new screen dimensions
     */
    override fun updateDimensions(renderDimensions: ScreenDimensions)
    {
        super.updateDimensions(renderDimensions)

        // Only resize texture if the new dimensions are not empty
        if(!renderDimensions.isEmpty())
        {
            // Create texture parameters
            val parameters = TextureParameters(dimensions = this.renderDimensions)

            // If the texture wasnt yet initialized, do so for the first time
            if(!::texture.isInitialized)
            {
                this.texture = Texture(parameters)
            }
            else
            {
                // Otherwise just recreate the texture
                this.texture.recreate(parameters)
            }

            // We also need a new depth buffer.
            // Destroy it if it already exists
            if(this.depthBufferHandle != GLES30.GL_NONE)
            {
                // Create temporary array to hold our depth buffer handle. This sadly is needed
                // because the API needs to support deletion of multiple buffers at once.
                val renderBuffers = intArrayOf(this.depthBufferHandle)

                // Delete the depth buffer
                GLES30.glDeleteRenderbuffers(1, renderBuffers, 0)
            }

            // Generate new depth renderbuffer
            val renderBuffers = IntArray(1)
            GLES30.glGenRenderbuffers(1, renderBuffers, 0)

            // Check for success
            if(renderBuffers[0] == GLES30.GL_NONE)
                throw IllegalStateException("TextureTarget: Could not create depth buffer")

            // Assign handle
            this.depthBufferHandle = renderBuffers[0]

            // Activate the FBO
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, this.fboHandle)

            // Attach it to our framebuffer object
            GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, this.depthBufferHandle)
            GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT24,
                    this.renderDimensions.width, this.renderDimensions.height)

            // We generated a new texture earlier, which now has to be attached to the FBO.
            GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT,
                    GLES30.GL_RENDERBUFFER, this.depthBufferHandle)

            // Make sure the texture is activated
            this.texture.use(TextureUnit.Unit0)

            // Associate it with the frame buffer object
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                    GLES30.GL_TEXTURE_2D, this.texture.handle, 0)

            // Set up draw buffers. It works without this, but might be needed on other devices
            val drawBuffers = intArrayOf(GLES30.GL_COLOR_ATTACHMENT0)
            GLES30.glDrawBuffers(1, drawBuffers, 0)

            // Check for success
            if(GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE)
                throw IllegalStateException("TextureTarget: Couldn't complete framebuffer initialization")
        }
    }

    /**
     * Begin rendering to this texture rendering target
     */
    override fun beginRender()
    {
        // We can't render to this if the texture hasnt yet been initialized
        if(!::texture.isInitialized)
            return

        // Activate FBO
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, this.fboHandle);

        // Apply viewport setup
        super.beginRender()

        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Clear the screen, both color and depth buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
    }

    /**
     * Stop rendering to this texture rendering target
     */
    override fun endRender()
    {
        // Nothing to do if the texture wasnt initialized yet
        if(!::texture.isInitialized)
            return

        // Disable FBO and return to default frame buffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }

    /**
     * Check whether this render target currently has a useable texture.
     *
     * @return Flag indicating whether the underlying texture is initialized
     */
    fun hasTexture(): Boolean
    {
        return ::texture.isInitialized
    }
}