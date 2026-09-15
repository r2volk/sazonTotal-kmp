package org.sazontotal.project.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF22C55E)
) {
    Canvas(modifier = modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.2.dp.toPx()

        drawLine(
            color = color,
            start = Offset(w * 0.20f, h * 0.55f),
            end = Offset(w * 0.44f, h * 0.75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
            // join not needed for lines but keeps round ends consistent
        )
        drawLine(
            color = color,
            start = Offset(w * 0.44f, h * 0.75f),
            end = Offset(w * 0.80f, h * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
