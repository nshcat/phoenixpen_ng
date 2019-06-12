package com.phoenixpen.android.game.data.biome

import com.phoenixpen.android.game.ascii.Color
import com.phoenixpen.android.game.core.WeightedTileListSerializer
import com.phoenixpen.android.game.core.WeightedTypeList
import com.phoenixpen.android.game.core.WeightedTypeListSerializer
import kotlinx.serialization.Serializable

/**
 * A single entry in a type key, containing a density and all possible types, weighted with their probability.
 *
 * @property density The chance a value will be picked from the weighted list
 * @property types All possible types
 */
@Serializable
data class TypeKeyEntry(
        val density: Double,
        @Serializable(with= WeightedTypeListSerializer::class) val types: WeightedTypeList
)

/**
 * A typealias used to implement the t ypekey, which can be used to look up the colors in the
 * biome template layer images
 */
typealias TypeKey = Map<Color, TypeKeyEntry>