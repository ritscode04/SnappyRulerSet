package com.example.snappyrulerset.geometry

import androidx.compose.ui.geometry.Offset
import kotlin.math.floor

class SpatialIndex(private val cellSize: Float = 80f) {
    private val buckets = mutableMapOf<Pair<Int,Int>, MutableList<Offset>>()

    private fun keyFor(p: Offset): Pair<Int,Int> = Pair(floor(p.x / cellSize).toInt(), floor(p.y / cellSize).toInt())

    fun insert(p: Offset) { val k = keyFor(p); buckets.getOrPut(k) { mutableListOf() }.add(p) }
    fun clear() { buckets.clear() }

    fun nearby(p: Offset, radius: Float): List<Offset> {
        val minx = floor((p.x - radius) / cellSize).toInt()
        val maxx = floor((p.x + radius) / cellSize).toInt()
        val miny = floor((p.y - radius) / cellSize).toInt()
        val maxy = floor((p.y + radius) / cellSize).toInt()
        val found = mutableListOf<Offset>()
        for (x in minx..maxx) for (y in miny..maxy) buckets[Pair(x,y)]?.let { cell -> found.addAll(cell) }
        return found.filter { (it - p).getDistance() <= radius }
    }
}
