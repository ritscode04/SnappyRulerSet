package com.example.snappyrulerset.tools

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

interface Tool {
    var active: Boolean
    fun onTouchDown(pos: Offset)
    fun onDrag(pos: Offset)
    fun onRelease()
    fun onTransform(pan: Offset, zoom: Float, rotation: Float)
    fun drawPreview(drawScope: DrawScope)
}
