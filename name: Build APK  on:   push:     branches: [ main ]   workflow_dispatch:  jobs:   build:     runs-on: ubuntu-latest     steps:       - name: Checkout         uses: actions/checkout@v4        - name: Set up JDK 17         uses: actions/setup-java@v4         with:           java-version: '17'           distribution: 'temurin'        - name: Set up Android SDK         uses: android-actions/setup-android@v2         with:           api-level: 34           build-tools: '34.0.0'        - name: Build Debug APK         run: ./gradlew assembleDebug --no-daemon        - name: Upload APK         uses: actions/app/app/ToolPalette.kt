package com.example.snappyrulerset.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snappyrulerset.state.StateModel

@Composable
fun ToolPalette(state: StateModel) {
    Row(Modifier.padding(8.dp)) {
        Button(onClick = { state.selectTool(StateModel.ToolType.RULER) }) { Text("Ruler") }
        Button(onClick = { state.selectTool(StateModel.ToolType.SETSQUARE) }) { Text("Set Square") }
        Button(onClick = { state.selectTool(StateModel.ToolType.PROTRACTOR) }) { Text("Protractor") }
        Button(onClick = { state.selectTool(StateModel.ToolType.COMPASS) }) { Text("Compass") }
        Button(onClick = { state.selectTool(StateModel.ToolType.FREEHAND) }) { Text("Freehand") }
    }
}
