package com.bold.trading.feature.algo.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.charts.canvas.EquityCurveCanvas
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.algo.model.BacktestResult
import java.math.BigDecimal

/**
 * Screen S13: Historical Backtest Equity Curve Visualizer & Quant Performance.
 * Follows DESIGN.md Section 6: S13 & PLAN.md Task 7.3.
 */
@Composable
fun BacktestResultScreen(
    result: BacktestResult?,
    onBackToBuilder: () -> Unit,
    onDeployToPaper: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        Box(modifier = modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text(text = "No backtest results available. Run a test from the Rule Builder.", color = BoldColors.TextSecondary)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Performance Headline Card
        BoldCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BoldColors.BorderSubtle
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result.strategyName,
                        color = BoldColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    StatusBadge(text = "250 Bars Verified", status = BadgeStatus.Healthy)
                }

                Spacer(modifier = Modifier.height(6.dp))

                val finalEq = BigDecimal(result.finalEquity.toDouble())
                val growth = ((result.finalEquity - 1_000_000f) / 1_000_000f) * 100f

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = CurrencyFormatter.formatRupees(finalEq),
                        color = BoldColors.PrimaryBullish,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = String.format("(+%.1f%%)", growth),
                        color = BoldColors.PrimaryBullish,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }

        // Statistical Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "WIN RATE",
                value = String.format("%.1f%%", result.winRate),
                isBull = result.winRate >= 50f,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "PROFIT FACTOR",
                value = String.format("%.2f", result.profitFactor),
                isBull = result.profitFactor >= 1.5f,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "MAX DRAWDOWN",
                value = String.format("%.1f%%", result.maxDrawdown),
                isBull = false,
                modifier = Modifier.weight(1f)
            )
        }

        // Equity Curve Canvas Card
        BoldCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BoldColors.BorderSubtle
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "CUMULATIVE CAPITAL TRAJECTORY (₹10L BASELINE)",
                    color = BoldColors.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(10.dp))

                EquityCurveCanvas(
                    equityPoints = result.equityCurve,
                    initialCapital = 1_000_000f
                )
            }
        }

        // Trade Count Breakdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoldColors.SurfaceElevated, RoundedCornerShape(8.dp))
                .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Trades: ${result.totalTrades}",
                color = BoldColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${result.winningTrades} Wins · ${result.losingTrades} Losses",
                color = BoldColors.TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BoldButton(
                text = "Edit Rules",
                onClick = onBackToBuilder,
                variant = BoldButtonVariant.Outlined,
                modifier = Modifier.weight(1f)
            )

            BoldButton(
                text = "Deploy to Paper Lab",
                onClick = onDeployToPaper,
                variant = BoldButtonVariant.Primary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    isBull: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BoldColors.SurfaceCard)
            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = BoldColors.TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                color = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
