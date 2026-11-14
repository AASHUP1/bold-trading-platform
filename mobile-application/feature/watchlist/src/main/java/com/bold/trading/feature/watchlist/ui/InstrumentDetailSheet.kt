package com.bold.trading.feature.watchlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import java.math.BigDecimal

/**
 * Screen S06 Detail Sheet: Expanded quotes, 52W Range, Day High/Low, and Quick Execution.
 * Follows DESIGN.md Section 5.4 & Section 6: S06
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentDetailSheet(
    instrument: CachedInstrumentEntity,
    onDismiss: () -> Unit,
    onSimulateTrade: (CachedInstrumentEntity) -> Unit,
    onOpenChart: (CachedInstrumentEntity) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val isBullish = instrument.changePercent >= BigDecimal.ZERO
    val changeColor = if (isBullish) BoldColors.PrimaryBullish else BoldColors.AccentBearish

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BoldColors.SurfaceCard,
        contentColor = BoldColors.TextPrimary,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BoldColors.TextMuted)
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            WatermarkBanner()
            Spacer(modifier = Modifier.height(16.dp))

            // Symbol Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = instrument.symbol,
                            color = BoldColors.TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(
                            text = instrument.exchange,
                            status = BadgeStatus.Neutral
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${instrument.name} · ${instrument.sector}",
                        color = BoldColors.TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatRupees(instrument.ltp),
                        color = BoldColors.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${CurrencyFormatter.formatRupees(instrument.changeAmount)} (${CurrencyFormatter.formatPercentage(instrument.changePercent)})",
                        color = changeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Intraday Range & 52-Week Range
            BoldCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BoldColors.BorderSubtle
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(label = "Day Low", value = CurrencyFormatter.formatRupees(instrument.low24h))
                        MetricItem(label = "Day High", value = CurrencyFormatter.formatRupees(instrument.high24h), alignEnd = true)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Range Bar
                    val low = instrument.low24h.toFloat()
                    val high = instrument.high24h.toFloat()
                    val cur = instrument.ltp.toFloat()
                    val ratio = if (high > low) ((cur - low) / (high - low)).coerceIn(0f, 1f) else 0.5f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(BoldColors.BorderSubtle)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .height(4.dp)
                                .background(BoldColors.PrimaryBullish)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(label = "Volume (24h)", value = CurrencyFormatter.formatCompact(instrument.volume24h))
                        MetricItem(label = "Lot Size", value = "${instrument.lotSize}", alignEnd = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BoldButton(
                    text = "Open Chart",
                    onClick = { onOpenChart(instrument) },
                    variant = BoldButtonVariant.Outlined,
                    modifier = Modifier.weight(1f)
                )

                BoldButton(
                    text = "Simulate Trade",
                    onClick = { onSimulateTrade(instrument) },
                    variant = BoldButtonVariant.Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    alignEnd: Boolean = false
) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(
            text = label,
            color = BoldColors.TextSecondary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = BoldColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
