package com.phoenixpen.android.game.data


/**
 * An actual tree instance. It contains all the structures that make up the tree.
 *
 * TODO put logic in here? Updateable
 */
class Tree(type: TreeType)
{
    /**
     * All structures that make up this tree
     */
    val structures = ArrayList<TreePartStructure>()
}