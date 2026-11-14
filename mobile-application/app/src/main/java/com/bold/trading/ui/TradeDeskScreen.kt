package com.bold.trading.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.options.OptionChainViewModel
import com.bold.trading.feature.options.ui.OptionChainScreen
import com.bold.trading.feature.papertrading.PaperTerminalViewModel
import com.bold.trading.feature.papertrading.ui.PaperTerminalScreen

enum class TradeDeskMode(val title: String) {
    OPTION_CHAIN("Option Chain (S11)"),
    PAPER_LEDGER("Paper Ledger (S14/S16)")
}

/**
 * Unified Trade Desk Host: F&O Options Strategy Chain and Paper Ledger Terminal.
 * Follows DESIGN.md Section 5.5 & S11/S14/S16.
 */
@Composable
fun TradeDeskScreen(
    optionChainViewModel: OptionChainViewModel,
    paperTerminalViewModel: PaperTerminalViewModel,
    onNavigateToWatchlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(TradeDeskMode.OPTION_CHAIN) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
    ) {
        // Mode Switcher Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BoldColors.SurfaceElevated)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TradeDeskMode.entries.forEach { mode ->
                val isSelected = activeMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) BoldColors.SurfaceCard else androidx.compose.ui.graphics.Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) BoldColors.BorderSubtle else androidx.compose.ui.graphics.Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { activeMode = mode }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.title,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (activeMode) {
                TradeDeskMode.OPTION_CHAIN -> {
                    OptionChainScreen(viewModel = optionChainViewModel)
                }
                TradeDeskMode.PAPER_LEDGER -> {
                    PaperTerminalScreen(
                        viewModel = paperTerminalViewModel,
                        onNavigateToWatchlist = onNavigateToWatchlist
                    )
                }
            }
        }
    }
}
