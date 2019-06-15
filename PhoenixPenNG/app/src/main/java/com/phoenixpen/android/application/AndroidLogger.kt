package com.phoenixpen.android.application

import android.util.Log
import com.phoenixpen.game.logging.LogLevel
import com.phoenixpen.game.logging.Logger

/**
 * Logger implementation for Android
 */
class AndroidLogger: Logger()
{
    /**
     * Dispatch log message to Android logger
     */
    override fun log(level: LogLevel, tag: String, message: String)
    {
        // Different log severity levels require different logging endpoints
        when(level)
        {
            LogLevel.Debug -> Log.d(tag, message)
            LogLevel.Warning -> Log.w(tag, message)
            LogLevel.Error -> Log.e(tag, message)
            LogLevel.Info -> Log.i(tag, message)
        }
    }
}