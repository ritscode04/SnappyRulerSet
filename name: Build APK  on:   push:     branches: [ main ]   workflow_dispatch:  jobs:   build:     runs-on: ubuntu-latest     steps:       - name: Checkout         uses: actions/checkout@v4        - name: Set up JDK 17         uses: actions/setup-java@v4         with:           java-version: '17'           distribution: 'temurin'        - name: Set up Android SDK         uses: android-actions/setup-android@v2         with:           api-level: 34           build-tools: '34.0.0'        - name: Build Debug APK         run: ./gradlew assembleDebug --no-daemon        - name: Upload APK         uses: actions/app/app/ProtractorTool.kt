package com.example.snappyrulerset.tools

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.atan2

class ProtractorTool : Tool {
    override var active = true
    private var vertex: Offset? = null
    private var rayA: Offset? = null
    private var rayB: Offset? = null

    override fun onTouchDown(pos: Offset) { vertex = pos; rayA = pos; rayB = null }
    override fun onDrag(pos: Offset) { if (rayA != null) rayB = pos }
    override fun onRelease() {}
    override fun onTransform(pan: Offset, zoom: Float, rotation: Float) {}
    override fun drawPreview(drawScope: DrawScope) {
        val v = vertex ?: return
        val a = rayA ?: return
        val b = rayB ?: return
        drawScope.drawLine(Color.Magenta, start = v, end = a, strokeWidth = 3f)
        drawScope.drawLine(Color.Magenta, start = v, end = b, strokeWidth = 3f)
        val angle = angleBetween(a, v, b)
        val hardAngles = listOf(30.0,45.0,60.0,90.0,120.0,135.0,150.0,180.0)
        val snapped = hardAngles.minByOrNull { kotlin.math.abs(it - angle) }?.takeIf { kotlin.math.abs(it - angle) <= 1.0 } ?: angle
        drawScope.drawContext.canvas.nativeCanvas.apply {
            drawText("${snapped.format(1)}°", v.x + 10f, v.y - 10f, android.graphics.Paint().apply { color = android.graphics.Color.BLACK; textSize = 36f })
        }
    }

    private fun angleBetween(a: Offset, v: Offset, b: Offset): Double {
        val ang1 = atan2(a.y - v.y, a.x - v.x)
        val ang2 = atan2(b.y - v.y, b.x - v.x)
        var d = Math.toDegrees((ang2 - ang1).toDouble())
        if (d < 0) d += 360.0
        return d
    }
    private fun Double.format(d: Int) = "%.${d}f".format(this)
}
