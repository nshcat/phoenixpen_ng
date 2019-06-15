package com.phoenixpen.game.data

import com.phoenixpen.game.ascii.Position
import com.phoenixpen.game.ascii.PositionSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Type class describing a single tree structure
 *
 * @property identifier Unique identifier for this tree structure type
 * @property trunkPosition Where the trunk is located on the first layer. This is needed for tree placement.
 * @property structure The actual structure data
 */
@Serializable
class TreeStructureType(
        val identifier: String,
        @SerialName("trunk_position") @Serializable(with=PositionSerializer::class) val trunkPosition: Position = Position(0, 0),
        @Serializable(with=TreeStructureSerializer::class) val structure: TreeStructure)
{
    companion object
    {
        /**
         * Placeholder tree structure, is used if an unknown structure type is encountered
         */
        val placeholder = TreeStructureType(
                "placeholder",
                structure = TreeStructure().apply {
                    this.layers.add(
                            StructureLayer(LayerDimensions(1, 1)).apply {
                                this.entries[0] = PlaceholderType.TrunkCap
                            }
                    )
                }
        )
    }
}