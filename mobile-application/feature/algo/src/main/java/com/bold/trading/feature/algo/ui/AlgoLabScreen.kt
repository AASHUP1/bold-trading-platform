package com.bold.trading.feature.algo.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.algo.AlgoLabTab
import com.bold.trading.feature.algo.AlgoLabViewModel
import com.bold.trading.feature.algo.model.InstitutionalSignal

/**
 * Screen S12, S13, S15: Algo Strategy Lab Host.
 * Follows DESIGN.md Section 6: S12, S13, S15 & PLAN.md Phase 7.
 */
@Composable
fun AlgoLabScreen(
    viewModel: AlgoLabViewModel,
    onDeployToPaper: () -> Unit,
    onCopySignalToPaper: (InstitutionalSignal) -> Unit,
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

        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "QUANT STRATEGY LAB",
                    color = BoldColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Algorithmic Builder & Historical Backtester",
                    color = BoldColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
            StatusBadge(text = "100% Free", status = BadgeStatus.Healthy)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Selector Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AlgoLabTab.entries.forEach { tab ->
                val isSelected = state.currentTab == tab
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
                        .clickable { viewModel.selectTab(tab) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab View
        Box(modifier = Modifier.weight(1f)) {
            when (state.currentTab) {
                AlgoLabTab.BUILDER -> {
                    StrategyBuilderScreen(
                        strategy = state.currentStrategy,
                        isBacktesting = state.isBacktesting,
                        onTimeframeChanged = { viewModel.updateTimeframe(it) },
                        onAddCondition = { viewModel.addCondition() },
                        onRemoveCondition = { viewModel.removeCondition(it) },
                        onRunBacktest = { viewModel.runHistoricalBacktest() }
                    )
                }
                AlgoLabTab.BACKTEST -> {
                    BacktestResultScreen(
                        result = state.backtestResult,
                        onBackToBuilder = { viewModel.selectTab(AlgoLabTab.BUILDER) },
                        onDeployToPaper = onDeployToPaper
                    )
                }
                AlgoLabTab.SIGNALS -> {
                    SignalsFeedScreen(
                        signals = state.signals,
                        onCopyToTicket = onCopySignalToPaper
                    )
                }
            }
        }
    }
}
