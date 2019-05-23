package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.ascii.DrawInfo
import com.phoenixpen.android.utility.WeightedList
import com.phoenixpen.android.utility.WeightedPair
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class defining several properties of a material that can be used as primary content of a
 * map cell. This represents the type class pattern.
 *
 * TODO:
 *  - Pathing
 *
 * @property type MaterialType type. This defines in cells of which state this material can be used.
 * @property identifier The unique identifier of this material type.
 * @property description A human readable description of the material
 * @property glyphs A weighted list containing all possible glyphs this material can be rendered as
 * @property glyphsFancy Same as [glyphs], but for fancy graphics mode
 */
@Serializable
data class MaterialType(
        //val type: MapCellState,
        val identifier: String,
        val description: String,
        val glyphs: WeightedList<DrawInfo>,
        @SerialName("glyphs_fancy") val glyphsFancy: WeightedList<DrawInfo>)
{
    /**
     * Default materials
     */
    companion object
    {
        /**
         * Placeholder material intended to be used if a material can not be found anymore. Very visible
         * to make debugging easier
         */
        val placeholder = MaterialType("placeholder", "Placeholder material",
                WeightedList(listOf(WeightedPair(DrawInfo(88, Color.black, Color.magenta), 1.0))),
                WeightedList(listOf(WeightedPair(DrawInfo(88, Color.black, Color.magenta), 1.0))))

        /**
         * Special air material that will be used for air cells. Will not be drawn.
         */
        val air = MaterialType("air", "Placeholder material for air cells", WeightedList(listOf()), WeightedList(listOf()))
    }
}