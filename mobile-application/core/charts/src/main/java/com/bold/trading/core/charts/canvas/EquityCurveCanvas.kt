package com.bold.trading.core.charts.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.theme.BoldColors
import kotlin.math.max

/**
 * Institutional Historical Backtest Equity Curve Canvas.
 * Plots cumulative portfolio capital over 250 trade bars with baseline and drawdown zones.
 * Follows DESIGN.md Section 6: S13 & PLAN.md Task 7.3.
 */
@Composable
fun EquityCurveCanvas(
    equityPoints: List<Float>,
    initialCapital: Float = 1_000_000f,
    modifier: Modifier = Modifier
) {
    if (equityPoints.size < 2) return

    val textMeasurer = rememberTextMeasurer()
    val rightMargin = 140f

    Box(
        modifier = modifier
            .fillMaxSize()
            .height(200.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartWidth = size.width - rightMargin
            val chartHeight = size.height

            val minEquity = minOf(initialCapital * 0.95f, equityPoints.minOrNull() ?: initialCapital)
            val maxEquity = max(initialCapital * 1.05f, equityPoints.maxOrNull() ?: initialCapital)
            val span = if (maxEquity - minEquity == 0f) 1f else maxEquity - minEquity

            fun equityToY(eq: Float): Float {
                val pad = chartHeight * 0.08f
                val h = chartHeight - (pad * 2)
                val norm = (eq - minEquity) / span
                return pad + (h * (1f - norm))
            }

            fun indexToX(idx: Int): Float {
                return (idx.toFloat() / (equityPoints.size - 1)) * chartWidth
            }

            val baseY = equityToY(initialCapital)

            // 1. Draw Baseline (Initial ₹10,00,000)
            drawLine(
                color = BoldColors.BorderSubtle,
                start = Offset(0f, baseY),
                end = Offset(chartWidth, baseY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )

            drawText(
                textMeasurer = textMeasurer,
                text = "Base: ₹10.0L",
                topLeft = Offset(chartWidth + 10f, baseY - 7.sp.toPx()),
                style = TextStyle(
                    color = BoldColors.TextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            )

            // 2. Draw Curve and Gradient Fill
            val strokePath = Path()
            val fillPath = Path()

            equityPoints.forEachIndexed { i, eq ->
                val x = indexToX(i)
                val y = equityToY(eq)

                if (i == 0) {
                    strokePath.moveTo(x, y)
                    fillPath.moveTo(x, chartHeight)
                    fillPath.lineTo(x, y)
                } else {
                    strokePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(chartWidth, chartHeight)
            fillPath.close()

            // Gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BoldColors.PrimaryBullish.copy(alpha = 0.22f),
                        Color.Transparent
                    )
                )
            )

            // Line
            drawPath(
                path = strokePath,
                color = BoldColors.PrimaryBullish,
                style = Stroke(
                    width = 2.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 3. Draw Peak and Final Value tags
            val finalEquity = equityPoints.last()
            val finalY = equityToY(finalEquity)

            drawCircle(
                color = BoldColors.PrimaryBullish,
                radius = 4.dp.toPx(),
                center = Offset(chartWidth, finalY)
            )

            val finalEquityText = String.format("₹%.1fL", finalEquity / 100_000f)
            drawText(
                textMeasurer = textMeasurer,
                text = finalEquityText,
                topLeft = Offset(chartWidth + 10f, finalY - 7.sp.toPx()),
                style = TextStyle(
                    color = BoldColors.PrimaryBullish,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}
