package com.phoenixpen.android.data

import com.phoenixpen.android.ascii.DrawInfo
import com.phoenixpen.android.utility.WeightedList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class defining several properties of a material that can be used as primary content of a
 * map cell
 *
 * @property type MaterialInfo type. This defines in cells of which state this material can be used.
 * @property identifier The unique identifier of this material type.
 * @property description A human readable description of the material
 * @property glyphs A weighted list containing all possible glyphs this material can be rendered as
 * @property glyphsFancy Same as [glyphs], but for fancy graphics mode
 */
@Serializable
data class MaterialInfo(
        val type: MaterialType,
        val identifier: String,
        val description: String,
        val glyphs: WeightedList<DrawInfo>,
        @SerialName("glyphs_fancy") val glyphsFancy: WeightedList<DrawInfo>)