package com.phoenixpen.game.core

import com.phoenixpen.game.ascii.DrawInfo
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.util.concurrent.ThreadLocalRandom

/**
 * A commonly used type alias for a weighted list containing tile draw info instances.
 */
//typealias WeightedTileList = WeightedList<DrawInfo>

// TODO fix generic serialization! For now, own class.


/**
 * A pair of a value and its associated probability.
 *
 * @property glyph The glyph id
 * @property probability The values probability
 */
@Serializable
data class WeightedGlyphPair(val glyph: Int, val probability: Double)


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
class WeightedGlyphList(val values: List<WeightedGlyphPair>)
{
    /**
     * The total probability value. Needed to determine the required range of
     * the underlying random number generator.
     */
    @Transient
    protected val totalProbability: Double
            = this.values.stream().mapToDouble(WeightedGlyphPair::probability).sum()

    /**
     * Check input
     */
    init
    {
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
    fun drawElement(): Int
    {
        return this.drawEntry().second.glyph
    }

    /**
     * Selects a random element from the weighted list, based on the stored
     * probabilities, and returns its index.
     *
     * @return Index of randomly selected value of type T
     */
    fun drawIndex(): Int
    {
        return this.drawEntry().first
    }

    /**
     * Retrieve element based on its index. This index might be the result of an earlier [drawIndex]
     * call.
     *
     * @param idx Index of element to retrieve
     * @return Element at given index, if it exists.
     */
    fun elementAt(idx: Int): Int
    {
        if(idx >= this.values.size)
            throw IllegalArgumentException("WeightedList::elementAt: Index out of bounds")

        return this.values[idx].glyph
    }

    /**
     * Check if the weighted list is empty.
     *
     * @return Flag indicating whether the list is empty.
     */
    fun isEmpty(): Boolean
    {
        return this.values.isEmpty()
    }

    /**
     * Internal helper methods. Picks one of the weighted pair entries at random.
     *
     * @return Pair of the selected entry as well as its index.
     */
    private fun drawEntry(): Pair<Int, WeightedGlyphPair>
    {
        if(this.values.isEmpty())
            throw IllegalStateException("Can't draw element from empty weighted list")

        // Draw number
        val number = this.drawNumber()

        // Our running sum of probabilities
        var sum = 0.0

        // Iterate through all values until total probability of [number] is reached
        for((idx, p) in this.values.withIndex())
        {
            sum += p.probability

            if(number <= sum)
                return Pair(idx, p)
        }

        throw RuntimeException("WeightedList: Reached end of drawEntry. This should never happen")
    }
}



/**
 * Serializer implementation for [WeightedGlyphList]. We don't want a deep nesting normally caused by
 * the list property [values].
 */
class WeightedGlyphListSerializer : KSerializer<WeightedGlyphList>
{
    override val descriptor: SerialDescriptor
        get() = StringDescriptor.withName("WeightedGlyphList")

    @ImplicitReflectionSerializer
    override fun deserialize(decoder: Decoder): WeightedGlyphList
    {
        return WeightedGlyphList(decoder.decode(WeightedGlyphPair.serializer().list))
    }

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, obj: WeightedGlyphList)
    {
        encoder.encode(obj.values)
    }
}