package com.phoenixpen.game.data.biome

import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.core.WeightedTypeList
import com.phoenixpen.game.core.WeightedTypeListSerializer
import com.phoenixpen.game.core.WeightedTypePair
import com.phoenixpen.game.graphics.Color
import com.phoenixpen.game.math.NormalDistribution
import com.phoenixpen.game.math.PoissonDiskSampler
import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumSerializer
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonInput
import kotlinx.serialization.json.JsonObject

/**
 * A single entry in a type key. It details how a certain collection of items might be spawned during
 * biome generation. This is mostly an internal class with ugly data layout.
 *
 * TODO use optional or either?
 */
@Serializable(with = TypeKeyEntrySerializer::class)
data class TypeKeyEntry(
        val mode: SpawnMode = SpawnMode.Place,
        val density: Double = 0.0,
        val minDistance: Double = 0.0,
        val minDistanceMod: NormalDistribution = NormalDistribution(probability = 0.0),
        val types: WeightedTypeList
)
{
    /**
     * Create poisson disk sampler based on attributes stored in this class
     *
     * @param dimensions Dimensions to use for the sampler
     * @return Poisson disk sampler instance
     */
    fun poissonSampler(dimensions: Dimensions): PoissonDiskSampler
    {
        if(this.mode != SpawnMode.Poisson)
            throw IllegalStateException("TypeKeyEntry not in poisson mode")

        return PoissonDiskSampler(dimensions, this.minDistance, minDistanceMod = this.minDistanceMod)
    }
}


/**
 * Serializer implementation for [TileType].
 *
 * Note that in case the weighted list "types" only contains one entry, the following syntax is allowed:
 *
 * {
 *    "type": "some_type"
 * }
 *
 * which will correctly initialize the weighted type list.
 */
@Serializer(forClass = TypeKeyEntry::class)
class TypeKeyEntrySerializer : KSerializer<TypeKeyEntry>
{
    override val descriptor: SerialDescriptor
        get() = StringDescriptor.withName("TypeKeyEntry")

    private fun parseTypes(input: JsonObject): WeightedTypeList
    {
        // Allow a single type to override weighted list
        if(input.containsKey("type"))
        {
            val type = input.getPrimitive("type").content

            return WeightedTypeList(listOf(WeightedTypePair(type, 1.0)))
        }
        else
        {
            // Otherwise just parse weighted type list
            val entry = input.getArray("types")

            return Json.parse(WeightedTypeListSerializer(), entry.toString())
        }
    }

    @ImplicitReflectionSerializer
    override fun deserialize(input: Decoder): TypeKeyEntry
    {
        // The JSON AST only supports unchecked casts, so we need to be prepared for any
        // type mismatch exceptions here
        try
        {
            // Retrieve JSON input
            val jsonInput = input as JsonInput

            // Decode JSON to AST and interpret as JSON object
            val root = jsonInput.decodeJson().jsonObject

            // Assume placing mode by default
            var mode = SpawnMode.Place

            // Retrieve mode entry
            if(root.containsKey("mode"))
            {
                // Parse mode entry
                val modeEntry = root.getPrimitive("mode")
                mode = Json.parse(EnumSerializer(SpawnMode::class), modeEntry.content)
            }

            // Retrieve type list
            val typeList = this.parseTypes(root)

            // Decide what to do depending on the mode value
            return when (mode)
            {
                SpawnMode.Density ->
                {
                    // Try to retrieve density
                    val densityEntry = root.getPrimitive("density")
                    val density =  densityEntry.double

                    TypeKeyEntry(mode, density = density, types = typeList)
                }

                SpawnMode.Poisson ->
                {
                    val minDist = root.getPrimitive("min_distance").double

                    var minDistMod = NormalDistribution(probability = 0.0)

                    // Supplying a minimum distance modifier is optional
                    if(root.containsKey("min_distance_modifier"))
                    {
                        val minDistModObj = root.getObject("min_distance_modifier")

                        // Try to deserialize normal distribution
                        minDistMod = Json.parse(NormalDistribution.serializer(), minDistModObj.toString())
                    }

                    TypeKeyEntry(mode, minDistance = minDist, minDistanceMod = minDistMod, types = typeList)
                }

                else -> TypeKeyEntry(types = typeList)
            }
        }
        catch(ex: Exception)
        {
            throw IllegalStateException("Failed to deserialize TypeKeyEntry: ${ex.message}")
        }
    }

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, obj: TypeKeyEntry)
    {
        throw NotImplementedError("Serialization for TypeKeyEntry is not supported")
    }
}




/**
 * A typealias used to implement the type key, which can be used to look up the colors in the
 * biome template layer images
 */
typealias TypeKey = Map<Color, TypeKeyEntry>