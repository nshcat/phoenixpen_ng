package com.phoenixpen.game.core

/**
 * Base class for types that support observer classes to subscribe in order to be notified of state
 * changes, which are based on values of type [T].
 *
 * @param T Type of values that are observed
 */
abstract class Observable<T>
{
    /**
     * All registered observers. These will all be notified
     * when the observed object changes.
     */
    protected val observers: MutableList<Observer<T>> = ArrayList()

    /**
     * Register an observer with this observable.
     * It will be notified of any changes to the observed object.
     *
     * @param sink The observer to register
     */
    fun subscribe(observer: Observer<T>)
    {
        this.observers.add(observer)
    }

    /**
     * Unregister an observer from this observable. It will not receive
     * any notifications anymore.
     *
     * @param sink The observer to unregister
     */
    fun unsubscribe(observer: Observer<T>)
    {
        this.observers.remove(observer)
    }

    /**
     * Notify all subscribed observers with given object value
     *
     * @param value The value of the observed object
     */
    protected fun notifyAll(value: T)
    {
        for(observer in this.observers)
        {
            // Notify this sink
            observer.notify(value)
        }
    }
}