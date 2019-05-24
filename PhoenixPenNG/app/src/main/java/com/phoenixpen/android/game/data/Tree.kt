package com.phoenixpen.android.game.data


/**
 * An actual tree instance. It contains all the structures that make up the tree.
 *
 * @property type The type class associated with this instance
 * TODO put logic in here? Updateable
 */
class Tree(val type: TreeType)
{
    /**
     * All structures that make up this tree
     */
    val structures = ArrayList<TreePartStructure>()
}