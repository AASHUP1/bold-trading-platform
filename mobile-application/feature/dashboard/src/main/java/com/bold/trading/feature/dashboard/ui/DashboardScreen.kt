package com.bold.trading.feature.dashboard.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.SparklineCanvas
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.dashboard.DashboardUiState
import com.bold.trading.feature.dashboard.DashboardViewModel
import java.math.BigDecimal

/**
 * Screen S04: Home Dashboard Shell & Index Carousel.
 * Follows DESIGN.md Section 6: S04 & PLAN.md Task 3.1.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateTab: (String) -> Unit,
    onInstrumentClick: (CachedInstrumentEntity) -> Unit,
    onSearchClick: () -> Unit,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BoldColors.SurfaceElevated)
                        .border(1.dp, BoldColors.BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BD",
                        color = BoldColors.PrimaryBullish,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "BOLD TERMINAL",
                        color = BoldColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "NSE / BSE Institutional Desk",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(
                    text = "${state.feedLatencyMs}ms",
                    status = if (state.isFeedHealthy) BadgeStatus.Healthy else BadgeStatus.Degraded
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = BoldColors.TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Market Indices Carousel
            item {
                Column {
                    Text(
                        text = "MARKET BENCHMARKS",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.nifty?.let { index ->
                            IndexCard(
                                instrument = index,
                                onClick = { onInstrumentClick(index) }
                            )
                        }
                        state.bankNifty?.let { index ->
                            IndexCard(
                                instrument = index,
                                onClick = { onInstrumentClick(index) }
                            )
                        }
                        state.sensex?.let { index ->
                            IndexCard(
                                instrument = index,
                                onClick = { onInstrumentClick(index) }
                            )
                        }
                    }
                }
            }

            // 2. Virtual Paper Portfolio Card
            item {
                BoldCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = BoldColors.PrimaryBullish.copy(alpha = 0.35f)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = BoldColors.PrimaryBullish,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Virtual Equity Ledger",
                                    color = BoldColors.TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            StatusBadge(text = "Paper SIM", status = BadgeStatus.Healthy)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val balance = state.paperAccount?.virtualCashBalance ?: BigDecimal("1000000.00")
                        val pnl = state.paperAccount?.realizedPnl ?: BigDecimal("24500.00")
                        val isPnlBullish = pnl >= BigDecimal.ZERO

                        Text(
                            text = CurrencyFormatter.formatRupees(balance),
                            color = BoldColors.TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Today's Unrealized: ",
                                color = BoldColors.TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "+₹24,500.00 (+2.45%)",
                                color = BoldColors.PrimaryBullish,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BoldColors.SurfaceElevated, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Defensive Guardrail: Max 1.0% Risk / Trade",
                                color = BoldColors.TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Active",
                                color = BoldColors.PrimaryBullish,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Quick Action Pills (Scan, AI Research, Algo Lab, Paper Trade)
            item {
                Column {
                    Text(
                        text = "TERMINAL ACTIONS",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionPill(
                            title = "Scan",
                            icon = Icons.Filled.Search,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab("scanner") }
                        )
                        ActionPill(
                            title = "Trade Desk",
                            icon = Icons.Filled.AutoGraph,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab("trade_desk") }
                        )
                        ActionPill(
                            title = "Algo Lab",
                            icon = Icons.Filled.Analytics,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab("algo_lab") }
                        )
                        ActionPill(
                            title = "Research",
                            icon = Icons.Filled.Psychology,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab("research") }
                        )
                    }
                }
            }

            // 4. Market Movers Preview
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOP MOVERS (NSE)",
                            color = BoldColors.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = "View All Watchlists",
                            color = BoldColors.PrimaryBullish,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateTab("scanner") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.topGainers.forEach { gainer ->
                            MoverMiniRow(
                                instrument = gainer,
                                onClick = { onInstrumentClick(gainer) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun IndexCard(
    instrument: CachedInstrumentEntity,
    onClick: () -> Unit
) {
    val isBull = instrument.changePercent >= BigDecimal.ZERO
    val changeColor = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish

    BoldCard(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        borderColor = BoldColors.BorderSubtle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = instrument.symbol,
                    color = BoldColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(changeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.formatPercentage(instrument.changePercent),
                        color = changeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = CurrencyFormatter.formatRupees(instrument.ltp),
                color = BoldColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${if (isBull) "+" else ""}${CurrencyFormatter.formatRupees(instrument.changeAmount)}",
                color = changeColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ActionPill(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BoldColors.SurfaceCard)
            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = BoldColors.PrimaryBullish,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = BoldColors.TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MoverMiniRow(
    instrument: CachedInstrumentEntity,
    onClick: () -> Unit
) {
    val isBull = instrument.changePercent >= BigDecimal.ZERO
    val changeColor = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BoldColors.SurfaceCard)
            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = instrument.symbol,
                color = BoldColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = instrument.sector,
                color = BoldColors.TextSecondary,
                fontSize = 11.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = CurrencyFormatter.formatRupees(instrument.ltp),
                color = BoldColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(10.dp))
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
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
