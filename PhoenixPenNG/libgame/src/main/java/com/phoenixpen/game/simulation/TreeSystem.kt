package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.*
import com.phoenixpen.game.data.*
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.MapCellState
import com.sun.xml.internal.bind.util.Which.which
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class managing all the trees in the game world
 *
 * @property simulation Game simulation instance
 */
class TreeSystem(simulation: Simulation): System(simulation), StructureHolder, CoveringHolder
{
    /**
     * Enumeration describing the different states the tree simulation can be in
     */
    enum class TreeSystemState
    {
        /**
         * It is spring
         */
        Spring,

        /**
         * It is summer
         */
        Summer,

        /**
         * Leaves are changing in a normal way. This state is used between each season, except
         * between autumn and winter.
         */
        ChangingLeaves,

        /**
         * It is autumn, but the trees are not yet dropping their leaves
         */
        Autumn,

        /**
         * Trees are in the progress of dropping their leaves
         */
        AutumnDroppingLeaves,

        /**
         * All leaves have been dropped and are present as a covering on the ground.
         */
        AutumnDroppedLeaves,

        /**
         * The system is removing all dropped leaves
         */
        RemovingDroppedLeaves,

        /**
         * When entering winter, all dropped leaves are removed
         */
        Winter
    }

    /**
     * The current season
     */
    private var currentSeason = Season.Spring

    /**
     * The current state this simulation system is in
     */
    private var currentState: TreeSystemState = TreeSystemState.Spring

    /**
     * Collection of all trees currently present in the game world
     */
    private val trees = ArrayList<Tree>()

    /**
     * Collection of all leaf coverings
     */
    private val coverings = LinkedList<Covering>()

    /**
     * Class that manages all the tree part type classes
     */
    private val treePartManager = TreePartManager()

    /**
     * Manager for tree structure types
     */
    private val treeStructureManager = TreeStructureManager()

    /**
     * Manager for all tree type classes
     */
    private val treeTypeManager = TreeTypeManager()

    /**
     * A collection of all structures that are part of a tree and are leaves
     */
    private val leafStructures = ArrayList<TreePart>()

    /**
     * A collection containing all the leaf parts that need to still drop their leaves
     */
    private val leavesToModify = LinkedList<TreePart>()

    /**
     * Animation used to make changing leaves more appealing
     */
    private val modificationAnim = ModificationAnimation(0.05, 1)

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

                        // Create tree part
                        val part = TreePart.create(treePartType, pos)

                        // Save in tree object
                        tree.structures.add(part)

                        // If it is leaves, store it in the appropiate collection for later use
                        if(treePartType.isLeaves)
                            this.leafStructures.add(part)
                    }
                }
            }
        }

        // Tree is generated
        this.trees.add(tree)
    }

    /**
     * Retrieve all coverings managed by this system
     *
     * @return Collection of all covering instances managed by this system
     */
    override fun coverings(): Collection<Covering>
    {
        return this.coverings
    }

    /**
     * Sets the season property for all known leaf structures to the given season
     *
     * @param season The new season value to use
     */
    private fun setLeafSeason(season: Season)
    {
        for(part in this.leafStructures)
        {
            if(part.type.tileType.mode == TileTypeMode.Seasonal)
                part.tileInstance.setSeason(season)
        }
    }

    /**
     * Update the simulation state of the tree system based on given number of elapsed ticks
     *
     * @param elapsedTicks Ticks elapsed since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // Switch over current state
        when(this.currentState)
        {
            TreeSystemState.ChangingLeaves ->
            {
                // Check if we are done
                if(!this.modificationAnim.isActive)
                {
                    // Determine which state to switch to
                    this.currentState = when(this.currentSeason)
                    {
                        Season.Spring -> TreeSystemState.Spring
                        Season.Summer -> TreeSystemState.Summer
                        Season.Autumn -> TreeSystemState.Autumn
                        else -> throw IllegalStateException("ChangingLeaves state cant be used between autumn and winter")
                    }
                }
                else
                {
                    // Otherwise perform animation
                    val amount = this.modificationAnim.update(elapsedTicks)

                    val partsToModify = this.leavesToModify.take(amount)

                    for(part in partsToModify)
                    {
                        if(part.tileInstance.tileMode == TileTypeMode.Seasonal)
                            part.tileInstance.setSeason(this.currentSeason)
                    }

                    // Remove modified leaves
                    for(ix in 1 .. amount)
                        this.leavesToModify.removeFirst()
                }
            }
            TreeSystemState.Spring ->
            {
                // Check if its summer yet
                if(this.simulation.seasonSystem.currentSeason == Season.Summer)
                {
                    // Set current season to summer
                    this.currentSeason = Season.Summer

                    // Advance state to changing leaves
                    this.currentState = TreeSystemState.ChangingLeaves

                    // Prepare animation
                    this.leavesToModify.clear()
                    for(part in this.leafStructures)
                        this.leavesToModify.add(part)

                    this.leavesToModify.shuffle()

                    this.modificationAnim.reset(this.leavesToModify.size)
                }
            }
            TreeSystemState.Summer ->
            {
                // Check if its autumn yet
                if(this.simulation.seasonSystem.currentSeason == Season.Autumn)
                {
                    // Advance season to autumn
                    this.currentSeason = Season.Autumn

                    // Advance state to changing leaves
                    this.currentState = TreeSystemState.ChangingLeaves

                    // Prepare animation
                    this.leavesToModify.clear()
                    for(part in this.leafStructures)
                        this.leavesToModify.add(part)

                    this.leavesToModify.shuffle()

                    this.modificationAnim.reset(this.leavesToModify.size)
                }
            }
            TreeSystemState.Autumn ->
            {
                // We do nothing in autumn, until the given point in time is reached where we
                // are supposed to start dropping the leaves.
                if(this.simulation.seasonSystem.seasonProgress() >= this.simulation.seasonConfiguration.leafDropStart)
                {
                    // Switch to leaf dropping state
                    this.currentState = TreeSystemState.AutumnDroppingLeaves

                    // Collect all tree parts that need to drop leaves
                    this.leavesToModify.clear()
                    for(part in this.leafStructures)
                    {
                        if(part.type.dropsLeaves)
                        {
                            this.leavesToModify.add(part)
                        }
                    }

                    // Shuffle for nice random effect
                    this.leavesToModify.shuffle()

                    // Reset animation
                    this.modificationAnim.reset(this.leavesToModify.size)
                }
            }
            TreeSystemState.AutumnDroppingLeaves ->
            {
                // Check if we are done with dropping leaves
                if(!this.modificationAnim.isActive)
                {
                    // Switch to next state
                    this.currentState = TreeSystemState.AutumnDroppedLeaves
                }
                else
                {
                    // Retrieve amount of leave structures to drop
                    val amount = this.modificationAnim.update(elapsedTicks)

                    // Retrieve those structures
                    val parts = this.leavesToModify.take(amount)

                    // Drop the leaves
                    for(part in parts)
                        this.dropLeaves(part)

                    // Remove from collection
                    for(i in 1 .. amount)
                        this.leavesToModify.removeFirst()
                }
            }
            TreeSystemState.AutumnDroppedLeaves ->
            {
                // Wait for winter
                if(this.simulation.seasonSystem.currentSeason == Season.Winter)
                {
                    // Begin removing dropped leaves
                    this.currentState = TreeSystemState.RemovingDroppedLeaves

                    // Set up animation
                    this.coverings.shuffle()

                    this.modificationAnim.reset(this.coverings.size)

                    // Make sure all leaves are in winter mode
                    this.setLeafSeason(Season.Winter)
                }
            }
            TreeSystemState.RemovingDroppedLeaves ->
            {
                // Are we done?
                if(!this.modificationAnim.isActive)
                    this.currentState = TreeSystemState.Winter
                else
                {
                    val amount = this.modificationAnim.update(elapsedTicks)

                    for(ix in 1 .. amount)
                        this.coverings.removeFirst()
                }
            }
            TreeSystemState.Winter ->
            {
                // Wait for spring
                if(this.simulation.seasonSystem.currentSeason == Season.Spring)
                {
                    // Advance season to spring
                    this.currentSeason = Season.Spring

                    // Advance state to changing leaves
                    this.currentState = TreeSystemState.ChangingLeaves

                    // Prepare animation
                    this.leavesToModify.clear()
                    for(part in this.leafStructures)
                        this.leavesToModify.add(part)

                    this.leavesToModify.shuffle()

                    this.modificationAnim.reset(this.leavesToModify.size)
                }
            }
        }
    }

    /**
     * Try to drop the leaves for given tree part
     *
     * @param part Tree part to drop leaves for
     */
    private fun dropLeaves(part: TreePart)
    {
        // Retrieve covering type to use for dropped leaves
        val coveringTypeId = part.type.dropCoveringType

        // Check if it was given
        if(coveringTypeId.isEmpty())
        {
            GlobalLogger.e("TreeSystem", "Tree part type \"${part.type.basicData.identifier}\" is marked as dropping leaves but has no covering type assigned")
            return
        }

        // Otherwise retrieve the actual covering type
        val coveringType = this.simulation.coveringManager.lookupCovering(coveringTypeId)

        // Retrieve map instance
        val map = this.simulation.map

        // It could be that the given tree part is outside of map bounds. Check for that
        if(!map.isInBounds(part.position))
            return

        // Set season to winter which most often represent dropped leaves
        if(part.tileInstance.tileMode == TileTypeMode.Seasonal)
            part.tileInstance.setSeason(Season.Winter)

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