package com.bold.trading.feature.algo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.algo.model.InstitutionalSignal

/**
 * Screen S15: Institutional Real-Time Algo Signals Feed.
 * Follows DESIGN.md Section 6: S15 & PLAN.md Task 7.4.
 */
@Composable
fun SignalsFeedScreen(
    signals: List<InstitutionalSignal>,
    onCopyToTicket: (InstitutionalSignal) -> Unit,
    modifier: Modifier = Modifier
) {
    if (signals.isEmpty()) {
        Box(modifier = modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text(text = "No active algorithmic signals at this time.", color = BoldColors.TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(signals, key = { it.id }) { sig ->
            SignalCard(
                signal = sig,
                onCopy = { onCopyToTicket(sig) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SignalCard(
    signal: InstitutionalSignal,
    onCopy: () -> Unit
) {
    val isBuy = signal.direction == "BUY"

    BoldCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isBuy) BoldColors.PrimaryBullish.copy(alpha = 0.35f) else BoldColors.AccentBearish.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Signal Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = "${signal.direction} · ${signal.confidencePercent}%",
                        status = if (isBuy) BadgeStatus.Healthy else BadgeStatus.Degraded
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = signal.symbol,
                        color = BoldColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = signal.timestampText,
                    color = BoldColors.TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Model: ${signal.strategyName}",
                color = BoldColors.TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Entry Zone, SL, and Target Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BoldColors.SurfaceElevated, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Entry Zone", color = BoldColors.TextSecondary, fontSize = 10.sp)
                    Text(
                        text = "${CurrencyFormatter.formatRupees(signal.entryMin)} - ${CurrencyFormatter.formatRupees(signal.entryMax)}",
                        color = BoldColors.TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Stop Loss", color = BoldColors.TextSecondary, fontSize = 10.sp)
                    Text(
                        text = CurrencyFormatter.formatRupees(signal.stopLoss),
                        color = BoldColors.AccentBearish,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Targets row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TargetTag("T1", CurrencyFormatter.formatRupees(signal.target1))
                TargetTag("T2", CurrencyFormatter.formatRupees(signal.target2))
                TargetTag("T3", CurrencyFormatter.formatRupees(signal.target3))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            BoldButton(
                text = "Copy to Paper Order Ticket",
                onClick = onCopy,
                variant = BoldButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TargetTag(label: String, value: String) {
    Row(
        modifier = Modifier
            .background(BoldColors.SurfaceElevated, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$label: ", color = BoldColors.TextSecondary, fontSize = 10.sp)
        Text(
            text = value,
            color = BoldColors.PrimaryBullish,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
