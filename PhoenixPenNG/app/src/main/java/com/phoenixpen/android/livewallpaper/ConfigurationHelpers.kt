package com.phoenixpen.android.livewallpaper

import android.content.SharedPreferences

/**
 * Retrieve integral value from given shared preferences instance. If retrieval
 * fails, use given default value.
 *
 * @param prefs The preferences instance to retrieve data from
 * @param key Key of the value to retrieve
 * @param default Default value, will be used if any error occurs
 * @return Stored preference value if it exists and is valid, default value otherwise
 */
fun parseInteger(prefs: SharedPreferences, key: String, default: Int) : Int
{
    // Retrieve string representation of the default value for usage with
    // the Android API
    val defaultStr = default.toString()

    // Try to retrieve configuration value
    try
    {
        // Return value entry and convert to integer.
        return prefs.getString(key, defaultStr).toInt()
    }
    catch(ex: NumberFormatException)
    {
        // Just return the default value
        return default
    }
}