package com.bold.trading.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.scanner.ScannerViewModel
import com.bold.trading.feature.scanner.ui.ScannerScreen
import com.bold.trading.feature.watchlist.WatchlistViewModel
import com.bold.trading.feature.watchlist.ui.WatchlistScreen

enum class DiscoveryMode(val title: String) {
    WATCHLISTS("Watchlists (S06)"),
    MARKET_SCANNER("Market Scanner (S08)")
}

/**
 * Unified Discovery Screen: Combines Multi-Watchlist Monitor (S06) and Real-Time Market Scanner (S08).
 * Follows DESIGN.md Section 5.5 & S06/S08.
 */
@Composable
fun DiscoveryScreen(
    watchlistViewModel: WatchlistViewModel,
    scannerViewModel: ScannerViewModel,
    onOpenChart: (CachedInstrumentEntity) -> Unit,
    onSimulateTrade: (CachedInstrumentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(DiscoveryMode.WATCHLISTS) }

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
            DiscoveryMode.entries.forEach { mode ->
                val isSelected = activeMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) BoldColors.SurfaceCard else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) BoldColors.BorderSubtle else Color.Transparent,
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
                DiscoveryMode.WATCHLISTS -> {
                    WatchlistScreen(
                        viewModel = watchlistViewModel,
                        onSimulateTrade = onSimulateTrade,
                        onOpenChart = onOpenChart
                    )
                }
                DiscoveryMode.MARKET_SCANNER -> {
                    ScannerScreen(
                        viewModel = scannerViewModel,
                        onOpenChart = onOpenChart,
                        onSimulateTrade = onSimulateTrade
                    )
                }
            }
        }
    }
}
