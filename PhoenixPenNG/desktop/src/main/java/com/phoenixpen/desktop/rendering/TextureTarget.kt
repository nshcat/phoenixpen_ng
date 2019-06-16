package com.phoenixpen.desktop.rendering

import com.jogamp.opengl.GL4

/**
 * A render target that renders the scene to a GPU texture. This is useful if any postprocessing
 * must be applied to the rendered image before it is shown to the user.
 *
 * @param gl The OpenGL context
 */
class TextureTarget(gl: GL4): RenderTarget(gl)
{
    /**
     * The actual texture that will be rendered to. This will be initialized upon the first
     * screen resize that is not empty. This is done because in Android apps, the screen is created
     * with an initial size of (0,0), and later resized to fit the screen.
     */
    lateinit var texture: Texture2D

    /**
     * The handle of the frame buffer object used to render to [texture]
     */
    protected var fboHandle: Int = GL4.GL_NONE

    /**
     * The handle of our depth materials buffer
     */
    protected var depthBufferHandle: Int = GL4.GL_NONE

    /**
     * Initialize frame buffer object
     */
    init
    {
        // Try to create a new FBO
        val fboHandles = IntArray(1)
        gl.glGenFramebuffers(1, fboHandles, 0)

        // Check for success
        if(fboHandles[0] == GL4.GL_NONE)
            throw IllegalStateException("Could not create new FBO")

        // Otherwise just save the handle for later
        this.fboHandle = fboHandles[0]
    }

    /**
     * Handle screen resize by resizing the dimensions of the underlying texture and depth buffer
     *
     * @param renderDimensions The new screen dimensions
     */
    override fun updateDimensions(renderDimensions: com.phoenixpen.game.ascii.ScreenDimensions)
    {
        super.updateDimensions(renderDimensions)

        // Only resize texture if the new dimensions are not empty
        if(!renderDimensions.isEmpty())
        {
            // Create texture parameters
            val parameters = Texture2DParameters(dimensions = this.renderDimensions)

            // If the texture wasnt yet initialized, do so for the first time
            if(!::texture.isInitialized)
            {
                this.texture = Texture2D(gl, parameters)
            }
            else
            {
                // Otherwise just recreate the texture
                this.texture.recreate(parameters)
            }

            // We also need a new depth buffer.
            // Destroy it if it already exists
            if(this.depthBufferHandle != GL4.GL_NONE)
            {
                // Create temporary array to hold our depth buffer handle. This sadly is needed
                // because the API needs to support deletion of multiple buffers at once.
                val renderBuffers = intArrayOf(this.depthBufferHandle)

                // Delete the depth buffer
                gl.glDeleteRenderbuffers(1, renderBuffers, 0)
            }

            // Generate new depth renderbuffer
            val renderBuffers = IntArray(1)
            gl.glGenRenderbuffers(1, renderBuffers, 0)

            // Check for success
            if(renderBuffers[0] == GL4.GL_NONE)
                throw IllegalStateException("TextureTarget: Could not create depth buffer")

            // Assign handle
            this.depthBufferHandle = renderBuffers[0]

            // Activate the FBO
            gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, this.fboHandle)

            // Attach it to our framebuffer object
            gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, this.depthBufferHandle)
            gl.glRenderbufferStorage(GL4.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT24,
                    this.renderDimensions.width, this.renderDimensions.height)

            // We generated a new texture earlier, which now has to be attached to the FBO.
            gl.glFramebufferRenderbuffer(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT,
                    GL4.GL_RENDERBUFFER, this.depthBufferHandle)

            // Make sure the texture is activated
            this.texture.use(TextureUnit.Unit0)

            // Associate it with the frame buffer object
            gl.glFramebufferTexture2D(GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0,
                    GL4.GL_TEXTURE_2D, this.texture.handle, 0)

            // Set up draw buffers. It works without this, but might be needed on other devices
            val drawBuffers = intArrayOf(GL4.GL_COLOR_ATTACHMENT0)
            gl.glDrawBuffers(1, drawBuffers, 0)

            // Check for success
            if(gl.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER) != GL4.GL_FRAMEBUFFER_COMPLETE)
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
        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, this.fboHandle);

        // Apply viewport setup
        super.beginRender()

        // Enable depth testing
        gl.glEnable(GL4.GL_DEPTH_TEST)

        // Clear the screen, both color and depth buffer
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT or GL4.GL_DEPTH_BUFFER_BIT)
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
        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0)
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