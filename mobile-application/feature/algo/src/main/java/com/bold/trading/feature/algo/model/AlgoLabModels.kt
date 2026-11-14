package com.bold.trading.feature.algo.model

import java.math.BigDecimal

data class ConditionBlock(
    val id: String,
    val indicatorA: String,
    val operator: String, // "Crosses Above", "Crosses Below", "Greater Than", "Less Than"
    val indicatorB: String
)

data class AlgoStrategy(
    val id: String,
    val name: String,
    val description: String,
    val timeframe: String = "15m",
    val conditions: List<ConditionBlock>,
    val targetPercent: Float = 2.0f,
    val stopLossPercent: Float = 1.0f
)

data class BacktestResult(
    val strategyName: String,
    val winRate: Float,
    val profitFactor: Float,
    val maxDrawdown: Float,
    val totalTrades: Int,
    val winningTrades: Int,
    val losingTrades: Int,
    val finalEquity: Float,
    val equityCurve: List<Float>
)

data class InstitutionalSignal(
    val id: String,
    val symbol: String,
    val direction: String, // "BUY" or "SELL"
    val strategyName: String,
    val entryMin: BigDecimal,
    val entryMax: BigDecimal,
    val stopLoss: BigDecimal,
    val target1: BigDecimal,
    val target2: BigDecimal,
    val target3: BigDecimal,
    val confidencePercent: Int,
    val timestampText: String
)
