package com.phoenixpen.android.game.data

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