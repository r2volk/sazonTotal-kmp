package org.sazontotal.project.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun BackspaceIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF8E8E93)
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val tagPath = Path().apply {
            moveTo(w * 0.32f, h * 0.22f)
            lineTo(w * 0.88f, h * 0.22f)
            lineTo(w * 0.88f, h * 0.78f)
            lineTo(w * 0.32f, h * 0.78f)
            lineTo(w * 0.12f, h * 0.50f)
            close()
        }
        drawPath(
            path = tagPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )

        // Draw 'X' inside
        val xMin = w * 0.44f
        val xMax = w * 0.70f
        val yMin = h * 0.36f
        val yMax = h * 0.64f
        drawLine(
            color = color,
            start = Offset(xMin, yMin),
            end = Offset(xMax, yMax),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(xMax, yMin),
            end = Offset(xMin, yMax),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
