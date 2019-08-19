package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.Position3D
import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.data.TreePart
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.MapCellState
import javax.print.attribute.standard.Destination

/**
 * An animated transition used to implement dropping of various coverings from tree parts.
 * This is, for example, used to implement the shedding of leaves in autumn.
 * In addition, it supports the removal of existing coverings as part of the animation. This can be
 * used to "move" coverings, i.e. fruit that stains a tree red to fruit that is on the ground. This
 * works by removing any coverings in a given collection that have the same position as the tree part
 * currently being modified.
 *
 * @property simulation The current simulation instance. This is needed in order to access the map.
 * @property coverings The current tree system coverings collection. All created coverings will be stored in here.
 * @property newLeafState The new leaf state to apply to the leaves after they have been affected by this transition
 * @param treeParts All tree parts affected by this transition
 * @property removeCoverings Whether to remove coverings on modified tree parts.
 * @property coveringsToRemove All coverings that might be on the tree parts and subsequently removed as part of this transition.
 */
abstract class CoveringDropTransition(
        val simulation: Simulation,
        val coverings: MutableList<Covering>,
        val newLeafState: LeafState,
        val removeCoverings: Boolean = false,
        val coveringsToRemove: MutableList<Covering> = listOf<Covering>().toMutableList(),
        treeParts: Collection<TreePart>
):  AnimatedTransition<TreePart>(
        objectCollection = treeParts
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
     * Determine which type of covering to spawn for given affected tree part.
     * This method is supposed to be overridden by subclasses in order to implement their
     * desired behaviour.
     *
     * @param part Affected tree part
     * @return Covering type identifier extracted from given tree part
     */
    protected abstract fun retrieveCoveringType(part: TreePart): String

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
        val coveringTypeId = this.retrieveCoveringType(part)

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