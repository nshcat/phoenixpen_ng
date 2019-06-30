package com.phoenixpen.game.simulation

import com.phoenixpen.game.core.Updateable

/**
 * An abstract base class for all simulation systems
 *
 * @property simulation The current simulation state
 */
abstract class System(val simulation: Simulation): Updateable
{
    /**
     * The current resource provider
     */
    protected val resources = this.simulation.resources
}