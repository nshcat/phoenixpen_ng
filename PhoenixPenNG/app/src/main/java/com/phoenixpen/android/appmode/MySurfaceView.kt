package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import com.phoenixpen.android.game.ascii.ColorSerializer
import com.phoenixpen.android.game.ascii.Position3D
import com.phoenixpen.android.game.ascii.TileType
import com.phoenixpen.android.game.ascii.TileTypeSerializer
import com.phoenixpen.android.game.core.AsciiApplication
import com.phoenixpen.android.game.data.PlaceholderDefinition
import com.phoenixpen.android.game.data.PlaceholderDefinitionSerializer
import com.phoenixpen.android.game.data.TreeStructure
import com.phoenixpen.android.game.data.TreeStructureType
import com.phoenixpen.android.game.simulation.TreeHolder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    init
    {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        //setRenderer(AsciiApplication(ctx))

        android.os.Debug.waitForDebugger()

        val json = """
            {
                "mode": "Varied",
                "data" : [
                    { "tile": { "glyph": 1 }, "probability": 0.3  }
                ]
            }
        """.trimIndent()

        val tile = Json.parse(TileTypeSerializer(), json)

        val x = 1


    }
}