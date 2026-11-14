package com.bold.trading.feature.scanner.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.bold.trading.feature.scanner.ScannerViewModel
import com.bold.trading.feature.scanner.model.ScanResultItem
import com.bold.trading.feature.scanner.model.ScannerPreset
import java.math.BigDecimal

/**
 * Screen S08: Real-Time Quantitative Market Scanner.
 * Follows DESIGN.md Section 6: S08 & PLAN.md Task 7.1.
 */
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    onOpenChart: (CachedInstrumentEntity) -> Unit,
    onSimulateTrade: (CachedInstrumentEntity) -> Unit,
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

        // Scanner Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "QUANT MARKET SCANNER",
                    color = BoldColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Scanning NSE Universe (${state.totalUniverseCount} Instruments)",
                    color = BoldColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
            StatusBadge(text = "${state.matchedResults.size} Matched", status = BadgeStatus.Healthy)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScannerPreset.entries.forEach { preset ->
                val isSelected = state.selectedPreset == preset
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.onPresetSelected(preset) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preset.displayName,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Preset Formula Card
        BoldCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BoldColors.BorderSubtle
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = BoldColors.PrimaryBullish,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.selectedPreset.ruleDescription,
                    color = BoldColors.TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Matched Scan Results
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BoldColors.PrimaryBullish, strokeWidth = 2.dp)
            }
        } else if (state.matchedResults.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No instruments currently meet '${state.selectedPreset.displayName}' criteria.",
                    color = BoldColors.TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.matchedResults, key = { it.instrument.instrumentId }) { item ->
                    ScanResultRow(
                        item = item,
                        onOpenChart = { onOpenChart(item.instrument) },
                        onSimulateTrade = { onSimulateTrade(item.instrument) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ScanResultRow(
    item: ScanResultItem,
    onOpenChart: () -> Unit,
    onSimulateTrade: () -> Unit
) {
    val inst = item.instrument
    val isBull = inst.changePercent >= BigDecimal.ZERO
    val changeColor = if (isBull) BoldColors.PrimaryBullish else BoldColors.AccentBearish

    BoldCard(
        modifier = Modifier.fillMaxWidth(),
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
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = inst.symbol,
                            color = BoldColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(text = inst.exchange, status = BadgeStatus.Neutral)
                    }
                    Text(
                        text = "${inst.name} · ${inst.sector}",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatRupees(inst.lastTradedPrice),
                        color = BoldColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = CurrencyFormatter.formatPercentage(inst.changePercent),
                        color = changeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Criteria Checklist Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.checklist.forEach { check ->
                    Row(
                        modifier = Modifier
                            .background(BoldColors.SurfaceElevated, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = BoldColors.PrimaryBullish,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = check,
                            color = BoldColors.TextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BoldButton(
                    text = "Chart",
                    onClick = onOpenChart,
                    variant = BoldButtonVariant.Outlined,
                    modifier = Modifier.weight(1f)
                )

                BoldButton(
                    text = "Simulate Trade",
                    onClick = onSimulateTrade,
                    variant = BoldButtonVariant.Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
