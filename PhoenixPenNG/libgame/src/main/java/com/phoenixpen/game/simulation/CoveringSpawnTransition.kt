package com.phoenixpen.game.simulation

import com.phoenixpen.game.data.Covering
import com.phoenixpen.game.data.TreePart

/**
 * An animated transition that spawns coverings on tree parts.
 *
 * @property simulation The game simulation instance
 * @property coverings Destination collection for coverings that are created as part of this transition
 * @param treeParts All affected tree parts
 */
abstract class CoveringSpawnTransition(
        val simulation: Simulation,
        val coverings: MutableList<Covering>,
        treeParts: Collection<TreePart>
):  AnimatedTransition<TreePart>(
        objectCollection = treeParts
    )
{
    /**
     * Retrieve type of covering to spawn on given affected tree part.
     * Subclasses are expected to implement this method in order to select
     * which type of covering to spawn for each particular type of tree part
     * encountered.
     *
     * @param part Affected tree part
     * @return Type identifier of covering to spawn on tree part
     */
    protected abstract fun retrieveCoveringType(part: TreePart): String

    /**
     * Process all leaves slated for modification in this frame.
     *
     * @param affectedObjects Objects affected in this frame
     */
    override fun processObjects(affectedObjects: Collection<TreePart>)
    {
        // Create covering on each of the affected tree parts
        for(part in affectedObjects)
        {
            // Figure out which type of covering to spawn on this particular tree part
            val coveringTypeId = this.retrieveCoveringType(part)

            // Retrieve actual covering type
            val coveringType = simulation.coveringManager.lookupCovering(coveringTypeId)

            // Create covering at the exact same position as the current tree part
            val covering = Covering.create(coveringType, part.position)

            // Store in given destination collection
            this.coverings.add(covering)
        }
    }
}