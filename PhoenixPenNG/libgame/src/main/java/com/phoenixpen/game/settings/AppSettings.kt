package com.phoenixpen.game.settings

import com.phoenixpen.game.simulation.EternalSeasonType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A class representing the application user settings - which means data that is directly changeable
 * by the user, i.e. via Android app settings or a local configuration file. It is supplied to the
 * game library by the platform-dependant layers.
 *
 * It is important that all fields have sane defaults set, since there needs to be a way to create
 * fallback settings instance, for example when the user starts the application for the first time.
 *
 * @property isMinimalMode Whether the device requests all unneeded featured to be turned off, used on resource constrained devices.
 * @property mainTileSetId The resource ID of the main glyph tile set, used for graphical parts of the application
 * @property consoleTileSetId The resource ID of the console tile set
 */
@Serializable
class AppSettings(
    @SerialName("minimalistic_mode") var isMinimalMode: Boolean = false,
    @SerialName("main_tileset_id") var mainTileSetId: String = "text.png",
    @SerialName("console_tileset_id") var consoleTileSetId: String = "curses_640x300.png",
    @SerialName("enable_seasons") var enableSeasons: Boolean = true,
    @SerialName("enable_rain") var enableRain: Boolean = true,
    @SerialName("enable_snow") var enableSnow: Boolean = true,
    @SerialName("enable_eternal_season") var enableEternalSeason: Boolean = false,
    @SerialName("eternal_season") var eternalSeason: EternalSeasonType = EternalSeasonType.Spring
)