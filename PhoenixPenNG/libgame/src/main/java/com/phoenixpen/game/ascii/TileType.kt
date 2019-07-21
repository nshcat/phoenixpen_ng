package com.phoenixpen.game.ascii

import com.phoenixpen.game.core.*
import com.phoenixpen.game.simulation.Season
import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumSerializer
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonInput

/**
 * Enumeration describing the various ways the graphical representation of a game object can be specified.
 */
enum class TileTypeMode
{
    /**
     * A single, fixed tile
     */
    Static,

    /**
     * A selection of tiles combined with probabilities.
     */
    Varied,

    /**
     * Similar to varied mode, but color info and glyph are stored and chosen independently
     * from each other.
     */
    VariedSeparate,

    /**
     * A selection of tiles meant to be played in sequential order, repeating.
     */
    Animated,

    /**
     * Tile is not drawn. Causes the game object to be completely transparent
     */
    NoDraw
}

/**
 * An opaque handle class used to store information about the instantiation of a tile type as part of
 * a game object. This class, for example, stores the index into the weighted tile list, if varied tiles
 * are activated.
 *
 * @property tileMode The mode this tile is in
 * seasonal changes independently from each other
 * @property tileIndex Index used to store which varied entry was selected for this tile
 * @property animIdx Index used to store the animation state of an animated tile
 * @property colorIndex Index of the active color info. Used in VariedSeparate mode.
 * @property glyphIndex Index of the active glyph. Used in VariedSeparate mode.
 */
data class TileInstance(
        val tileMode: TileTypeMode = TileTypeMode.Static,
        val tileIndex: Int = -1,
        val colorIndex: Int = -1,
        val glyphIndex: Int = -1,
        val animIdx: TickCounter = TickCounter(0)
        ): Updateable
{
    /**
     * Update animation state, if used
     *
     * @param elapsedTicks Number of ticks elapsed since last update
     */
    override fun update(elapsedTicks: Int)
    {
        if(this.tileMode == TileTypeMode.Animated && this.animIdx.isNotEmpty())
        {
            this.animIdx.update(elapsedTicks)
        }
    }
}

/**
 * A class encapsulating the different ways a graphical representation of a game object can be specified.
 * It supports static, varied and animated tiles. It is supposed to be stored inside type class instances.
 * It can create instances of type [TileInstance] which acts like an opaque handle to be saved in object
 * instances in order to save e.g. the chosen varied tile.
 *
 * @property mode The current tile type mode. Per default, this is set to static.
 * @property staticTile The static tile info, if used
 * @property variedTiles The varied tile info, if used
 * @property animatedTile The tile animation, if used
 * @property variedGlyphs Varied glyphs, used in VariedSeparate mode
 * @property variedColors Varied color info, used in VariedSeparate mode
 */
@Serializable
class TileType(
        val mode: TileTypeMode = TileTypeMode.Static,
        val staticTile: DrawInfo = DrawInfo(),
        val variedTiles: WeightedTileList = WeightedTileList(listOf()),
        val variedGlyphs: WeightedGlyphList = WeightedGlyphList(listOf()),
        val variedColors: WeightedColorList = WeightedColorList(listOf()),
        val animatedTile: Animation = Animation.empty()
)
{

    /**
     * Create a new tile instance based on this tile type. In the case of varied tiles,
     * this will pick a graphical representation.
     *
     * @param animOffset Animation offset. Will only have an effect if tile actually uses animation.
     *
     * @return New tile instance based on this tile type.
     */
    fun createInstance(animOffset: Int = 0): TileInstance
    {
        return when(this.mode)
        {
            TileTypeMode.NoDraw, TileTypeMode.Static  -> TileInstance(this.mode)
            TileTypeMode.Varied -> TileInstance(this.mode, this.variedTiles.drawIndex())
            TileTypeMode.Animated -> TileInstance(this.mode, animIdx = TickCounter(period = this.animatedTile.speed, initial = animOffset))
            TileTypeMode.VariedSeparate -> TileInstance(this.mode, colorIndex = this.variedColors.drawIndex(), glyphIndex = this.variedGlyphs.drawIndex())
        }
    }

    /**
     * Retrieve graphical representation based on this tile type and the given tile instance.
     *
     * @param instance Instance used to retrieve tile draw info
     * @return Draw information describing how to draw this tile type
     */
    fun tile(instance: TileInstance): DrawInfo
    {
        return when(this.mode)
        {
            TileTypeMode.NoDraw -> throw IllegalStateException("Can't retrieve tile for NoDraw object!")
            TileTypeMode.Static -> this.staticTile
            TileTypeMode.Varied -> this.variedTiles.elementAt(instance.tileIndex)
            TileTypeMode.Animated -> this.animatedTile.frameAtRaw(instance.animIdx.totalPeriods)
            TileTypeMode.VariedSeparate -> {
                val colorInfo = this.variedColors.elementAt(instance.colorIndex)

                DrawInfo(this.variedGlyphs.elementAt(instance.glyphIndex), colorInfo.foreground, colorInfo.background)
            }
        }
    }

    /**
     * Check if we should draw this tile
     *
     * @param instance Instance containing tile state
     * @return Flag indicating whether this tile should be drawn
     */
    fun shouldDraw(instance: TileInstance): Boolean
    {
        return instance.tileMode != TileTypeMode.NoDraw
    }
}


/**
 * Serializer implementation for [TileType].
 */
@Serializer(forClass=TileType::class)
class TileTypeSerializer : KSerializer<TileType>
{
    override val descriptor: SerialDescriptor
        get() = StringDescriptor.withName("TileType")

    @ImplicitReflectionSerializer
    override fun deserialize(input: Decoder): TileType
    {
        // The JSON AST only supports unchecked casts, so we need to be prepared for any
        // type mismatch exceptions here
        try {
            // Retrieve JSON input
            val jsonInput = input as JsonInput

            // Decode JSON to AST and interpret as JSON object
            val root = jsonInput.decodeJson().jsonObject

            // Assume static glyph mode per default
            var mode = TileTypeMode.Static

            // Retrieve mode entry
            if(root.containsKey("mode"))
            {
                // Parse mode entry
                val modeEntry = root.getPrimitive("mode")
                mode = Json.parse(EnumSerializer(TileTypeMode::class), modeEntry.content)
            }

            // Decide what to do depending on the mode value
            return when (mode)
            {
                TileTypeMode.VariedSeparate ->
                {
                    // Try to parse varied glyph entries
                    if(!root.containsKey("varied_glyphs"))
                        throw RuntimeException("missing \"varied_glyphs\" entry")

                    if(!root.containsKey("varied_colors"))
                        throw RuntimeException("missing \"varied_colors\" entry")

                    // Retrieve JSON entries for both the glyphs and the colors
                    val glyphsEntry = root.getArray("varied_glyphs")
                    val colorsEntry = root.getArray("varied_colors")

                    // Parse them
                    val glyphs = Json.nonstrict.parse(WeightedGlyphListSerializer(), glyphsEntry.toString())
                    val colors = Json.nonstrict.parse(WeightedColorListSerializer(), colorsEntry.toString())

                    TileType(mode, variedGlyphs = glyphs, variedColors = colors)
                }
                TileTypeMode.NoDraw ->
                {
                    // No further data is needed.
                    TileType(mode)
                }
                TileTypeMode.Static ->
                {
                    // Interpret this same entry as a DrawInfo. This removes one layer of nesting.
                    TileType(mode, staticTile = Json.nonstrict.parse(DrawInfo.serializer(), root.toString()))
                }
                TileTypeMode.Varied ->
                {
                    // The data entry definitely has to exist.
                    if(!root.containsKey("varied_tiles"))
                        throw RuntimeException("missing \"varied_tiles\" entry")

                    // Interpret data entry as array
                    val entry = root.getArray("varied_tiles")
                    TileType(mode, variedTiles = Json.parse(WeightedTileListSerializer(), entry.toString()))
                }
                TileTypeMode.Animated ->
                {
                    // Retrieve frames
                    val frameEntry = root.getArray("frames")
                    val frames = Json.parse(DrawInfo.serializer().list, frameEntry.toString())
                    val speed = root.getPrimitive("speed").int
                    TileType(mode, animatedTile = Animation(speed, frames))
                }
            }
        }
        catch(ex: Exception)
        {
            throw IllegalStateException("Failed to deserialize TileType: ${ex.message}")
        }
    }

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, obj: TileType)
    {
        throw NotImplementedError("Serialization for TileType is not supported")
    }
}