package com.phoenixpen.game.simulation

import com.phoenixpen.game.ascii.*
import com.phoenixpen.game.data.*
import com.phoenixpen.game.logging.GlobalLogger
import com.phoenixpen.game.map.MapCellState
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
         * Just normal, green leaves. Ever-green trees will mostly stay in this state.
         * This state can also lead to different states depending on the season:
         *  - If it is autumn, leaves can brown
         *  - If it is spring, the flowers of the trees can begin to bloom
         *  - If in summer, fruit will start to grow
         *  - If in summer, dropped flowers will disappear
         */
        // TODO:
        // - clean up dropped flowers if in summer (if list of dropped flowers is not empty)
        Leaves,

        /**
         * Trees are in bloom. Trees that do not support blooming will just stay green.
         */
        InBloom,

        /**
         * Tree is bearing fruit
         */
        BearingFruit,

        /**
         * Leaves are autumnal colours. This state also has to clean up dropped fruit after a while.
         * It also has to drop the leaves after a while.
         */
        AutumnalLeaves,

        /**
         * Leaves have been dropped. Has to clean up dropped leaves a short while after winter
         * started.
         */
        Barren
    }

    /**
     * The current state this simulation system is in
     */
    private var currentState: TreeSystemState = TreeSystemState.Leaves

    /**
     * Collection of all trees currently present in the game world
     */
    private val trees = ArrayList<Tree>()

    /**
     * Collection of all leaf coverings
     */
    private val coverings = LinkedList<Covering>()

    /**
     * Collection of all dropped fruit coverings
     */
    private val droppedFruit = LinkedList<Covering>()

    /**
     * Collection of all dropped flower coverings
     */
    private val droppedFlowers = LinkedList<Covering>()

    /**
     * Collection of all flower coverings on trees
     */
    private val flowersOnTrees = LinkedList<Covering>()

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
     * Transition used to implement tree changes
     */
    private var transition = Optional.empty<TreeTransition>()

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
                        val part = TreePart.create(treePartType, pos, tree)

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
        // Array list to store all the coverings
        val result = ArrayList<Covering>()

        result.addAll(this.coverings)
        result.addAll(this.droppedFruit)
        result.addAll(this.droppedFlowers)
        result.addAll(this.flowersOnTrees)

        return result
    }

    /**
     * Update the simulation state of the tree system based on given number of elapsed ticks
     *
     * @param elapsedTicks Ticks elapsed since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // Update all tree parts
        for(tree in this.trees)
            for(part in tree.structures)
                part.update(elapsedTicks)

        // If a transition is currently active, we _only_ do that. This allows transitions to
        // define the "next" state right when they start, since we ignore any active state when
        // a transition is currently active.
        if(this.transition.isPresent)
        {
            // Retrieve transition instance
            val currentTransition = this.transition.get()

            // If the transition is finished, we then just go on reacting to the current state,
            // since the "new" state was already set when the transition was activated.
            if(!currentTransition.isDone())
            {
                // Update transition
                currentTransition.update(elapsedTicks)

                // Do _not_ handle current state further
                return
            }
            else
            {
                // If it is done, replace optional instance with empty value
                this.transition = Optional.empty()

                // We do _not_ return here, since we now want to react to the current state
            }
        }

        // Switch over current state
        when(this.currentState)
        {
            // In the [InBloom] state we only have to check for the end of the spring in order to
            // drop the flowers.
            TreeSystemState.InBloom ->
            {
                // If the tree is currently in bloom, we have to wait until the summer to drop the
                // flowers
                if(this.simulation.seasonSystem.currentSeason == Season.Summer)
                {
                    this.transition = Optional.of(
                            CoveringDropTransition(
                                    this.simulation, this.droppedFlowers, LeafState.Normal,
                                    { x -> x.tree.type.dropFlowerCoveringType },
                                    true,
                                    this.flowersOnTrees,
                                    this.leafStructures.filter { x -> x.tree.type.doesBloom }
                            )
                    )

                    // New state is normal leaves
                    this.currentState = TreeSystemState.Leaves
                }
            }

            // In autumn we have to drop leaves and clean-up dropped fruit
            TreeSystemState.AutumnalLeaves ->
            {
                // Do we still have to clean up dropped fruit and have actually reached the start
                // time for that?
                if(this.droppedFruit.isNotEmpty()
                    && this.simulation.seasonSystem.seasonProgress() >= this.simulation.seasonConfiguration.fruitCleanupStart)
                {
                    // Create animation to clean up coverings
                    this.transition = Optional.of(CoveringCleanupTransition(this.droppedFruit))
                }

                // Are we supposed to drop leaves?
                else if(this.simulation.seasonSystem.seasonProgress() >= this.simulation.seasonConfiguration.leafDropStart)
                {
                    // Create leaf drop animation
                    this.transition = Optional.of(
                            CoveringDropTransition(
                                    this.simulation, this.coverings, LeafState.Dropped,
                                    { x -> x.type.dropCoveringType },
                                    false,
                                    listOf<Covering>().toMutableList(),
                                    this.leafStructures.filter { x -> x.dropsLeaves() }
                            )
                    )

                    // Switch to barren state
                    this.currentState = TreeSystemState.Barren
                }
            }

            // When leaves are dropped, we have to both clean up the dropped leaves and wait for the
            // end of winter in order to regrow the leaves.
            TreeSystemState.Barren ->
            {
                // When its winter and there are still dropped leaves and the season has changed to winter,
                // we need to clean them up
                if(this.coverings.isNotEmpty() &&
                    this.simulation.seasonSystem.currentSeason == Season.Winter)
                {
                    // Create animation to clean up the leaf coverings
                    this.transition = Optional.of(CoveringCleanupTransition(this.coverings))
                }

                // If we have reached spring we need to regrow all leaves
                else if(this.simulation.seasonSystem.currentSeason == Season.Spring)
                {
                    // Create a transition that changes the state of all leaf structures to normal
                    this.transition = Optional.of(LeafStateTransition(LeafState.Normal, this.leafStructures))

                    // Switch to new state
                    this.currentState = TreeSystemState.Leaves
                }

            }
        }
    }


}