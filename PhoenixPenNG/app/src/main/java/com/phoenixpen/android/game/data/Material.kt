package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.DrawInfo

/**
 * A specific material instance, based on a [MaterialType]. It stores information unique to the
 * specific cell, such as which visual representation was chosen from the material type.
 *
 * @property type The corresponding material type instance
 * @property glyphIndex Index describing which of the glyph representations was chosen
 * @property fancyGlyphIndex Index describing which of the fancy glyph representations was chosen
 */
data class Material(var type: MaterialType, var glyphIndex: Int, var fancyGlyphIndex: Int)
{
    /**
     * Change material type to given material.
     *
     * @param type New material type
     */
    fun setMaterial(type: MaterialType)
    {
        // Pick visual representation
        val indices = pickIndices(type)

        this.type = type
        this.glyphIndex = indices.first
        this.fancyGlyphIndex = indices.second
    }

    /**
     * Clear this material. This resets it back to air.
     */
    fun clear()
    {
        this.type = MaterialType.air
        this.fancyGlyphIndex = -1
        this.glyphIndex = -1
    }

    /**
     * Retrieve current graphical representation of this material instance.
     *
     * @param fancyMode Flag indicating whether game is in normal or fancy mode
     * @return Draw information instance describing graphical representation of this material
     */
    fun tile(fancyMode: Boolean = true): DrawInfo
    {
        return when(fancyMode)
        {
            true -> this.type.glyphsFancy.elementAt(this.fancyGlyphIndex)
            false -> this.type.glyphs.elementAt(this.glyphIndex)
        }
    }

    /**
     * Some utility methods
     */
    companion object
    {
        /**
         * Create a new material instance from given material type. The visual representation will be
         * randomly chosen.
         */
        fun create(type: MaterialType): Material
        {
            // Pick material glyph indices
            val indices = pickIndices(type)

            return Material(type, indices.first, indices.second)
        }

        /**
         * Pick visual representation for both normal and fancy mode from given material type (which
         * might contain multiple variants for each). Does not work for materials without glyphs.
         *
         * @param type Material type to use
         * @return Pair of indices, first one for normal and second one for fancy mode
         */
        private fun pickIndices(type: MaterialType): Pair<Int, Int>
        {
            // Check that there are in fact entries in both of the weighted lists. Throw exception
            // if thats not the case, since theres nothing we can do in that case.
            if(type.glyphs.isEmpty() || type.glyphsFancy.isEmpty())
                throw IllegalArgumentException("Can't create material from type \"${type.identifier}\" since no glyphs are given")

            // Otherwise just pick indices
            return Pair(type.glyphs.drawIndex(), type.glyphsFancy.drawIndex())
        }

        /**
         * An empty material instance
         */
        val empty = Material(MaterialType.air, -1, -1)
    }
}