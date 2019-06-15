package com.phoenixpen.android.resources

/**
 * An interface for classes that provide essential game resources
 */
interface ResourceProvider
{
    /**
     * Retrieve the complete contents of a text file.
     *
     * @param id Resource id string
     * @return Text file contents, including new lines
     */
    fun text(id: String): String

    /**
     * Retrieve the complete contents of a  JSON file. This basically does the same thing as
     * [text], but resource provider implementations might choose to store JSON documents in
     * a different place.
     *
     * @param id Resource id string
     * @return JSON file contents, including new lines
     */
    fun json(id: String): String

    /**
     * Retrieve bitmap from resources
     *
     * @param id Resource id string
     * @return Bitmap read from resource
     */
    fun bitmap(id: String): Bitmap
    // TODO val factoryOptions = BitmapFactory.Options().apply { inScaled = false } is important
}