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
     * A selection of tiles meant to be played in sequential order, repeating.
     */
    Animated,

    /**
     * Tile is not drawn. Causes the game object to be completely transparent
     */
    NoDraw,

    /**
     * A tile that has possibly different tiles for the four seasons
     */
    Seasonal
}

/**
 * An opaque handle class used to store information about the instantiation of a tile type as part of
 * a game object. This class, for example, stores the index into the weighted tile list, if varied tiles
 * are activated.
 *
 * @property tileMode The mode this tile is in
 * @property currentSeason The current season associated with this tile. This is done so that tiles can be subjected to
 * seasonal changes independently from each other
 * @property tileIndex Index used to store which varied entry was selected for this tile
 * @property animIdx Index used to store the animation state of an animated tile
 * @property seasonalInstances Tile instances of the various seasonal tiles, if needed
 */
data class TileInstance(
        val tileMode: TileTypeMode = TileTypeMode.Static,
        val tileIndex: Int = -1,
        val animIdx: TickCounter = TickCounter(0),
        var currentSeason: Season = Season.Spring,
        val seasonalInstances: List<TileInstance> = listOf()
        ): Updateable
{
    /**
     * Update animation state, if used
     *
     * @param elapsedTicks Number of ticks elapsed since last update
     */
    override fun update(elapsedTicks: Int)
    {
        // If we are in seasonal mode, update all seasonal tile instances
        if(this.tileMode == TileTypeMode.Seasonal)
        {
            for (instance in this.seasonalInstances)
                instance.update(elapsedTicks)
        }
        // We only need to do updates here if the mode is "animated"
        else if(this.tileMode == TileTypeMode.Animated && this.animIdx.isNotEmpty())
        {
            this.animIdx.update(elapsedTicks)
        }
    }

    /**
     * Advance stored information to next season.
     */
    fun nextSeason()
    {
        if(this.tileMode == TileTypeMode.Seasonal)
            this.currentSeason.nextSeason()
        else
            throw IllegalStateException("Tile not in seasonal mode")
    }

    /**
     * Replace the stored season information with the given value
     *
     * @param season New season value
     */
    fun setSeason(season: Season)
    {
        if(this.tileMode == TileTypeMode.Seasonal)
            this.currentSeason = season
        else
            throw IllegalStateException("Tile not in seasonal mode")
    }

    /**
     * For seasonal tile types, retrieve currently relevant tile instance based on [currentSeason]
     *
     * @return Currently relevant tile instance
     */
    fun instanceForSeason(): TileInstance
    {
        if(this.tileMode != TileTypeMode.Seasonal)
            throw IllegalStateException("instanceForSeason only works in seasonal mode")

        if(this.seasonalInstances.size != 4)
            throw IllegalStateException("Invalid number of seasonal tile instances")

        return this.seasonalInstances[this.currentSeason.index]
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
 * @property seasonalTiles Seasonal tiles for each season of the year
 */
@Serializable
class TileType(
        val mode: TileTypeMode = TileTypeMode.Static,
        val staticTile: DrawInfo = DrawInfo(),
        val variedTiles: WeightedTileList = WeightedTileList(listOf()),
        val animatedTile: Animation = Animation.empty(),
        val seasonalTiles: List<TileType> = listOf()
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
            TileTypeMode.Seasonal -> {
                // Create tile instance for all seasonal types
                val seasonalInstances = ArrayList<TileInstance>()
                for(seasonalTile in this.seasonalTiles)
                    seasonalInstances.add(seasonalTile.createInstance(animOffset = animOffset))

                // Check that none of them are, recursively, also seasonal entries
                if(seasonalInstances.any { x -> x.tileMode == TileTypeMode.Seasonal })
                    throw IllegalStateException("Seasonal tile type that contain other seasonal tile types are not allowed")

                // Create tile instance for the parent tile type
                TileInstance(this.mode, seasonalInstances = seasonalInstances)
            }
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
            TileTypeMode.Seasonal -> this.seasonalTiles[instance.currentSeason.index].tile(instance.instanceForSeason())
            TileTypeMode.Static -> this.staticTile
            TileTypeMode.Varied -> this.variedTiles.elementAt(instance.tileIndex)
            TileTypeMode.Animated -> this.animatedTile.frameAtRaw(instance.animIdx.totalPeriods)
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
        // If the current tile is a no draw tile by itself, we should not draw
        if(instance.tileMode == TileTypeMode.NoDraw)
        {
            return false
        }
        // If the tile mode is seasonal, then the currently active seasonal tile might be NoDraw
        else if(instance.tileMode == TileTypeMode.Seasonal)
        {
            // Retrieve currently active tile instance
            val activeInstance = instance.instanceForSeason()

            // If its nodraw we are out of luck
            return activeInstance.tileMode != TileTypeMode.NoDraw
        }
        else
        {
            // Otherwise there will always be something to draw
            return true
        }

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
                TileTypeMode.Seasonal ->
                {
                    // The seasonal tiles entry has to exist
                    if(!root.containsKey("seasonal_tiles"))
                        throw RuntimeException("missing \"seasonal_tiles\" entry")

                    // Retrieve seasonal infos
                    val frameEntry = root.getArray("seasonal_tiles")

                    // Deserialize TileTypes
                    val seasonalTypes = Json.parse(TileTypeSerializer().list, frameEntry.toString())

                    // Construct tile type instance
                    TileType(mode, seasonalTiles = seasonalTypes)
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