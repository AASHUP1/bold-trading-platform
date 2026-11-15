package com.bold.trading.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import java.math.BigDecimal

@Composable
fun HomeScreen(
    instrumentDao: InstrumentDao,
    paperAccountDao: PaperAccountDao,
    modifier: Modifier = Modifier
) {
    // Collect reactive Flow streams directly from Room Database
    val instruments by instrumentDao.getAllInstruments().collectAsState(initial = emptyList())
    val paperAccount by paperAccountDao.getPaperAccount().collectAsState(initial = null)

    val nifty = instruments.find { it.symbol == "NIFTY 50" }
    val bankNifty = instruments.find { it.symbol == "BANKNIFTY" }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WatermarkBanner(
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Virtual Portfolio Card
            item {
                BoldCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = BoldColors.PrimaryBullish.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VIRTUAL CAPITAL (PAPER LEDGER)",
                            color = BoldColors.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        StatusBadge(text = "ACTIVE", status = BadgeStatus.Healthy)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = CurrencyFormatter.formatInr(paperAccount?.virtualCapital ?: BigDecimal("1000000.00")),
                        color = BoldColors.TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Available Cash", color = BoldColors.TextMuted, fontSize = 11.sp)
                            Text(
                                text = CurrencyFormatter.formatInr(paperAccount?.availableCash ?: BigDecimal("1000000.00")),
                                color = BoldColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Realized P&L", color = BoldColors.TextMuted, fontSize = 11.sp)
                            Text(
                                text = CurrencyFormatter.formatInr(paperAccount?.realizedPnl ?: BigDecimal.ZERO),
                                color = BoldColors.PrimaryBullish,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Market Benchmark Indices Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // NIFTY 50 Index Card
                    BoldCard(modifier = Modifier.weight(1f)) {
                        Text(text = "NIFTY 50", color = BoldColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nifty?.lastTradedPrice?.toPlainString() ?: "24,360.50",
                            color = BoldColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = CurrencyFormatter.formatPercentage(nifty?.changePercent ?: BigDecimal("0.65")),
                            color = if ((nifty?.changePercent ?: BigDecimal.ZERO) >= BigDecimal.ZERO) BoldColors.PrimaryBullish else BoldColors.AccentBearish,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // BANK NIFTY Index Card
                    BoldCard(modifier = Modifier.weight(1f)) {
                        Text(text = "BANKNIFTY", color = BoldColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bankNifty?.lastTradedPrice?.toPlainString() ?: "51,820.00",
                            color = BoldColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = CurrencyFormatter.formatPercentage(bankNifty?.changePercent ?: BigDecimal("0.42")),
                            color = if ((bankNifty?.changePercent ?: BigDecimal.ZERO) >= BigDecimal.ZERO) BoldColors.PrimaryBullish else BoldColors.AccentBearish,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Watchlist Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE WATCHLIST (ROOM DB)",
                        color = BoldColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Real-time Ticks ●",
                        color = BoldColors.PrimaryBullish,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Dynamic Watchlist Items (Flow from Room SQLite)
            items(instruments, key = { it.symbol }) { inst ->
                val isBull = inst.changePercent >= BigDecimal.ZERO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BoldColors.SurfaceCard, CircleShape)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = inst.symbol,
                            color = BoldColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = inst.name,
                            color = BoldColors.TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = CurrencyFormatter.formatInr(inst.lastTradedPrice),
                            color = BoldColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = CurrencyFormatter.formatPercentage(inst.changePercent),
                            color = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
