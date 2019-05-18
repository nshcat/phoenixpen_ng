package com.phoenixpen.android.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class defining several properties of a material that can be used as primary content of a
 * map cell
 *
 * @property type MaterialInfo type. This defines in cells of which state this material can be used.
 * @property identifier The unique identifier of this material type.
 */
@Serializable
data class MaterialInfo(
        val type: MaterialType,
        val identifier: String)