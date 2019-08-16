package com.phoenixpen.game.simulation

import com.phoenixpen.game.data.Covering

/**
 * An animated tree transition used to cleanup any kind of covering. This for example
 * used to remove dropped leaves after a while.
 */
class CoveringCleanupTransition(
        val coverings: MutableList<Covering>):

        AnimatedTreeTransition<Covering>(
                objectCollection = coverings
        )
{
    /**
     * Initialize state
     */
    init
    {
        // Randomize covering list
        this.coverings.shuffle()
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
        // We basically only use the functionality of [AnimatedTreeTransition] in order to
        // get the amount of objects to modify in this frame.
        for(i in 1 .. affectedObjects.size)
            this.coverings.removeAt(0)
    }
}