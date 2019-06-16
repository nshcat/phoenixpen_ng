package com.phoenixpen.desktop.application

import com.phoenixpen.game.ascii.Color
import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.resources.Bitmap
import com.phoenixpen.game.resources.ResourceProvider
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO

/**
 * Resource provider implementation for the desktop app. Expects different types of resources in subfolders
 * under [prefix].
 *
 * @property prefix Path to resource directory
 */
class DesktopResourceProvider(private val prefix: Path): ResourceProvider
{
    /**
     * Retrieve text file contents
     *
     * @param id Name of file to retrieve
     * @return Contents of the text file
     */
    override fun text(id: String): String
    {
        // Build combined path
        val path = this.prefix.resolve(Paths.get("text", id))

        // Read all text
        return this.readTextFile(path)
    }

    /**
     * Retrieve JSON file contents
     *
     * @param id Name of the file to retrieve
     * @return Contents of the JSON file
     */
    override fun json(id: String): String
    {
        // Build combined path
        val path = this.prefix.resolve(Paths.get("json", id))

        // Read all text
        return this.readTextFile(path)
    }

    /**
     * Retrieve shader file contents
     *
     * @param id Name of the file to retrieve
     * @return Contents of the shader file
     */
    fun shader(id: String): String
    {
        // Build combined path
        val path = this.prefix.resolve(Paths.get("shaders", id))

        // Read all text
        return this.readTextFile(path)
    }

    /**
     * Retrieve bitmap file
     *
     * @param id Name of the bitmap
     * @return Bitmap loaded from file
     */
    override fun bitmap(id: String): Bitmap
    {
        // Build combined path
        val path = this.prefix.resolve(Paths.get("images", id))

        // Retrieve buffered image
        val bufImage = ImageIO.read(Files.newInputStream(path))

        // Create internal bitmap instance
        val bitmap = Bitmap(bufImage.width, bufImage.height)

        // Copy pixels
        for(ix in 0 until bitmap.width)
        {
            for(iy in 0 until bitmap.height)
            {
                // Retrieve pixel at position (ix, iy)
                val pixel = bufImage.getRGB(ix, iy)

                // Retrieve color channels
                val red = pixel and 0x00ff0000 shr 16
                val green = pixel and 0x0000ff00 shr 8
                val blue = pixel and 0x000000ff

                // Save in destination bitmap
                bitmap.setPixelAt(Position(ix, iy), Color(red, green, blue))
            }
        }


        return bitmap
    }

    /**
     * Retrieve buffered image
     *
     * @param id Name of the bitmap
     * @return BufferedImage loaded from file
     */
    fun bufferedImage(id: String): BufferedImage
    {
        // Build combined path
        val path = this.prefix.resolve(Paths.get("images", id))

        // Retrieve buffered image
        return ImageIO.read(Files.newInputStream(path))
    }

    /**
     * Retrieve path to texture
     *
     * @param id Name of the texture
     * @return Path to the texture
     */
    fun texturePath(id: String): Path
    {
        // Build combined path
        return this.prefix.resolve(Paths.get("textures", id))
    }

    /**
     * Read whole content of a text file into a string
     *
     * @param path Path of the file to read
     * @return Complete contents of the specified file
     */
    private fun readTextFile(path: Path): String
    {
        return Files.readAllLines(path).joinToString("\n")
    }
}