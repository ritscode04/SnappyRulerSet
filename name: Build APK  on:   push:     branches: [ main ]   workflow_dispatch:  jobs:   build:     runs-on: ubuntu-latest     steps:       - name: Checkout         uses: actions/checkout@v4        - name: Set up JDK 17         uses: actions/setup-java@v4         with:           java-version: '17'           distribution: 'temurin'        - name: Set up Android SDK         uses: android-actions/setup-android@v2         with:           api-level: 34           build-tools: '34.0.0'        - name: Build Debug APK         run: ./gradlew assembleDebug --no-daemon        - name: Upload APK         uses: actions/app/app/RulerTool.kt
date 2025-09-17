package com.example.snappyrulerset.tools

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.*

class RulerTool(
    var center: Offset = Offset.Zero,
    var rotationDeg: Float = 0f,
    var length: Float = 600f
) : Tool {
    override var active = true
    private var dragging = false
    private var dragStart = Offset.Zero
    private val allowedAngles = listOf(0f, 30f, 45f, 60f, 90f)
    private val snapAngleThreshold = 8f

    override fun onTouchDown(pos: Offset) { dragging = true; dragStart = pos }
    override fun onDrag(pos: Offset) { if (!dragging) return; val delta = pos - dragStart; center += delta; dragStart = pos }
    override fun onRelease() { dragging = false }
    override fun onTransform(pan: Offset, zoom: Float, rotation: Float) {
        val rotDeg = Math.toDegrees(rotation.toDouble()).toFloat()
        rotationDeg += rotDeg
        val snap = allowedAngles.minByOrNull { abs(shortAngleDiff(it, rotationDeg)) } ?: 0f
        if (abs(shortAngleDiff(snap, rotationDeg)) <= snapAngleThreshold) rotationDeg = snap
        center += pan
        length *= zoom
        if (length < 40f) length = 40f
    }

    private fun shortAngleDiff(a: Float, b: Float): Float {
        var d = (a - b) % 360f
        if (d > 180f) d -= 360f
        if (d < -180f) d += 360f
        return d
    }

    override fun drawPreview(drawScope: DrawScope) {
        val half = length / 2f
        val theta = Math.toRadians(rotationDeg.toDouble())
        val dx = cos(theta).toFloat()
        val dy = sin(theta).toFloat()
        val p1 = center + Offset(-dx * half, -dy * half)
        val p2 = center + Offset(dx * half, dy * half)
        drawScope.drawLine(color = Color(0xFF1E88E5), start = p1, end = p2, strokeWidth = 6f)
        drawScope.drawCircle(color = Color(0xFF1E88E5), center = center, radius = 6f)
        drawScope.drawContext.canvas.nativeCanvas.apply {
            drawText("${rotationDeg.format(1)}°", center.x + 12f, center.y - 12f, android.graphics.Paint().apply { color = android.graphics.Color.BLACK; textSize = 36f })
        }
    }

    private fun Float.format(d: Int) = "%.${d}f".format(this)
}
