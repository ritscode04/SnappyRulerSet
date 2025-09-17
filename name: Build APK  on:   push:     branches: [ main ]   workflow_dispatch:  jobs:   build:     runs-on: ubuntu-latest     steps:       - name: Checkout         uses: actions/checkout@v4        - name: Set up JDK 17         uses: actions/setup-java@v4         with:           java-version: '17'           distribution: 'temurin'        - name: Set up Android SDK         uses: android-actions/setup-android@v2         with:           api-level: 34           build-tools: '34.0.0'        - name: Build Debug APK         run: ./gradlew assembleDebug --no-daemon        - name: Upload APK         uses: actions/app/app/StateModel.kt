package com.example.snappyrulerset.state

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.example.snappyrulerset.geometry.SpatialIndex
import com.example.snappyrulerset.tools.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class StateModel {
    sealed class Shape {
        data class Freehand(val path: Path, val strokeWidth: Float = 3f) : Shape()
        data class Line(val start: Offset, val end: Offset, val strokeWidth: Float = 4f) : Shape()
        data class Circle(val center: Offset, val radius: Float, val strokeWidth: Float = 3f) : Shape()
    }

    val shapes = mutableListOf<Shape>().let { androidx.compose.runtime.mutableStateListOf(*it.toTypedArray()) }
    var activeFreehand: Path? = null

    var currentTool: Tool? = null
    var canvasPan = Offset.Zero
    var canvasScale = 1f
    var isToolActive = false

    private val index = SpatialIndex(cellSize = 80f)

    private val undoStack: Deque<List<Shape>> = ArrayDeque()
    private val redoStack: Deque<List<Shape>> = ArrayDeque()
    private val maxHistory = 20

    var currentAngle = 0.0
    var currentLength = 0f

    enum class ToolType { RULER, SETSQUARE, PROTRACTOR, COMPASS, FREEHAND }

    fun selectTool(t: ToolType) {
        currentTool = when (t) {
            ToolType.RULER -> RulerTool()
            ToolType.SETSQUARE -> SetSquareTool()
            ToolType.PROTRACTOR -> ProtractorTool()
            ToolType.COMPASS -> CompassTool()
            ToolType.FREEHAND -> null
        }
        isToolActive = (t != ToolType.FREEHAND)
    }

    fun startFreehand(p: Offset) { pushHistory(); activeFreehand = Path().apply { moveTo(p.x, p.y) } }
    fun updateFreehand(p: Offset) { activeFreehand?.lineTo(p.x, p.y) }
    fun finishFreehand() { activeFreehand?.let { shapes.add(Shape.Freehand(Path(it), 3f)) }; activeFreehand = null }

    fun onToolTouchDown(pos: Offset) { currentTool?.onTouchDown(pos) }
    fun onToolDrag(pos: Offset) { currentTool?.onDrag(pos) }
    fun onToolRelease() { currentTool?.onRelease() }
    fun onToolTransform(pan: Offset, zoom: Float, rotation: Float) { currentTool?.onTransform(pan, zoom, rotation) }

    private fun snapshot(): List<Shape> = shapes.map { it }
    private fun restoreSnapshot(s: List<Shape>) { shapes.clear(); shapes.addAll(s) }

    fun pushHistory() { undoStack.push(snapshot()); if (undoStack.size > maxHistory) undoStack.removeLast(); redoStack.clear() }
    fun undo() { if (undoStack.isEmpty()) return; redoStack.push(snapshot()); restoreSnapshot(undoStack.pop()) }
    fun redo() { if (redoStack.isEmpty()) return; undoStack.push(snapshot()); restoreSnapshot(redoStack.pop()) }

    suspend fun exportToBitmap(): android.graphics.Bitmap = withContext(Dispatchers.Default) {
        val w = 1080; val h = 1920
        val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(bmp)
        c.drawColor(android.graphics.Color.WHITE)
        val paint = android.graphics.Paint().apply { color = android.graphics.Color.BLACK; strokeWidth = 4f }
        shapes.forEach { s ->
            when (s) {
                is Shape.Line -> c.drawLine(s.start.x, s.start.y, s.end.x, s.end.y, paint)
                is Shape.Circle -> c.drawCircle(s.center.x, s.center.y, s.radius, paint)
                is Shape.Freehand -> { /* re-tracing Path to Canvas omitted for brevity */ }
            }
        }
        bmp
    }
}
