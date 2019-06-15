package com.phoenixpen.android.resources

import android.content.Context
import java.nio.file.Paths
import org.apache.commons.io.FilenameUtils;

/**
 * A resource providing based on the Android application resources
 *
 * @property ctx The android application context
 */
class AndroidResourceProvider(val ctx: Context): ResourceProvider
{
    override fun text(id: String): String
    {
        // Strip extension from resource id
        Paths.get(text).
    }

    override fun json(id: String): String
    {
        // JSON files are stored the same way as ordinary text files on Android
        return this.text(id)
    }

    override fun bitmap(id: String): Bitmap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Strip the extension from given resource id.
     *
     * @param id Resource id to strip file extension from
     * @return Resource id without file extension
     */
    private fun stripExtension(id: String): String
    {
        return FilenameUtils.removeExtension(id)
    }

    private fun findResourceId(id: String, type: String): Int
    {
        // First strip the file extension
        val stripped = this.stripExtension(id)

        // Retrieve android resource id
        return this.ctx.resources.getResourcePackageName()

    }
}