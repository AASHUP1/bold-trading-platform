package com.bold.trading.feature.scanner.model

import com.bold.trading.core.database.entity.CachedInstrumentEntity

enum class ScannerPreset(val displayName: String, val ruleDescription: String) {
    BREAKOUT("Breakout", "Price above 20 EMA AND 24h High break AND Change > 1.0%"),
    VOLUME_SPIKE("Volume Spike", "24h Volume > 1.5x average AND Positive momentum"),
    VWAP_RECLAIM("VWAP Reclaim", "LTP crossed above intraday VWAP with bullish tick confirmation"),
    RSI_OVERSOLD("RSI Oversold Bounce", "RSI 14 < 35 with immediate reversal bar"),
    GAP_UP("Gap Up Momentum", "Open above previous close by > 0.8% with buyer dominance")
}

data class ScanResultItem(
    val instrument: CachedInstrumentEntity,
    val preset: ScannerPreset,
    val matchTimeText: String,
    val checklist: List<String>,
    val isBullish: Boolean
)
