package com.phoenixpen.android.application

import android.util.Log
import com.phoenixpen.game.logging.LogLevel
import com.phoenixpen.game.logging.LogMessage
import com.phoenixpen.game.logging.Logger

/**
 * Logger implementation for Android
 */
class AndroidLogger: Logger()
{
    /**
     * Dispatch log message to Android logger
     */
    override fun log(message: LogMessage)
    {
        // Different log severity levels require different logging endpoints
        when(message.level)
        {
            LogLevel.Debug -> Log.d(message.tag, message.message)
            LogLevel.Warning -> Log.w(message.tag, message.message)
            LogLevel.Error -> Log.e(message.tag, message.message)
            LogLevel.Info -> Log.i(message.tag, message.message)
        }
    }
}