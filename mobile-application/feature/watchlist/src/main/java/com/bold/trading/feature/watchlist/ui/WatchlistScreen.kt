package com.bold.trading.feature.watchlist.ui

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.watchlist.WatchlistTab
import com.bold.trading.feature.watchlist.WatchlistViewModel

/**
 * Screen S06: Multi-Watchlist Component with Sparklines & Search Filter.
 * Follows DESIGN.md Section 6: S06 & PLAN.md Task 3.2.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onSimulateTrade: (CachedInstrumentEntity) -> Unit,
    onOpenChart: (CachedInstrumentEntity) -> Unit,
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
        Spacer(modifier = Modifier.height(14.dp))

        // Search Input Bar
        BoldCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BoldColors.BorderSubtle
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = BoldColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = BoldColors.TextPrimary,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(BoldColors.PrimaryBullish),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (state.searchQuery.isEmpty()) {
                            Text(
                                text = "Search symbol, company or index...",
                                color = BoldColors.TextMuted,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.onClearSearch() },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear",
                            tint = BoldColors.TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WatchlistTab.entries.forEach { tab ->
                val isSelected = state.selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.displayName,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Watchlist instruments LazyColumn
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = BoldColors.PrimaryBullish,
                    strokeWidth = 2.dp
                )
            }
        } else if (state.filteredInstruments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StatusBadge(text = "0 Results", status = BadgeStatus.Neutral)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No instruments matched '${state.searchQuery}'",
                        color = BoldColors.TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.filteredInstruments,
                    key = { it.instrumentId }
                ) { instrument ->
                    InstrumentCard(
                        instrument = instrument,
                        onClick = { viewModel.onInstrumentSelected(instrument) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Modal detail bottom sheet
    state.selectedInstrument?.let { instrument ->
        InstrumentDetailSheet(
            instrument = instrument,
            onDismiss = { viewModel.onInstrumentSelected(null) },
            onSimulateTrade = {
                viewModel.onInstrumentSelected(null)
                onSimulateTrade(it)
            },
            onOpenChart = {
                viewModel.onInstrumentSelected(null)
                onOpenChart(it)
            }
        )
    }
}
