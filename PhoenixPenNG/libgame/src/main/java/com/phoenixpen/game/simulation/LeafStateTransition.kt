package com.phoenixpen.game.simulation

import com.phoenixpen.game.data.TreePart

/**
 * An animated transition implementing the changing of leaf state
 *
 * @property newState The new leaf state to transition to
 * @property objects All affected leaf structures
 */
class LeafStateTransition(val newState: LeafState, objects: Collection<TreePart>):
    AnimatedTransition<TreePart>(
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
        // Change leaf state for all affected leaves
        for(leaf in affectedObjects)
            leaf.leafState = this.newState
    }
}