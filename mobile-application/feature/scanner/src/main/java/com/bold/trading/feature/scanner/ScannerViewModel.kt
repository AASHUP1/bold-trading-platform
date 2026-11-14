package com.bold.trading.feature.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.feature.scanner.model.ScanResultItem
import com.bold.trading.feature.scanner.model.ScannerPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

data class ScannerUiState(
    val selectedPreset: ScannerPreset = ScannerPreset.BREAKOUT,
    val matchedResults: List<ScanResultItem> = emptyList(),
    val totalUniverseCount: Int = 0,
    val isLoading: Boolean = false
)

class ScannerViewModel(
    instrumentDao: InstrumentDao
) : ViewModel() {

    private val _selectedPreset = MutableStateFlow(ScannerPreset.BREAKOUT)

    val uiState: StateFlow<ScannerUiState> = combine(
        instrumentDao.getAllInstruments(),
        _selectedPreset
    ) { allInstruments, preset ->
        val matched = allInstruments.filter { !it.isIndex }.filter { inst ->
            when (preset) {
                ScannerPreset.BREAKOUT -> inst.changePercent >= BigDecimal("0.50")
                ScannerPreset.VOLUME_SPIKE -> inst.volume >= 8_000_000L
                ScannerPreset.VWAP_RECLAIM -> inst.changePercent >= BigDecimal("0.00")
                ScannerPreset.RSI_OVERSOLD -> inst.changePercent < BigDecimal("0.00")
                ScannerPreset.GAP_UP -> inst.changePercent >= BigDecimal("1.00")
            }
        }.map { inst ->
            ScanResultItem(
                instrument = inst,
                preset = preset,
                matchTimeText = "Triggered 2m ago",
                checklist = when (preset) {
                    ScannerPreset.BREAKOUT -> listOf("Price > 20 EMA", "24h High Test", "Change > 0.50%")
                    ScannerPreset.VOLUME_SPIKE -> listOf("Volume > 1.5x Avg", "Tick Velocity High", "Buyer Interest")
                    ScannerPreset.VWAP_RECLAIM -> listOf("Reclaimed VWAP", "Bullish Candle Fill", "Zero Sell Pressure")
                    ScannerPreset.RSI_OVERSOLD -> listOf("RSI 14 < 35", "Support Zone Rebound", "Defensive Entry")
                    ScannerPreset.GAP_UP -> listOf("Gap > 0.8%", "Pre-Market Buyer Flow", "Sustained Above Open")
                },
                isBullish = inst.changePercent >= BigDecimal.ZERO
            )
        }

        ScannerUiState(
            selectedPreset = preset,
            matchedResults = matched,
            totalUniverseCount = allInstruments.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScannerUiState(isLoading = true)
    )

    fun onPresetSelected(preset: ScannerPreset) {
        _selectedPreset.update { preset }
    }
}
