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

/**
 * Animated transition used to spawn flowers on trees that are in bloom
 */
class SpawnFlowersTransition(
        simulation: Simulation,
        flowersOnTrees: MutableList<Covering>,
        treeParts: Collection<TreePart>
):  CoveringSpawnTransition(
       simulation,
        flowersOnTrees,
        treeParts
    )
{
    override fun retrieveCoveringType(part: TreePart): String
    {
        return part.tree.type.flowerCoveringType
    }
}

/**
 * Animated transition used to spawn fruit on trees
 */
class SpawnFruitTransition(
        simulation: Simulation,
        fruitOnTrees: MutableList<Covering>,
        treeParts: Collection<TreePart>
):  CoveringSpawnTransition(
        simulation,
        fruitOnTrees,
        treeParts
)
{
    override fun retrieveCoveringType(part: TreePart): String
    {
        return part.tree.type.fruitCoveringType
    }
}