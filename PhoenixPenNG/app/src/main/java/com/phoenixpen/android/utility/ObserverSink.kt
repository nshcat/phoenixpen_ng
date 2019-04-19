package com.phoenixpen.android.utility

public interface ObserverSink<T>
{
    /**-
     * Update this observer sink with the current state of the
     * observed object
     */
    fun notify(value: T)
}