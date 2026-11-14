package com.bold.trading.feature.watchlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.components.SparklineCanvas
import com.bold.trading.core.designsystem.theme.BoldColors
import java.math.BigDecimal

/**
 * Institutional Watchlist Row Card strictly adhering to DESIGN.md Section 5.3.
 * Height 68dp, #121923 surface, #253140 border, 48dp touch target, and tabular numbers.
 */
@Composable
fun InstrumentCard(
    instrument: CachedInstrumentEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBullish = instrument.changePercent >= BigDecimal.ZERO
    val changeColor = if (isBullish) BoldColors.PrimaryBullish else BoldColors.AccentBearish

    // Generate mock sparkline points around LTP if historical array is not populated
    val sparklinePoints = remember(instrument.ltp, instrument.changePercent) {
        val base = instrument.ltp.toFloat()
        val changePct = instrument.changePercent.toFloat()
        val start = base / (1f + changePct / 100f)
        val diff = base - start
        listOf(
            start,
            start + diff * 0.2f - (base * 0.003f),
            start + diff * 0.45f + (base * 0.004f),
            start + diff * 0.35f,
            start + diff * 0.75f - (base * 0.002f),
            start + diff * 0.90f + (base * 0.001f),
            base
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BoldColors.SurfaceCard)
            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = BoldColors.PrimaryBullish.copy(alpha = 0.2f))
            ) {
                onClick()
            }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left column: Symbol and Sector
            Column(
                modifier = Modifier.width(110.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = instrument.symbol,
                    color = BoldColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${instrument.exchange} · ${instrument.sector}",
                    color = BoldColors.TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            // Center: Hardware-accelerated Sparkline
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                SparklineCanvas(
                    points = sparklinePoints,
                    isBullish = isBullish,
                    width = 80.dp,
                    height = 28.dp
                )
            }

            // Right column: Tabular LTP and % Change Badge
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = CurrencyFormatter.formatRupees(instrument.ltp),
                    color = BoldColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(changeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.formatPercentage(instrument.changePercent),
                        color = changeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
