package com.bold.trading.core.charts.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.theme.BoldColors

/**
 * Institutional RSI (14) Momentum Sub-Chart Canvas.
 * Renders 30/70 momentum boundaries, shaded neutral band, and smooth RSI curve.
 */
@Composable
fun RsiSubChartCanvas(
    rsiValues: List<Float?>,
    modifier: Modifier = Modifier
) {
    if (rsiValues.size < 2) return

    val textMeasurer = rememberTextMeasurer()
    val rightMargin = 140f

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .height(100.dp)
    ) {
        val chartWidth = size.width - rightMargin
        val chartHeight = size.height

        fun rsiToY(rsi: Float): Float {
            val clamped = rsi.coerceIn(0f, 100f)
            return chartHeight * (1f - (clamped / 100f))
        }

        val y70 = rsiToY(70f)
        val y30 = rsiToY(30f)
        val y50 = rsiToY(50f)

        // Shaded 30-70 zone
        drawRect(
            color = BoldColors.SurfaceElevated.copy(alpha = 0.5f),
            topLeft = Offset(0f, y70),
            size = Size(chartWidth, y30 - y70)
        )

        // 70 Line
        drawLine(
            color = BoldColors.AccentBearish.copy(alpha = 0.6f),
            start = Offset(0f, y70),
            end = Offset(chartWidth, y70),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "70 OB",
            topLeft = Offset(chartWidth + 12f, y70 - 7.sp.toPx()),
            style = TextStyle(color = BoldColors.AccentBearish, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        )

        // 50 Centerline
        drawLine(
            color = BoldColors.BorderSubtle.copy(alpha = 0.4f),
            start = Offset(0f, y50),
            end = Offset(chartWidth, y50),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 4f), 0f)
        )

        // 30 Line
        drawLine(
            color = BoldColors.PrimaryBullish.copy(alpha = 0.6f),
            start = Offset(0f, y30),
            end = Offset(chartWidth, y30),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "30 OS",
            topLeft = Offset(chartWidth + 12f, y30 - 7.sp.toPx()),
            style = TextStyle(color = BoldColors.PrimaryBullish, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        )

        // Draw RSI Path
        val stepX = chartWidth / (rsiValues.size - 1)
        val path = Path()
        var hasStarted = false

        rsiValues.forEachIndexed { i, value ->
            if (value != null) {
                val x = i * stepX
                val y = rsiToY(value)
                if (!hasStarted) {
                    path.moveTo(x, y)
                    hasStarted = true
                } else {
                    path.lineTo(x, y)
                }
            }
        }

        if (hasStarted) {
            drawPath(
                path = path,
                color = Color(0xFFA78BFA), // Purple-400
                style = Stroke(width = 2f)
            )
        }

        // Current value badge
        val lastVal = rsiValues.lastOrNull { it != null }
        if (lastVal != null) {
            drawText(
                textMeasurer = textMeasurer,
                text = String.format("RSI: %.1f", lastVal),
                topLeft = Offset(10f, 6f),
                style = TextStyle(
                    color = Color(0xFFA78BFA),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}
