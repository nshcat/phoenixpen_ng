package com.phoenixpen.android.utility

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

/**
 * A pair of a value and its associated probability.
 *
 * @property value The value
 * @property probability The values probability
 */
@Serializable
data class WeightedPair<T>(val value: T, val probability: Double)


/**
 * A list containing values that are weighted with probabilities p_i.
 * In contrast to other implementations, the probabilities do not need
 * to sum up to 1, rather any arbitrary scale is allowed and will work as
 * expected. Example: Values that sum up to 100 behave exactly like values
 * that sum up to 1, given the same relative distribution of probability.
 *
 * @property values All values in the list. The probabilities are not allowed to be 0 or negative.
 */
@Serializable
class WeightedList<T>(val values: List<WeightedPair<T>>)
{
    /**
     * The total probability value. Needed to determine the required range of
     * the underlying random number generator.
     */
    @Transient
    protected val totalProbability: Double
        = this.values.stream().mapToDouble(WeightedPair<T>::probability).sum()

    /**
     * Check input
     */
    init
    {
        // We need at least one value
        //if(this.values.isEmpty())
        //    throw IllegalArgumentException("WeightedList: Value collection must not be empty")

        // Negative or zero probabilities are not allowed
        if(this.values.stream().anyMatch{ x -> x.probability <= 0.0 })
            throw IllegalArgumentException("WeightedList: Value probabilities must not be negative or zero")

    }

    /**
     * Draw random number in range useable to select a number from the list
     *
     * @return Random floating point number in range [0, [totalProbability]]
     */
    protected fun drawNumber(): Double
    {
        return ThreadLocalRandom.current().nextDouble(this.totalProbability)
    }

    /**
     * Selects a random element from the weighted list, based on the stored
     * probabilities
     *
     * @return Randomly selected value of type T
     */
    fun drawElement(): T
    {
        if(this.values.isEmpty())
            throw IllegalStateException("Can't draw element from empty weighted list")

        // Draw number
        val number = this.drawNumber()

        // Our running sum of probabilities
        var sum = 0.0

        // Iterate through all values until total probability of [number] is reached
        for(p in this.values)
        {
            sum += p.probability

            if(number <= sum)
                return p.value
        }

        throw RuntimeException("WeightedList: Reached end of drawElement. This should never happen")
    }
}


/*/**
 * Serializer implementation for [WeightedList]. We don't want a deep nesting normally caused by
 * the list property [values].
 */
@Serializer(forClass=WeightedList::class)
class WeightedListSerializer<T> : KSerializer<WeightedList<T>>
{
    override val descriptor: SerialDescriptor
        get() = StringDescriptor.withName("WeightedList")

    @ImplicitReflectionSerializer
    override fun deserialize(decoder: Decoder): WeightedList<T>
    {
        return WeightedList(decoder.decode<List<WeightedPair<T>>>())
    }

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, obj: WeightedList<T>)
    {
        encoder.encode(obj.values)
    }
}*/

