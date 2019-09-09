package com.phoenixpen.game.core

import kotlinx.serialization.Serializable
import java.util.concurrent.ThreadLocalRandom

/**
 * A class implementing a normal distribution with a user-selectable mean and variance.
 * Additionally, the user can set an activation probability, which is used to check whether
 * the distribution should produce a value or not, and an interval used to restrict the generated
 * values.
 *
 * These features can be used to, for example, implementing rain occurrence by first checking
 * whether rain should start, and if so, generating a rain duration based on the underlying
 * normal distribution.
 *
 * @property mean The mean of the normal distribution
 * @property variance The variance of the normal distribution
 * @property probability The probability used by user code to check whether the distribution should be evaluated
 * @property restricted Whether the generated values are restricted to given interval
 * @property min Restricted interval begin, inclusive
 * @property max Restricted interval end, inclusive
 */
@Serializable
class NormalDistribution(
        val mean: Double = 0.0,
        val variance: Double = 1.0,
        val probability: Double = 1.0,
        val restricted: Boolean = false,
        val min: Double = 0.0,
        val max: Double = 100.0
)
{
    /**
     * Check for valid input
     */
    init
    {
        if(probability < 0.0 || probability > 1.0)
            throw IllegalArgumentException("Probability has to be in interval [0, 1]")

        if(min >= max)
            throw IllegalArgumentException("Min/Max interval invalid")
    }

    /**
     * Do a check based on the stored [probability] which can be used by user code to determine
     * whether the normal distribution should be evaluated. If the property [probability] is not set
     * by the user, this will always evaluate to true.
     *
     * @return Flag indicating whether the check succeeded.
     */
    fun shouldGenerate(): Boolean
    {
        // If the probability is 100% anyways, don't bother
        if(this.probability >= 1.0)
            return true
        else
        {
            // Pick random number between 0 and 1
            val value = ThreadLocalRandom.current().nextDouble(1.0)

            // Perform actual check
            return value <= this.probability
        }
    }

    /**
     * Check whether the normal distribution described by this instance is the standard normal
     * distribution, which means mean being equal to 0 and variance being equal to 1.
     *
     * @return Flag indicating whether the distribution is the standard normal distribution
     */
    fun isStandard(): Boolean
    {
        return this.mean == 0.0 && this.variance == 1.0
    }

    /**
     * Generate a new value based on the normal distribution described by this instance.
     *
     * @return Next value of the normal distribution.
     */
    fun nextValue(): Double
    {
        // Retrieve value of a standard normal distribution
        val standardVal = ThreadLocalRandom.current().nextGaussian()

        // If this distribution is the standard normal distribution, we are done here
        if(this.isStandard())
            return this.restrict(standardVal)

        // Otherwise, process value further to implement arbitrary normal distributions
        return this.restrict((standardVal*this.variance) + this.mean)
    }

    /**
     * Restricts given value to the interval defined by [min] and [max], if [restricted] is set to true,
     * otherwise does nothing.
     *
     * @param input Value to potentially restrict to the interval
     * @return If [restricted] is true, [input] restricted to [min] and [max], [input] otherwise
     */
    private fun restrict(input: Double): Double = when(this.restricted)
    {
            true -> input.coerceIn(this.min, this.max)
            false -> input
    }
}