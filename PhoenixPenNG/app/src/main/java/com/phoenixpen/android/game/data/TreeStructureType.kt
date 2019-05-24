package com.phoenixpen.android.game.data

import com.phoenixpen.android.game.ascii.Position
import kotlinx.serialization.Serializable

/**
 * Type class describing a single tree structure
 *
 * @property identifier Unique identifier for this tree structure type
 * @property structure The actual structure data
 */
@Serializable
class TreeStructureType(
        val identifier: String,
        @Serializable(with=TreeStructureSerializer::class) val structure: TreeStructure)
{
    companion object
    {
        /**
         * Placeholder tree structure, is used if an unknown structure type is encountered
         */
        val placeholder = TreeStructureType(
                "placeholder",
                TreeStructure().apply {
                    this.layers.add(
                            StructureLayer(LayerDimensions(1, 1)).apply {
                                this.entries[0] = PlaceholderType.TrunkCap
                            }
                    )
                }
        )
    }
}