package com.phoenixpen.android.game.simulation

import android.content.Context
import com.phoenixpen.android.R
import com.phoenixpen.android.game.ascii.Position
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.data.*

/**
 * Class managing all the trees in the game world
 *
 * @property context The Android application context
 */
class TreeHolder(val context: Context): StructureHolder
{
    /**
     * Collection of all trees currently present in the game world
     */
    val trees = ArrayList<Tree>()

    /**
     * Class that manages all the tree part type classes
     */
    val treePartManager = TreePartManager()

    /**
     * Manager for tree structure types
     */
    val treeStructureManager = TreeStructureManager()

    /**
     * Manager for all tree type classes
     */
    val treeTypeManager = TreeTypeManager()

    /**
     * Initialize all the type class managers present in this system
     */
    init
    {
        // Load tree parts
        this.treePartManager.loadTreeParts(this.context, R.raw.tree_parts)

        // Load tree structures
        this.treeStructureManager.loadTreeStructures(this.context, R.raw.tree_structures)

        // Load tree types
        this.treeTypeManager.loadTrees(this.context, R.raw.tree_types)
    }

    /**
     * Retrieve all structures that are part of trees
     *
     * @return Collection of all structures managed by this class
     */
    override fun structures(): Collection<Structure>
    {
        // Array list to store all the structures
        val result = ArrayList<Structure>()

        // Accumulate all structures that are part of the trees
        for(tree in this.trees)
            result.addAll(tree.structures)

        return result
    }

    /**
     * Generate a tree of given type at given position
     *
     * @param position Position to generate tree at
     * @param type Type of tree to generate
     */
    fun generateTree(position: Position3D, type: String)
    {
        // Retrieve tree type class object
        val treeType = this.treeTypeManager.lookupTree(type)

        // Create tree object
        val tree = Tree(treeType)

        // Pick structure type
        val structure = treeType.structureTypes.random()

        // Retrieve the type class object
        val structureType = this.treeStructureManager.lookupTreeStructure(structure)

        // Generate each layer
        for((dy, layer) in structureType.structure.layers.withIndex())
        {
            // Retrieve dimensions
            val layerDimensions = layer.dimensions

            // Create all structures in this layer
            for(ix in 0 until layerDimensions.width)
            {
                for(iz in 0 until layerDimensions.height)
                {
                    // Retrieve placeholder type
                    val placeholder = layer.entryAt(Position(ix, iz))

                    // Only process if its not empty
                    if(placeholder != PlaceholderType.Empty)
                    {
                        // Look up actual definition
                        val partType = treeType.placeholderDefinition.definitionFor(placeholder)

                        // Retrieve tree part type class instance
                        val treePartType = this.treePartManager.lookupTreePart(partType)

                        // Generate actual structure
                        val pos = Position3D(position.x + ix, position.y + dy, position.z + iz)

                        // Save in tree object
                        tree.structures.add(TreePartStructure(treePartType, pos))
                    }
                }
            }
        }

        // Tree is generated
        this.trees.add(tree)
    }
}