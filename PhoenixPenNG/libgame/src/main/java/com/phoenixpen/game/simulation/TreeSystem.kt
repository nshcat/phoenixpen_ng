package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.*
import com.phoenixpen.game.data.*
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.resources.ResourceProvider

/**
 * Class managing all the trees in the game world
 *
 * @property resources The resource provider as a source for game data
 */
class TreeSystem(val resources: ResourceProvider): StructureHolder
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
        this.treePartManager.loadTreeParts(this.resources, "tree_parts.json")

        // Load tree structures
        this.treeStructureManager.loadTreeStructures(this.resources, "tree_structures.json")

        // Load tree types
        this.treeTypeManager.loadTrees(this.resources, "tree_types.json")
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
     * Generate a tree of given type at given position. This will center the tree structure at the
     * given position, using the structures trunk position information
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

        // Retrieve trunk position
        val trunkPos = structureType.trunkPosition

        // Calculate real top left position where we have to start inserting the structure
        val topLeft = position - Position3D(trunkPos.x, 0, trunkPos.y)

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
                        val pos = Position3D(topLeft.x + ix, topLeft.y + dy, topLeft.z + iz)

                        // Save in tree object
                        tree.structures.add(TreePart.create(treePartType, pos))
                    }
                }
            }
        }

        // Tree is generated
        this.trees.add(tree)
    }
}