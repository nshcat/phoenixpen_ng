import com.phoenixpen.game.ascii.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single entry in a water key, containing both the water tile type as well as an optional animation
 * offset
 *
 * @property animOffset Animation offset used when creating water tiles for this entry
 * @property type The water tile type for this entry
 */
@Serializable
data class WaterKeyEntry(
        @SerialName("animation_offset") val animOffset: Int = 0,
        val type: String
)

/**
 * A typealias used to implement the water key, which can be used to look up the colors in the
 * biome template layer images
 */
typealias WaterKey = Map<Color, WaterKeyEntry>