package com.phoenixpen.android.livewallpaper

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.phoenixpen.android.utility.ObserverSource

/**
 * The main class used to receive and store user configuration data.
 * Other app components can subscribe it in order to receive notifications
 * when the configuration data changes.
 */
class Configuration : ObserverSource<Configuration>()
{
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
        /* ... */

        // Notify all sinks of the update
        this.notifyAll(this)

        // Mark as initialized
        this.isInitialized = true
    }
}