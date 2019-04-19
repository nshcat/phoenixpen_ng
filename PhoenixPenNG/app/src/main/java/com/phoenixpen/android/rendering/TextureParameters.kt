package com.phoenixpen.android.rendering

/**
 * A class holding all information needed to create a texture.
 *
 * @property dimensions The texture dimensions, in pixels.
 * @property magFilter The value to use for GL_TEXTURE_MAG_FILTER
 * @property minFilter The value to use for GL_TEXTURE_MIN_FILTER
 */
data class TextureParameters
    (var dimensions: TextureDimensions,
     var magFilter: TextureMagFilter = TextureMagFilter.Linear,
     var minFilter: TextureMinFilter = TextureMinFilter.Linear)
