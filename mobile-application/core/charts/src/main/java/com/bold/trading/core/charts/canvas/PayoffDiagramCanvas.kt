package com.bold.trading.core.charts.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
import kotlin.math.min

data class PayoffPoint(
    val price: Float,
    val pnl: Float
)

/**
 * Institutional F&O Multi-Leg Payoff Diagram Canvas.
 * Plots non-linear options payoff curves across underlying expiration prices with breakeven points.
 * Follows DESIGN.md Section 5.6 & PLAN.md Task 6.2.
 */
@Composable
fun PayoffDiagramCanvas(
    points: List<PayoffPoint>,
    spotPrice: Float,
    breakevenPrices: List<Float>,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) return

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxSize()
            .height(220.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartWidth = size.width
            val chartHeight = size.height

            val minPrice = points.minOf { it.price }
            val maxPrice = points.maxOf { it.price }
            val priceSpan = if (maxPrice - minPrice == 0f) 1f else maxPrice - minPrice

            val minPnl = points.minOf { it.pnl }
            val maxPnl = points.maxOf { it.pnl }
            val pnlRange = max(1f, max(kotlin.math.abs(minPnl), kotlin.math.abs(maxPnl)) * 2.2f)

            fun priceToX(price: Float): Float {
                return ((price - minPrice) / priceSpan) * chartWidth
            }

            fun pnlToY(pnl: Float): Float {
                val centerY = chartHeight / 2f
                val scale = (chartHeight * 0.40f) / (pnlRange / 2f)
                return centerY - (pnl * scale)
            }

            val zeroY = pnlToY(0f)

            // 1. Draw Zero P&L horizontal baseline
            drawLine(
                color = BoldColors.BorderSubtle,
                start = Offset(0f, zeroY),
                end = Offset(chartWidth, zeroY),
                strokeWidth = 1.5f
            )

            // 2. Draw Spot Price vertical line
            val spotX = priceToX(spotPrice)
            if (spotX in 0f..chartWidth) {
                drawLine(
                    color = BoldColors.AccentWarning,
                    start = Offset(spotX, 0f),
                    end = Offset(spotX, chartHeight),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = String.format("Spot: ₹%.0f", spotPrice),
                    topLeft = Offset(spotX + 6f, 10f),
                    style = TextStyle(
                        color = BoldColors.AccentWarning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            // 3. Draw Breakeven markers
            breakevenPrices.forEach { bePrice ->
                val beX = priceToX(bePrice)
                if (beX in 0f..chartWidth) {
                    drawCircle(
                        color = BoldColors.TextPrimary,
                        radius = 4.dp.toPx(),
                        center = Offset(beX, zeroY)
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = String.format("BE: ₹%.0f", bePrice),
                        topLeft = Offset(beX - 24.dp.toPx(), zeroY + 8.dp.toPx()),
                        style = TextStyle(
                            color = BoldColors.TextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            // 4. Draw Profit and Loss Shaded Regions
            val profitPath = Path()
            val lossPath = Path()
            val curvePath = Path()

            var hasStarted = false
            points.forEachIndexed { i, pt ->
                val x = priceToX(pt.price)
                val y = pnlToY(pt.pnl)

                if (!hasStarted) {
                    curvePath.moveTo(x, y)
                    hasStarted = true
                } else {
                    curvePath.lineTo(x, y)
                }
            }

            // Draw full curve line
            drawPath(
                path = curvePath,
                color = BoldColors.PrimaryBullish,
                style = Stroke(width = 3f)
            )

            // 5. Draw Price Axis labels at bottom
            val stepCount = 4
            for (i in 0..stepCount) {
                val p = minPrice + (priceSpan * (i.toFloat() / stepCount))
                val x = priceToX(p)
                drawText(
                    textMeasurer = textMeasurer,
                    text = String.format("₹%.0f", p),
                    topLeft = Offset(x.coerceIn(4f, chartWidth - 60f), chartHeight - 16.sp.toPx()),
                    style = TextStyle(
                        color = BoldColors.TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}
