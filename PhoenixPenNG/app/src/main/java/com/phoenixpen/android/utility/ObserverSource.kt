package com.phoenixpen.android.utility

/**
 * A class representing an object that manages something observable.
 * The derived class does need to be this thing, but can merely hold it
 * as an attribute, for example.
 *
 * @param T The type of observed object
 */
abstract class ObserverSource<T>
{
    /**
     * All registered sinks. These will all be notified
     * when the observed object changes.
     */
    protected var sinks: MutableList<ObserverSink<T>> = ArrayList()

    /**
     * Register an observer sink with this source.
     * It will be notified of any changes to the observed object.
     *
     * @param sink The sink to register
     */
    fun subscribe(sink: ObserverSink<T>)
    {
        this.sinks.add(sink)
    }

    /**
     * Unregister an observer sink from this source. It will not receive
     * any notifications anymore.
     *
     * @param sink The sink to unregister
     */
    fun unsubscribe(sink: ObserverSink<T>)
    {
        this.sinks.remove(sink)
    }

    /**
     * Notify all subscribed sinks with given object value
     *
     * @param value The value of the observed object
     */
    protected fun notifyAll(value: T)
    {
        for(sink in this.sinks)
        {
            // Notify this sink
            sink.notify(value)
        }
    }
}