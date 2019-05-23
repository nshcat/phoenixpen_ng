package com.phoenixpen.android.data

import com.phoenixpen.android.ascii.DrawInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type class holding all required data for map features, which are very simple structures.
 */
@Serializable
data class MapFeatureType(
        @SerialName("basic_data") val basicData: StructureType,
        val tile: DrawInfo
)