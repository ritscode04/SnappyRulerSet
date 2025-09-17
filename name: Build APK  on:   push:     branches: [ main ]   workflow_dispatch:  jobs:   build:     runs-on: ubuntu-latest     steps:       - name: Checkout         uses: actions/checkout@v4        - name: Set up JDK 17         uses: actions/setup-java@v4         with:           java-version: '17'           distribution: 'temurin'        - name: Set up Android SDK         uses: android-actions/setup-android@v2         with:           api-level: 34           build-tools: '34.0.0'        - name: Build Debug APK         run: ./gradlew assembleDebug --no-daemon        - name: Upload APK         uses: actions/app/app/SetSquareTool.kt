package com.example.snappyrulerset.tools

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

class SetSquareTool : Tool {
    override var active = true
    override fun onTouchDown(pos: Offset) {}
    override fun onDrag(pos: Offset) {}
    override fun onRelease() {}
    override fun onTransform(pan: Offset, zoom: Float, rotation: Float) {}
    override fun drawPreview(drawScope: DrawScope) { /* placeholder preview — extend later */ }
}
