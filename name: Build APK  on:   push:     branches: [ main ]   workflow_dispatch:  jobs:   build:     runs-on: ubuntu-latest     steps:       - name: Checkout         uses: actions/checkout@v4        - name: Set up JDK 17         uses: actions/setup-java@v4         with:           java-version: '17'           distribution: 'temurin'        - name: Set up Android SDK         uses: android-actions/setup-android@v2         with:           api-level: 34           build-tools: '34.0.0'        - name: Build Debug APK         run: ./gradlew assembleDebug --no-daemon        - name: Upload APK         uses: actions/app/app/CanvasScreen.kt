package com.example.snappyrulerset.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.snappyrulerset.state.StateModel
import kotlinx.coroutines.launch

@Composable
fun CanvasScreen() {
    val state = remember { StateModel() }
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        ToolPalette(state)
        Box(Modifier.fillMaxSize()) {
            DrawingCanvas(state, modifier = Modifier.fillMaxSize())

            Column(Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Text(text = "Angle: ${state.currentAngle.format(1)}°")
                Text(text = "Len: ${state.currentLength.format(1)} px")
                Row {
                    Button(onClick = { state.undo() }) { Text("Undo") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { state.redo() }) { Text("Redo") }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        try {
                            val bmp = state.exportToBitmap()
                            // saving/sharing handled from Activity in real app (omitted)
                        } catch (e: Exception) { Log.e("Export", "failed", e) }
                    }
                }) { Text("Export") }
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)
private fun Float.format(digits: Int) = this.toDouble().format(digits)

@Composable
fun DrawingCanvas(state: StateModel, modifier: Modifier = Modifier) {
    val gridSpacingPx = with(LocalDensity.current) { 5.dp.toPx() * 2f }
    var lastPoint by remember { mutableStateOf<Offset?>(null) }

    Canvas(modifier = modifier
        .background(Color(0xFFFAFAFA))
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, rotation ->
                if (state.isToolActive) state.onToolTransform(pan, zoom, rotation)
                else {
                    state.canvasPan += pan
                    state.canvasScale *= zoom
                }
            }
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    lastPoint = offset
                    if (!state.isToolActive) state.startFreehand(offset) else state.onToolTouchDown(offset)
                },
                onDrag = { change, _ ->
                    val pos = change.position
                    if (!state.isToolActive) state.updateFreehand(pos) else state.onToolDrag(pos)
                    lastPoint = pos
                },
                onDragEnd = {
                    if (!state.isToolActive) state.finishFreehand() else state.onToolRelease()
                    lastPoint = null
                }
            )
        }
    ) {
        drawGrid(gridSpacingPx, color = Color(0xFFEFEFEF))

        state.shapes.forEach { shape ->
            when (shape) {
                is StateModel.Shape.Freehand -> drawPath(shape.path, color = Color.Black, style = Stroke(width = shape.strokeWidth))
                is StateModel.Shape.Line -> drawLine(color = Color.Black, start = shape.start, end = shape.end, strokeWidth = shape.strokeWidth)
                is StateModel.Shape.Circle -> drawCircle(color = Color.Black, radius = shape.radius, center = shape.center, style = Stroke(width = shape.strokeWidth))
            }
        }

        state.currentTool?.let { tool ->
            when (tool) {
                is com.example.snappyrulerset.tools.RulerTool -> tool.drawPreview(this)
                is com.example.snappyrulerset.tools.SetSquareTool -> tool.drawPreview(this)
                is com.example.snappyrulerset.tools.ProtractorTool -> tool.drawPreview(this)
                is com.example.snappyrulerset.tools.CompassTool -> tool.drawPreview(this)
            }
        }

        state.activeFreehand?.let { pf -> drawPath(pf, color = Color.Black, style = Stroke(width = 3f)) }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrid(spacing: Float, color: Color) {
    val w = size.width
    val h = size.height
    var x = 0f
    while (x < w) { drawLine(color = color, start = Offset(x, 0f), end = Offset(x, h), strokeWidth = 0.5f); x += spacing }
    var y = 0f
    while (y < h) { drawLine(color = color, start = Offset(0f, y), end = Offset(w, y), strokeWidth = 0.5f); y += spacing }
}
