package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.data.TreePart
import com.phoenixpen.game.data.TreePartType
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.MapCellState

/**
 * An animated tree transition used to implement dropping coverings
 *
 * @property simulation The current simulation instance
 * @property coverings The current tree system coverings collection
 * @property newLeafState The new leaf state to apply to the leaves after they have been affected by this transition
 * @property coveringType Callable that extracts the covering type to use to visualize the dropped things
 * @param objects All objects affected by this transition
 * @property removeCoverings Whether to remove coverings on modified leaves. This is used to remove fruit and flower coverings when dropping them.
 * @property coveringsToRemove All coverings that might be on the leaves and be removed in this transition, like a collection of all fruit on trees
 */
class CoveringDropTransition(
        val simulation: Simulation,
        val coverings: MutableList<Covering>,
        val newLeafState: LeafState,
        val coveringType: (TreePart) -> String,
        val removeCoverings: Boolean,
        val coveringsToRemove: MutableList<Covering>,
        objects: Collection<TreePart>):

        AnimatedTreeTransition<TreePart>(
            objectCollection = objects
        )
{
    /**
     * Process all leaves slated for modification in this frame.
     *
     * @param affectedObjects Objects affected in this frame
     */
    override fun processObjects(affectedObjects: Collection<TreePart>)
    {
        // Try to drop leaves for each affected leaf part
        for(part in affectedObjects)
        {
            this.drop(part)
        }
    }

    /**
     * Try to drop the covering for given tree part
     *
     * @param part Tree part to drop covering for
     */
    private fun drop(part: TreePart)
    {
        // Are we asked to remove any old coverings for this part?
        if(this.removeCoverings)
        {
            // Remove all coverings in given list on this position
            this.coveringsToRemove.removeIf { x -> x.position == part.position }
        }

        // Retrieve covering type to use for dropped leaves
        val coveringTypeId = this.coveringType(part)

        // Check if it was given
        if(coveringTypeId.isEmpty())
        {
            GlobalLogger.e("CoveringDropTransition", "Tree part type \"${part.type.basicData.identifier}\" has no covering type assigned for this drop transition")
            return
        }

        // Otherwise retrieve the actual covering type
        val coveringType = this.simulation.coveringManager.lookupCovering(coveringTypeId)

        // Retrieve map instance
        val map = this.simulation.map

        // It could be that the given tree part is outside of map bounds. Check for that
        if(!map.isInBounds(part.position))
            return

        // Set leaf state to the requested new state
        part.leafState = this.newLeafState

        // "Raycast" downwards to find ground
        for(iy in part.position.y downTo 0)
        {
            // Calculate position
            val position = Position3D(part.position.x, iy, part.position.z)

            // Retrieve map cell
            val mapCell = map.cellAt(position)

            // Check if its ground
            if(mapCell.state == MapCellState.Ground)
            {
                // Is there a structure on the ground?
                if(map.getStructureAtExact(position, true).isPresent)
                    break

                // Spawn covering
                this.coverings.add(Covering.create(coveringType, position))

                break
            }
        }
    }
}