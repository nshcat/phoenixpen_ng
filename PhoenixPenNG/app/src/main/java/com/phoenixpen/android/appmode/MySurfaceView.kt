package com.phoenixpen.android.appmode

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.phoenixpen.android.R
import com.phoenixpen.android.application.AsciiApplication
import com.phoenixpen.android.ascii.Color
import com.phoenixpen.android.ascii.DrawInfo
import com.phoenixpen.android.data.MaterialManager
import com.phoenixpen.android.data.MaterialType
import com.phoenixpen.android.map.MapCellState
import com.phoenixpen.android.utility.WeightedList
import com.phoenixpen.android.utility.WeightedPair
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
class Test(val test: WeightedList<Color>)

class MySurfaceView(ctx: Context): GLSurfaceView(ctx)
{
    init
    {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        setRenderer(AsciiApplication(ctx))

       /* val info = WeightedPair(DrawInfo(139, Color.red, Color.green), 0.5)
        val info2 = WeightedPair(DrawInfo(1, Color.white, Color.black), 0.5)

        val material = MaterialType(
                MapCellState.Ground,
                "test_material",
                "Test material for debugging",
                WeightedList(listOf(info, info2)),
                WeightedList(listOf(info, info2))
        )

        val json = Json.indented.stringify(MaterialType.serializer(), material)*/

       /* val mgr = MaterialManager()
        mgr.loadMaterials(this.context, R.raw.materials)

        val mat = mgr.lookupMaterial("test_material")
        val mat2 = mgr.lookupMaterial("test_material2")

        Log.d("nya", "\n${mat.description}")
        Log.d("nya", "\n${mat2.description}")*/
    }
}