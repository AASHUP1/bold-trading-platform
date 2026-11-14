package com.bold.trading.feature.algo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.algo.model.AlgoStrategy
import com.bold.trading.feature.algo.model.ConditionBlock

/**
 * Screen S12: Visual Quantitative Strategy Rule Builder.
 * Follows DESIGN.md Section 6: S12 & PLAN.md Task 7.2.
 */
@Composable
fun StrategyBuilderScreen(
    strategy: AlgoStrategy,
    isBacktesting: Boolean,
    onTimeframeChanged: (String) -> Unit,
    onAddCondition: () -> Unit,
    onRemoveCondition: (String) -> Unit,
    onRunBacktest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Strategy Name & Timeframe Card
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
                        text = strategy.name,
                        color = BoldColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    StatusBadge(text = "Quant Lab", status = BadgeStatus.Healthy)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = strategy.description,
                    color = BoldColors.TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "EXECUTION TIMEFRAME",
                    color = BoldColors.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("5m", "15m", "1H", "1D").forEach { tf ->
                        val isSelected = strategy.timeframe == tf
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onTimeframeChanged(tf) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tf,
                                color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Trigger Conditions Block List
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ENTRY TRIGGER CONDITIONS (ALL MUST MATCH)",
                    color = BoldColors.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "+ Add Condition",
                    color = BoldColors.PrimaryBullish,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onAddCondition() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                strategy.conditions.forEachIndexed { index, cond ->
                    ConditionItemCard(
                        index = index + 1,
                        condition = cond,
                        onDelete = { onRemoveCondition(cond.id) }
                    )
                }
            }
        }

        // Risk Parameters Summary Card
        BoldCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BoldColors.BorderSubtle
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "DEFENSIVE RISK PARAMETERS",
                    color = BoldColors.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Target Return", color = BoldColors.TextSecondary, fontSize = 11.sp)
                        Text("+${strategy.targetPercent}%", color = BoldColors.PrimaryBullish, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text("Stop Loss Budget", color = BoldColors.TextSecondary, fontSize = 11.sp)
                        Text("-${strategy.stopLossPercent}%", color = BoldColors.AccentBearish, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text("Risk : Reward", color = BoldColors.TextSecondary, fontSize = 11.sp)
                        Text("1 : 2.0", color = BoldColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Backtest Action Button
        BoldButton(
            text = if (isBacktesting) "Simulating 250 Historical Bars..." else "Run Historical Backtest (250 Bars)",
            onClick = onRunBacktest,
            variant = BoldButtonVariant.Primary,
            enabled = !isBacktesting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConditionItemCard(
    index: Int,
    condition: ConditionBlock,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BoldColors.SurfaceCard)
            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BoldColors.SurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$index",
                    color = BoldColors.PrimaryBullish,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = condition.indicatorA,
                        color = BoldColors.TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = condition.operator,
                        color = BoldColors.AccentWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = condition.indicatorB,
                    color = BoldColors.TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove",
                tint = BoldColors.TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
