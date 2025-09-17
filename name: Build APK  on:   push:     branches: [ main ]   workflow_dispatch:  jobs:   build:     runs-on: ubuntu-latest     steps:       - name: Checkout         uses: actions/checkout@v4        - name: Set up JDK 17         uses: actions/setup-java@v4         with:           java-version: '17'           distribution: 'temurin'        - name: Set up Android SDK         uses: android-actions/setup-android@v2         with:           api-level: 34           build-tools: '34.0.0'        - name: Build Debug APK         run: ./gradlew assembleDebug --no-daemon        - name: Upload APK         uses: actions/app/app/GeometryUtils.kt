package com.example.snappyrulerset.geometry

import androidx.compose.ui.geometry.Offset
import kotlin.math.*

object GeometryUtils {
    fun distance(a: Offset, b: Offset): Float = (a - b).getDistance()

    fun angleBetween(a: Offset, b: Offset): Double = Math.toDegrees(atan2(b.y - a.y, b.x - a.x).toDouble())

    fun projectPointOntoLine(p: Offset, a: Offset, b: Offset): Offset {
        val apx = p.x - a.x; val apy = p.y - a.y
        val abx = b.x - a.x; val aby = b.y - a.y
        val abLen2 = abx * abx + aby * aby
        val t = if (abLen2 == 0f) 0f else (apx * abx + apy * aby) / abLen2
        return Offset(a.x + abx * t, a.y + aby * t)
    }

    fun intersectionOfLines(a1: Offset, a2: Offset, b1: Offset, b2: Offset): Offset? {
        val x1 = a1.x; val y1 = a1.y; val x2 = a2.x; val y2 = a2.y
        val x3 = b1.x; val y3 = b1.y; val x4 = b2.x; val y4 = b2.y
        val denom = (y4 - y3)*(x2 - x1) - (x4 - x3)*(y2 - y1)
        if (denom == 0f) return null
        val ua = ((x4 - x3)*(y1 - y3) - (y4 - y3)*(x1 - x3)) / denom
        val x = x1 + ua * (x2 - x1)
        val y = y1 + ua * (y2 - y1)
        return Offset(x, y)
    }
}
