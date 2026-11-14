package com.bold.trading.core.charts.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.charts.model.CandleModel
import com.bold.trading.core.designsystem.theme.BoldColors
import kotlin.math.max
import kotlin.math.min

/**
 * High-performance Hardware-Accelerated Candlestick Canvas.
 * Supports pan, zoom, technical overlay lines (EMA 20, EMA 50, VWAP), volume bars, and crosshair inspection.
 */
@Composable
fun CandlestickCanvas(
    candles: List<CandleModel>,
    modifier: Modifier = Modifier,
    ema20: List<Float?>? = null,
    ema50: List<Float?>? = null,
    vwap: List<Float?>? = null,
    showEma20: Boolean = true,
    showEma50: Boolean = true,
    showVwap: Boolean = false,
    showVolume: Boolean = true,
    onCrosshairChange: ((CandleModel?) -> Unit)? = null
) {
    if (candles.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()

    var candleWidth by remember { mutableFloatStateOf(18f) }
    var scrollOffset by remember { mutableFloatStateOf(0f) }
    var crosshairOffset by remember { mutableStateOf<Offset?>(null) }

    val candleSpacing = 4f
    val priceAxisWidth = 140f

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(candles.size) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Zoom updates candle width
                    candleWidth = (candleWidth * zoom).coerceIn(6f, 60f)
                    // Pan updates horizontal scroll
                    val maxScroll = max(0f, (candles.size * (candleWidth + candleSpacing)) - (size.width - priceAxisWidth))
                    scrollOffset = (scrollOffset - pan.x).coerceIn(0f, maxScroll)
                }
            }
            .pointerInput(candles.size) {
                detectDragGestures(
                    onDragStart = { offset ->
                        crosshairOffset = offset
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        crosshairOffset = change.position
                    },
                    onDragEnd = {
                        crosshairOffset = null
                        onCrosshairChange?.invoke(null)
                    },
                    onDragCancel = {
                        crosshairOffset = null
                        onCrosshairChange?.invoke(null)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartWidth = size.width - priceAxisWidth
            val chartHeight = size.height
            val totalCandleStep = candleWidth + candleSpacing

            val startIndex = (scrollOffset / totalCandleStep).toInt().coerceIn(0, max(0, candles.size - 1))
            val visibleCount = ((chartWidth / totalCandleStep) + 2).toInt()
            val endIndex = min(candles.size, startIndex + visibleCount)

            if (startIndex >= endIndex) return@Canvas

            val visibleCandles = candles.subList(startIndex, endIndex)
            val minPrice = visibleCandles.minOfOrNull { it.low } ?: 0f
            val maxPrice = visibleCandles.maxOfOrNull { it.high } ?: 1f
            val priceRange = if (maxPrice - minPrice == 0f) 1f else maxPrice - minPrice

            val maxVolume = if (showVolume) visibleCandles.maxOfOrNull { it.volume } ?: 1L else 1L
            val volumeHeightMax = chartHeight * 0.18f

            fun priceToY(price: Float): Float {
                val availableHeight = chartHeight * (if (showVolume) 0.80f else 0.94f)
                val topPadding = chartHeight * 0.03f
                val normalized = (price - minPrice) / priceRange
                return topPadding + (availableHeight * (1f - normalized))
            }

            // 1. Draw horizontal price grid lines and labels
            val gridSteps = 5
            for (i in 0..gridSteps) {
                val gridPrice = minPrice + (priceRange * (i.toFloat() / gridSteps))
                val y = priceToY(gridPrice)

                drawLine(
                    color = BoldColors.BorderSubtle.copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Price text on right margin
                val priceText = String.format("₹%.2f", gridPrice)
                drawText(
                    textMeasurer = textMeasurer,
                    text = priceText,
                    topLeft = Offset(chartWidth + 12f, y - 8.sp.toPx()),
                    style = TextStyle(
                        color = BoldColors.TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            // 2. Draw Candles and Volume Bars
            visibleCandles.forEachIndexed { i, candle ->
                val globalIndex = startIndex + i
                val x = chartWidth - ((endIndex - 1 - globalIndex) * totalCandleStep) - (candleWidth / 2f)

                if (x >= -candleWidth && x <= chartWidth + candleWidth) {
                    val isBull = candle.isBullish
                    val color = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish

                    // Volume Bar at base
                    if (showVolume && maxVolume > 0) {
                        val volHeight = (candle.volume.toFloat() / maxVolume) * volumeHeightMax
                        val volTop = chartHeight - volHeight
                        drawRect(
                            color = color.copy(alpha = 0.25f),
                            topLeft = Offset(x - (candleWidth / 2f), volTop),
                            size = Size(candleWidth, volHeight)
                        )
                    }

                    // Wick
                    val highY = priceToY(candle.high)
                    val lowY = priceToY(candle.low)
                    drawLine(
                        color = color,
                        start = Offset(x, highY),
                        end = Offset(x, lowY),
                        strokeWidth = 2f
                    )

                    // Candle Body
                    val openY = priceToY(candle.open)
                    val closeY = priceToY(candle.close)
                    val bodyTop = min(openY, closeY)
                    val bodyHeight = max(2f, kotlin.math.abs(closeY - openY))

                    drawRect(
                        color = color,
                        topLeft = Offset(x - (candleWidth / 2f), bodyTop),
                        size = Size(candleWidth, bodyHeight)
                    )
                }
            }

            // 3. Draw Overlay Indicator Lines (EMA 20, EMA 50, VWAP)
            fun drawIndicatorLine(data: List<Float?>?, color: Color, strokeWidth: Float = 2.5f) {
                if (data == null || data.size < candles.size) return
                val path = Path()
                var hasStarted = false

                for (i in startIndex until endIndex) {
                    val value = data.getOrNull(i) ?: continue
                    val x = chartWidth - ((endIndex - 1 - i) * totalCandleStep) - (candleWidth / 2f)
                    val y = priceToY(value)

                    if (!hasStarted) {
                        path.moveTo(x, y)
                        hasStarted = true
                    } else {
                        path.lineTo(x, y)
                    }
                }

                if (hasStarted) {
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = strokeWidth)
                    )
                }
            }

            if (showEma20 && ema20 != null) {
                drawIndicatorLine(ema20, BoldColors.AccentWarning, 2f)
            }
            if (showEma50 && ema50 != null) {
                drawIndicatorLine(ema50, Color(0xFF60A5FA), 2f)
            }
            if (showVwap && vwap != null) {
                drawIndicatorLine(vwap, Color(0xFFC084FC), 2f)
            }

            // 4. Crosshair Inspection Overlay
            crosshairOffset?.let { pos ->
                if (pos.x in 0f..chartWidth && pos.y in 0f..chartHeight) {
                    // Vertical guide
                    drawLine(
                        color = BoldColors.TextPrimary.copy(alpha = 0.6f),
                        start = Offset(pos.x, 0f),
                        end = Offset(pos.x, chartHeight),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    )

                    // Horizontal guide
                    drawLine(
                        color = BoldColors.TextPrimary.copy(alpha = 0.6f),
                        start = Offset(0f, pos.y),
                        end = Offset(chartWidth, pos.y),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    )

                    // Map X coordinate to nearest visible candle
                    val offsetFromRight = chartWidth - pos.x
                    val candleIndexFromEnd = (offsetFromRight / totalCandleStep).toInt()
                    val targetIndex = (endIndex - 1 - candleIndexFromEnd).coerceIn(startIndex, endIndex - 1)
                    val hoveredCandle = candles.getOrNull(targetIndex)

                    onCrosshairChange?.invoke(hoveredCandle)
                }
            }
        }
    }
}
