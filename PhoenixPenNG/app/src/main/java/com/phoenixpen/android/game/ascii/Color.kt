package com.phoenixpen.android.game.ascii

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

/**
 * An integral RGB color value. Each component has to be in range [0, 255].
 */
@Serializable(with=ColorSerializer::class)
data class Color(var r: Int, var g: Int, var b: Int)
{
    //@Serializer(forClass = Color::class)
    companion object //: KSerializer<Color>
    {
        /*override val descriptor: SerialDescriptor =
            StringDescriptor.withName("Color")

        override fun serialize(output: Encoder, obj: Color)
        {
            val toHex = { x: Int -> x.toString(16) }

            output.encodeString("#${toHex(obj.r)}${toHex(obj.g)}${toHex(obj.b)}")
        }

        override fun deserialize(input: Decoder): Color
        {
            val string = input.decodeString()

            val fromHex = { x: String -> x.toInt(16)}

            return Color(
                fromHex(string.substring(1, 3)),
                fromHex(string.substring(3, 5)),
                fromHex(string.substring(5, 7))
            )
        }*/

        val red = Color(255, 0, 0)
        val green = Color(0, 255, 0)
        val blue = Color(0, 0, 255)
        val black = Color(0, 0, 0)
        val white = Color(255, 255, 255)
        val magenta = Color(255, 0, 255)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color

        if (r != other.r) return false
        if (g != other.g) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        var result = r
        result = 31 * result + g
        result = 31 * result + b
        return result
    }
}


@Serializer(forClass = Color::class)
object ColorSerializer: KSerializer<Color>
{
    override val descriptor: SerialDescriptor =
            StringDescriptor.withName("Color")

    override fun serialize(output: Encoder, obj: Color)
    {
        val toHex = { x: Int ->
            val result = x.toString(16)
            if(result.length == 1) "0$result".toUpperCase()
            else result.toUpperCase()
        }

        output.encodeString("#${toHex(obj.r)}${toHex(obj.g)}${toHex(obj.b)}")
    }

    override fun deserialize(input: Decoder): Color
    {
        val string = input.decodeString()

        val fromHex = { x: String -> x.toInt(16)}

        return Color(
                fromHex(string.substring(1, 3)),
                fromHex(string.substring(3, 5)),
                fromHex(string.substring(5, 7))
        )
    }
}