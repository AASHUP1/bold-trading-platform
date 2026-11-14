package com.bold.trading.feature.charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.charts.canvas.CandlestickCanvas
import com.bold.trading.core.charts.canvas.RsiSubChartCanvas
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.charts.ChartInterval
import com.bold.trading.feature.charts.ChartViewModel
import java.math.BigDecimal

/**
 * Screen S07: Advanced Full-Screen Candlestick Chart with Indicators & Crosshair Inspection.
 * Follows DESIGN.md Section 6: S07 & PLAN.md Phase 4.
 */
@Composable
fun ChartScreen(
    symbol: String,
    viewModel: ChartViewModel,
    onBack: () -> Unit,
    onSimulateTrade: (CachedInstrumentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadInstrument(symbol)
    }

    val inst = state.instrument
    val isBull = (inst?.changePercent ?: BigDecimal.ZERO) >= BigDecimal.ZERO
    val changeColor = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(top = 8.dp)
    ) {
        // Watermark Banner mandated by PRD
        WatermarkBanner(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BoldColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = inst?.symbol ?: symbol,
                            color = BoldColors.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(
                            text = inst?.exchange ?: "NSE",
                            status = BadgeStatus.Neutral
                        )
                    }
                    Text(
                        text = "${inst?.name ?: "Indian Benchmark"} · ${inst?.sector ?: "Core"}",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (inst != null) CurrencyFormatter.formatRupees(inst.lastTradedPrice) else "--",
                    color = BoldColors.TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (inst != null) CurrencyFormatter.formatPercentage(inst.changePercent) else "--",
                    color = changeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interval Chips Row (1m, 5m, 15m, 1H, 1D)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ChartInterval.entries.forEach { interval ->
                val isSelected = state.selectedInterval == interval
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { viewModel.onIntervalSelected(interval) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = interval.label,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Technical Indicator Toggles Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IndicatorChip("EMA 20", state.showEma20, BoldColors.AccentWarning) { viewModel.toggleEma20() }
            IndicatorChip("EMA 50", state.showEma50, Color(0xFF60A5FA)) { viewModel.toggleEma50() }
            IndicatorChip("VWAP", state.showVwap, Color(0xFFC084FC)) { viewModel.toggleVwap() }
            IndicatorChip("RSI 14", state.showRsi, Color(0xFFA78BFA)) { viewModel.toggleRsi() }
            IndicatorChip("VOL", state.showVolume, BoldColors.TextSecondary) { viewModel.toggleVolume() }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // OHLCV Metrics Inspection Bar
        state.inspectedCandle?.let { candle ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(BoldColors.SurfaceCard, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OhlcMetric("O", candle.open)
                OhlcMetric("H", candle.high)
                OhlcMetric("L", candle.low)
                OhlcMetric("C", candle.close, candle.isBullish)
                Text(
                    text = "V: ${CurrencyFormatter.formatCompact(candle.volume)}",
                    color = BoldColors.TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main Candlestick Chart Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BoldColors.PrimaryBullish, strokeWidth = 2.dp)
                }
            } else {
                CandlestickCanvas(
                    candles = state.candles,
                    ema20 = state.ema20,
                    ema50 = state.ema50,
                    vwap = state.vwap,
                    showEma20 = state.showEma20,
                    showEma50 = state.showEma50,
                    showVwap = state.showVwap,
                    showVolume = state.showVolume,
                    onCrosshairChange = { viewModel.onCrosshairCandle(it) }
                )
            }
        }

        // Sub-Chart: RSI 14 Momentum
        if (state.showRsi && state.rsi14.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(horizontal = 8.dp)
            ) {
                RsiSubChartCanvas(rsiValues = state.rsi14)
            }
        }

        // Bottom CTA: "Simulate Trade" (S14 Entry Point)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoldColors.SurfaceCard)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            BoldButton(
                text = "Simulate Trade (${inst?.symbol ?: symbol})",
                onClick = { inst?.let { onSimulateTrade(it) } },
                variant = BoldButtonVariant.Primary,
                enabled = inst != null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun IndicatorChip(
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.15f) else BoldColors.SurfaceCard)
            .border(1.dp, if (isActive) activeColor else BoldColors.BorderSubtle, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isActive) activeColor else BoldColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun OhlcMetric(label: String, value: Float, isBull: Boolean? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label: ", color = BoldColors.TextMuted, fontSize = 10.sp)
        Text(
            text = String.format("%.2f", value),
            color = when (isBull) {
                true -> BoldColors.PrimaryBullish
                false -> BoldColors.AccentBearish
                null -> BoldColors.TextPrimary
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
