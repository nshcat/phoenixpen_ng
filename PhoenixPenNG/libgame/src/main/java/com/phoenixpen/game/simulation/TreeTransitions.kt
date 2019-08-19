package com.phoenixpen.game.simulation

import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.data.TreePart

/**
 * Animated transition used to drop tree leaves in autumn
 */
class LeafDropTransition(
        simulation: Simulation,
        droppedLeaves: MutableList<Covering>,
        treeParts: Collection<TreePart>
):  CoveringDropTransition(
        simulation,
        droppedLeaves,
        LeafState.Dropped,
        treeParts = treeParts
    )
{
    override fun retrieveCoveringType(part: TreePart): String
    {
         return part.type.dropCoveringType
    }
}

/**
 * Animated transition used to drop fruit on tree to the ground when they are ripe
 */
class FruitDropTransition(
        simulation: Simulation,
        droppedFruit: MutableList<Covering>,
        fruitOnTrees: MutableList<Covering>,
        treeParts: Collection<TreePart>
):  CoveringDropTransition(
        simulation,
        droppedFruit,
        LeafState.Normal,
        true,
        fruitOnTrees,
        treeParts
    )
{
    override fun retrieveCoveringType(part: TreePart): String
    {
        return part.tree.type.dropFruitCoveringType
    }
}

/**
 * Animated transition used to drop bloom flowers on tree to the ground when they are dead
 */
class FlowerDropTransition(
        simulation: Simulation,
        droppedFlowers: MutableList<Covering>,
        flowersOnTrees: MutableList<Covering>,
        treeParts: Collection<TreePart>
):  CoveringDropTransition(
        simulation,
        droppedFlowers,
        LeafState.Normal,
        true,
        flowersOnTrees,
        treeParts
)
{
    override fun retrieveCoveringType(part: TreePart): String
    {
        return part.tree.type.dropFlowerCoveringType
    }
}