package com.phoenixpen.game.data

import com.phoenixpen.game.graphics.DrawInfo
import com.phoenixpen.game.ascii.Position3D

/**
 * A class representing a base class for all structure types.
 * A structure is basically a static entity that is unable to move.
 *
 * @property baseType Basic structure type information, like interaction with the pathing system.
 * @property position The position of this structure in the game world. This is fixed.
 */
abstract class Structure(val baseType: StructureType, val position: Position3D)
{
    /**
     * Retrieve information on how to draw this structure. This is implemented in structure
     * sub classes, for example a plant will change its appearance over the seasons.
     *
     * @param fancyMode Whether we currently are in fancy graphics mode.
     * @return [DrawInfo] describing how to draw this structure.
     */
    abstract fun tile(fancyMode: Boolean = true): DrawInfo

    /**
     * Check whether this tile should currently be drawn. This is dynamic, since this could depend on
     * seasonal tiles.
     *
     * @return Flag indicating whether this structure should currently be rendered
     */
    abstract fun shouldDraw(): Boolean
}