package com.example.snappyrulerset.tools

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

class CompassTool : Tool {
    override var active = true
    private var center: Offset? = null
    private var radius = 0f

    override fun onTouchDown(pos: Offset) { if (center == null) center = pos else radius = (pos - center!!).getDistance() }
    override fun onDrag(pos: Offset) { radius = (pos - (center ?: pos)).getDistance() }
    override fun onRelease() {}
    override fun onTransform(pan: Offset, zoom: Float, rotation: Float) { center = center?.plus(pan); radius *= zoom }
    override fun drawPreview(drawScope: DrawScope) {
        center?.let { c -> drawScope.drawCircle(color = androidx.compose.ui.graphics.Color.Green, center = c, radius = radius, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)) }
    }
}
