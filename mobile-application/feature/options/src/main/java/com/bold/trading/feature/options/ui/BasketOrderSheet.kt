package com.bold.trading.feature.options.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.bold.trading.core.charts.canvas.PayoffDiagramCanvas
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.options.model.OptionLeg
import com.bold.trading.feature.options.model.StrategyPayoffResult
import com.bold.trading.feature.options.model.StrategyPreset
import java.math.BigDecimal

/**
 * Screen S11: Multi-Leg Option Strategy Payoff Preview & Basket Execution Sheet.
 * Follows DESIGN.md Section 5.6 & PLAN.md Task 6.2, 6.3.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketOrderSheet(
    preset: StrategyPreset,
    legs: List<OptionLeg>,
    payoff: StrategyPayoffResult,
    spotPrice: Float,
    lotSize: Int,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onExecuteBasket: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
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
                .verticalScroll(rememberScrollState())
        ) {
            WatermarkBanner()
            Spacer(modifier = Modifier.height(14.dp))

            // Strategy Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = preset.displayName.uppercase(),
                        color = BoldColors.TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${legs.size} Legs · Lot Size: $lotSize",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }
                StatusBadge(text = "Multi-Leg Spread", status = BadgeStatus.Healthy)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-Leg Strategy Definition List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BoldColors.SurfaceElevated, RoundedCornerShape(8.dp))
                    .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                legs.forEachIndexed { idx, leg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(
                                text = if (leg.isBuy) "BUY" else "SELL",
                                status = if (leg.isBuy) BadgeStatus.Healthy else BadgeStatus.Degraded
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${leg.strike.toInt()} ${if (leg.isCall) "CALL" else "PUT"}",
                                color = BoldColors.TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "${leg.quantity} Lot @ ${CurrencyFormatter.formatRupees(leg.premium)}",
                            color = BoldColors.TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payoff Curve Visualizer
            Text(
                text = "EXPIRATION PAYOFF PROFILE",
                color = BoldColors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            BoldCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BoldColors.BorderSubtle
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    PayoffDiagramCanvas(
                        points = payoff.points,
                        spotPrice = spotPrice,
                        breakevenPrices = payoff.breakevens
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Strategy Risk / Reward Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem("Max Profit", if (payoff.maxProfit != null) "+₹${payoff.maxProfit}" else "Unlimited", isBull = true)
                        MetricItem("Max Loss", if (payoff.maxLoss != null) "-₹${payoff.maxLoss}" else "Unlimited", isBull = false)
                        MetricItem("Net Capital", "${if (payoff.isNetDebit) "Debit" else "Credit"} ₹${payoff.netDebitCredit}")
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = BoldColors.AccentBearish,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Execution CTA
            BoldButton(
                text = "Execute Simulated Basket (${preset.displayName})",
                onClick = onExecuteBasket,
                variant = BoldButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    isBull: Boolean? = null
) {
    Column {
        Text(text = label, color = BoldColors.TextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = when (isBull) {
                true -> BoldColors.PrimaryBullish
                false -> BoldColors.AccentBearish
                null -> BoldColors.TextPrimary
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
