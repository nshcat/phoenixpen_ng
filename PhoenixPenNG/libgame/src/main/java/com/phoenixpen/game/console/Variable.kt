package com.phoenixpen.game.console

import java.lang.IllegalArgumentException

/**
 * Base class for console variables, which are local variables which can be registered in a global
 * variable and command manager to be accessed over the debug console. By storing variables at the
 * point where they are used, costly access is avoided and replaced by cheap direct access.
 *
 * @property name The qualified identifier of this variable. Might contain namespaces separated by periods.
 * @property description Description of this variable
 * @property type A short string describing the underlying data type, e.g. "bool". Used for user interface.
 * @property typeLetter A letter describing the underlying data type, e.g. "B". Used for user interface.
 */
abstract class Variable(
        val name: String,
        val description: String,
        val type: String,
        val typeLetter: Char
)
{
    /**
     * The identifier of this variable, which is its name without any qualifying namespaces.
     */
    val identifier: String

    /**
     * The individual segments of the variable name, includes the identifier.
     */
    val segments: Array<String>

    /**
     * Process given name.
     */
    init
    {
        // Empty name is not allowed
        if(this.name.isEmpty())
            throw IllegalArgumentException("Variable name must not be empty")

        // Split name string at periods
        val split = this.name.split('.')

        // The identifier is the last element
        this.identifier = split.last()

        // Save segments
        this.segments = split.toTypedArray()
    }

    /**
     * Retrieve contained value as string
     *
     *  @return Value of this variable, as string
     */
    abstract fun toRaw(): String

    /**
     * Set the value of this variable from string
     *
     * @param input Input string
     */
    abstract fun fromRaw(input: String)

    /**
     * Check whether given input string is a legal representation of the data type of this variable
     *
     * @param input Input string to check
     * @return Flag indicating whether given string was valid
     */
    abstract fun isValidRaw(input: String): Boolean
}

/**
 * A variable based on a boolean flag.
 *
 * @param name Variable identifier, may contain namespaces separated by periods
 * @param description Descriptive text for this variable
 * @property value The actual boolean flag value
 */
class BooleanVariable(
        name: String,
        description: String,
        var value: Boolean = true
    ) : Variable(name, description, "bool", 'b')
{
    /**
     * Retrieve contained value as string
     *
     *  @return Value of this variable, as string
     */
    override fun toRaw(): String
    {
        return this.value.toString()
    }

    /**
     * Set the value of this variable from string
     *
     * @param input Input string
     */
    override fun fromRaw(input: String)
    {
        this.value = input.toBoolean()
    }

    /**
     * Check whether given input string is a legal representation of the data type of this variable
     *
     * @param input Input string to check
     * @return Flag indicating whether given string was valid
     */
    override fun isValidRaw(input: String): Boolean
    {
        // TODO its not easy to check for validity in kotlin here, since toBoolean returns false
        // for all invalid inputs
        return true
    }
}