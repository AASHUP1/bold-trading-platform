package com.bold.trading.feature.papertrading.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.bold.trading.core.database.entity.CachedPaperPositionEntity
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.papertrading.PaperTab
import com.bold.trading.feature.papertrading.PaperTerminalViewModel
import java.math.BigDecimal

/**
 * Screen S14 & S16: Paper Trading Terminal & Virtual Ledger Journal.
 * Follows DESIGN.md Section 6: S14, S16 & PLAN.md Phase 5.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperTerminalScreen(
    viewModel: PaperTerminalViewModel,
    onNavigateToWatchlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(horizontal = 16.dp)
    ) {
        WatermarkBanner(modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(12.dp))

        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PAPER TRADING TERMINAL",
                    color = BoldColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Simulated Execution & Isolated Ledger",
                    color = BoldColors.TextSecondary,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(text = "100% Free", status = BadgeStatus.Healthy)
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { viewModel.openResetDialog() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset Capital",
                        tint = BoldColors.TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Virtual Capital Account Summary Card
        val account = state.account
        val availCash = account?.availableCash ?: BigDecimal("1000000.00")
        val marginUsed = account?.marginUsed ?: BigDecimal.ZERO
        val realizedPnl = account?.realizedPnl ?: BigDecimal.ZERO
        val isRealizedBull = realizedPnl >= BigDecimal.ZERO

        BoldCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BoldColors.BorderSubtle
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AVAILABLE VIRTUAL CASH",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Realized P&L",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = CurrencyFormatter.formatRupees(availCash),
                        color = BoldColors.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "${if (isRealizedBull) "+" else ""}${CurrencyFormatter.formatRupees(realizedPnl)}",
                        color = if (isRealizedBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BoldColors.SurfaceElevated, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Margin in Use: ${CurrencyFormatter.formatRupees(marginUsed)}",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "Risk Cap: 1.0% / Trade",
                        color = BoldColors.PrimaryBullish,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Selector Row: Active Positions vs Ledger Journal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaperTab.entries.forEach { tab ->
                val isSelected = state.selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.onTabSelected(tab) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Contents
        if (state.selectedTab == PaperTab.POSITIONS) {
            if (state.positions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        StatusBadge(text = "No Open Positions", status = BadgeStatus.Neutral)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "You currently hold 0 open simulated positions.",
                            color = BoldColors.TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BoldButton(
                            text = "Explore Watchlists & Trade",
                            onClick = onNavigateToWatchlist,
                            variant = BoldButtonVariant.Primary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.positions, key = { it.instrumentId }) { pos ->
                        PositionCard(
                            position = pos,
                            onSquareOff = { viewModel.squareOffPosition(pos) }
                        )
                    }
                }
            }
        } else {
            // Ledger Entries List
            if (state.ledgerEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No virtual ledger transactions recorded.",
                        color = BoldColors.TextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.ledgerEntries, key = { it.id }) { entry ->
                        LedgerEntryRow(entry = entry)
                    }
                }
            }
        }
    }

    // Order Ticket Bottom Sheet
    state.activeTicketInstrument?.let { inst ->
        OrderTicketBottomSheet(
            instrument = inst,
            availableCash = state.account?.availableCash ?: BigDecimal("1000000.00"),
            virtualCapital = state.account?.virtualCapital ?: BigDecimal("1000000.00"),
            onDismiss = { viewModel.closeOrderTicket() },
            onSubmitOrder = { isBuy, orderType, quantity, price, stopLoss, target ->
                viewModel.executeSimulatedOrder(inst, isBuy, orderType, quantity, price, stopLoss, target)
            }
        )
    }

    // Order Filled Confirmation Dialog
    state.lastFillReceipt?.let { receipt ->
        OrderFilledDialog(
            receipt = receipt,
            onDismiss = { viewModel.dismissReceipt() },
            onViewPositions = {
                viewModel.dismissReceipt()
                viewModel.onTabSelected(PaperTab.POSITIONS)
            }
        )
    }

    // Account Reset Confirmation Dialog
    if (state.isResetDialogOpen) {
        BasicAlertDialog(
            onDismissRequest = { viewModel.closeResetDialog() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BoldColors.SurfaceCard)
                    .border(1.dp, BoldColors.AccentBearish.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RESET VIRTUAL ACCOUNT",
                    color = BoldColors.AccentBearish,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "This will liquidate all active positions and reset your virtual capital ledger to the original ₹10,00,000.00 allocation.",
                    color = BoldColors.TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BoldButton(
                        text = "Cancel",
                        onClick = { viewModel.closeResetDialog() },
                        variant = BoldButtonVariant.Outlined,
                        modifier = Modifier.weight(1f)
                    )
                    BoldButton(
                        text = "Confirm Reset",
                        onClick = { viewModel.resetAccount() },
                        variant = BoldButtonVariant.Destructive,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PositionCard(
    position: CachedPaperPositionEntity,
    onSquareOff: () -> Unit
) {
    val isLong = position.quantity > 0
    val pnl = (position.currentLtp - position.averageEntryPrice).multiply(BigDecimal(position.quantity))
    val isBull = pnl >= BigDecimal.ZERO

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = position.symbol,
                        color = BoldColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(
                        text = if (isLong) "LONG ${position.quantity}" else "SHORT ${kotlin.math.abs(position.quantity)}",
                        status = if (isLong) BadgeStatus.Healthy else BadgeStatus.Degraded
                    )
                }

                Text(
                    text = "${if (isBull) "+" else ""}${CurrencyFormatter.formatRupees(pnl)}",
                    color = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Avg: ${CurrencyFormatter.formatRupees(position.averageEntryPrice)}",
                    color = BoldColors.TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "LTP: ${CurrencyFormatter.formatRupees(position.currentLtp)}",
                    color = BoldColors.TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            BoldButton(
                text = "Square Off Position",
                onClick = onSquareOff,
                variant = BoldButtonVariant.Outlined,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LedgerEntryRow(entry: LedgerEntryEntity) {
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.description,
                color = BoldColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Type: ${entry.entryType} · Balance: ${CurrencyFormatter.formatRupees(entry.balanceAfter)}",
                color = BoldColors.TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Text(
            text = CurrencyFormatter.formatRupees(entry.amount),
            color = BoldColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
