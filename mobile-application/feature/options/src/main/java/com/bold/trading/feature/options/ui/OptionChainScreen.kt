package com.bold.trading.feature.options.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.options.OptionChainViewModel
import com.bold.trading.feature.options.model.OptionStrikeRow
import com.bold.trading.feature.options.model.StrategyPreset
import com.bold.trading.feature.papertrading.ui.OrderFilledDialog

/**
 * Screen S11: Institutional Option Chain Table & Strategy Lab.
 * Follows DESIGN.md Section 5.6 & PLAN.md Phase 6.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionChainScreen(
    viewModel: OptionChainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(horizontal = 14.dp)
    ) {
        WatermarkBanner(modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(10.dp))

        // Underlying Index Switcher & Spot LTP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("NIFTY 50", "BANKNIFTY").forEach { symbol ->
                    val isSelected = state.underlyingSymbol == symbol
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectUnderlying(symbol) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbol,
                            color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatRupees(state.spotPrice),
                    color = BoldColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Spot Index",
                    color = BoldColors.TextSecondary,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Expiry Date Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            state.availableExpiries.forEach { exp ->
                val isSelected = state.selectedExpiry == exp
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { viewModel.selectExpiry(exp) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = exp,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Strategy Presets Bar
        Column {
            Text(
                text = "STRATEGY LAB PRESETS (TAP TO SIMULATE PAYOFF)",
                color = BoldColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StrategyPreset.entries.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BoldColors.SurfaceCard)
                            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
                            .clickable { viewModel.selectStrategyPreset(preset) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset.displayName,
                            color = BoldColors.PrimaryBullish,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option Chain Table Column Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoldColors.SurfaceElevated, RoundedCornerShape(6.dp))
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CALLS OI", color = BoldColors.TextSecondary, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
            Text("CALL LTP", color = BoldColors.PrimaryBullish, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("STRIKE", color = BoldColors.TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("PUT LTP", color = BoldColors.AccentBearish, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("PUTS OI", color = BoldColors.TextSecondary, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Option Chain Data Rows
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BoldColors.PrimaryBullish, strokeWidth = 2.dp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(state.strikes, key = { it.strike }) { row ->
                    OptionChainRow(
                        row = row,
                        onClick = { viewModel.selectStrategyPreset(StrategyPreset.BULL_CALL_SPREAD) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Basket Strategy Payoff Preview Bottom Sheet
    if (state.isPayoffSheetOpen && state.selectedPreset != null && state.payoffResult != null) {
        BasketOrderSheet(
            preset = state.selectedPreset!!,
            legs = state.activeStrategyLegs,
            payoff = state.payoffResult!!,
            spotPrice = state.spotPrice.toFloat(),
            lotSize = state.lotSize,
            errorMessage = state.errorMessage,
            onDismiss = { viewModel.closePayoffSheet() },
            onExecuteBasket = { viewModel.executeSimulatedBasket() }
        )
    }

    // Fill Receipt Dialog
    state.lastBasketReceipt?.let { receipt ->
        OrderFilledDialog(
            receipt = receipt,
            onDismiss = { viewModel.dismissBasketReceipt() },
            onViewPositions = { viewModel.dismissBasketReceipt() }
        )
    }
}

@Composable
private fun OptionChainRow(
    row: OptionStrikeRow,
    onClick: () -> Unit
) {
    val bgModifier = if (row.isAtm) {
        Modifier
            .background(BoldColors.SurfaceElevated, RoundedCornerShape(4.dp))
            .border(1.dp, BoldColors.AccentWarning.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
    } else {
        Modifier.background(BoldColors.SurfaceCard)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(bgModifier)
            .clickable { onClick() }
            .padding(vertical = 7.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Calls OI
        Text(
            text = CurrencyFormatter.formatCompact(row.callOi),
            color = BoldColors.TextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        // Calls LTP
        Text(
            text = CurrencyFormatter.formatRupees(row.callLtp),
            color = BoldColors.PrimaryBullish,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Center STRIKE (Highlight ATM)
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${row.strike.toInt()}",
                color = if (row.isAtm) BoldColors.AccentWarning else BoldColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Puts LTP
        Text(
            text = CurrencyFormatter.formatRupees(row.putLtp),
            color = BoldColors.AccentBearish,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Puts OI
        Text(
            text = CurrencyFormatter.formatCompact(row.putOi),
            color = BoldColors.TextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
