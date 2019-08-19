package com.phoenixpen.game.simulation

import com.phoenixpen.game.data.Covering

/**
 * An animated transition used to cleanup any kind of covering. For example, it might be used to
 * remove dropped tree leaves in a visually pleasing way.
 *
 * @property coverings Mutable collection reference containing all coverings that are eventually to be
 *                     removed. This animation will directly modify this collection in order to
 *                     implement the desired effect.
 * @property shuffle Whether to shuffle the given [coverings] collection prior to the start of the
 *                   animation.
 */
class CoveringCleanupTransition(
        private val coverings: MutableList<Covering>,
        shuffle: Boolean = true
):  AnimatedTransition<Covering>(
        objectCollection = coverings
    )
{
    /**
     * Initialize state
     */
    init
    {
        // Randomize covering list if requested
        if(shuffle)
        {
            this.coverings.shuffle()
        }
    }

    /**
     * Process all coverings slated for modification in this frame.
     *
     * @param affectedObjects Objects affected in this frame
     */
    override fun processObjects(affectedObjects: Collection<Covering>)
    {
        // The trick here is that the given [coverings] list is the exact same
        // as the coverings given in [objects]. This way we can just remove as many
        // elements from [coverings] as we get reported here.
        // We basically only use the functionality of [AnimatedTransition] in order to
        // get the amount of objects to remove in this frame.
        for(i in 1 .. affectedObjects.size)
            this.coverings.removeAt(0)
    }
}