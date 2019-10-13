package com.phoenixpen.android.livewallpaper

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.phoenixpen.android.utility.ObserverSource
import com.phoenixpen.game.settings.AppSettings
import com.phoenixpen.game.simulation.EternalSeasonType

/**
 * The main class used to receive and store user configuration data.
 * Other app components can subscribe it in order to receive notifications
 * when the configuration data changes.
 */
class Configuration : ObserverSource<Configuration>()
{
    /**
     * The current application settings object
     */
    var appSettings: AppSettings = AppSettings()

    /**
     * Singleton implementation
     */
    companion object
    {
        /**
         * The app-global singleton instance of this class
         */
        val instance = Configuration()
    }

    /**
     * Whether the configuration was already initialized using an android preference
     * instance
     */
    protected var isInitialized = false

    /**
     * Initialise or update the configuration state using the current app context.
     * This will extract all needed information from the currently active SharedPreferences
     * instance.
     *
     * @param ctx The app context
     */
    fun update(ctx: Context)
    {
        // Retrieve current preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

        // Delegate to other update method
        this.update(prefs)
    }

    /**
     * Initialise or update the configuration state using SharedPreferences object.
     *
     * @param prefs The SharedPreferences instance to retrieve data from
     */
    fun update(prefs: SharedPreferences)
    {
        // Load preferences here
        this.loadSettings(prefs)

        // Notify all sinks of the update
        this.notifyAll(this)

        // Mark as initialized
        this.isInitialized = true
    }

    /**
     * Actual perform settings load
     *
     * @param prefs The SharedPreferences instance to retrieve data from
     */
    private fun loadSettings(prefs: SharedPreferences)
    {
        this.appSettings.enableRain = prefs.getBoolean("enable_rain", true)
        this.appSettings.enableSnow = prefs.getBoolean("enable_snow", true)
        this.appSettings.enableSeasons = prefs.getBoolean("enable_seasons", true)
        this.appSettings.enableEternalSeason = prefs.getBoolean("enable_eternal_season", true)
        this.appSettings.eternalSeason = this.readEternalSeason(prefs)
    }

    private fun readEternalSeason(prefs: SharedPreferences): EternalSeasonType
    {
        val value = prefs.getString("eternal_season", EternalSeasonType.Spring.toString())

        return enumValueOf(value)
    }
}