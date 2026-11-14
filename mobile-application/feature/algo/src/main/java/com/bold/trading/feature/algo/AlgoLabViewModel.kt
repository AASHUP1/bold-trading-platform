package com.bold.trading.feature.algo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.feature.algo.model.AlgoStrategy
import com.bold.trading.feature.algo.model.BacktestResult
import com.bold.trading.feature.algo.model.ConditionBlock
import com.bold.trading.feature.algo.model.InstitutionalSignal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

enum class AlgoLabTab(val label: String) {
    BUILDER("Rule Builder"),
    BACKTEST("Backtest Results"),
    SIGNALS("Signals Feed")
}

data class AlgoLabUiState(
    val currentTab: AlgoLabTab = AlgoLabTab.BUILDER,
    val currentStrategy: AlgoStrategy = defaultStrategy,
    val backtestResult: BacktestResult? = null,
    val signals: List<InstitutionalSignal> = defaultSignals,
    val isBacktesting: Boolean = false
)

private val defaultStrategy = AlgoStrategy(
    id = "strat-vwap-01",
    name = "NIFTY VWAP Reclaim v2.1",
    description = "Intraday momentum strategy entering on volume-backed VWAP reclamation",
    timeframe = "15m",
    conditions = listOf(
        ConditionBlock("c1", "Price", "Crosses Above", "VWAP"),
        ConditionBlock("c2", "RSI (14)", "Greater Than", "50.0"),
        ConditionBlock("c3", "Volume", "Greater Than", "1.5x 20D Avg")
    ),
    targetPercent = 2.0f,
    stopLossPercent = 1.0f
)

private val defaultSignals = listOf(
    InstitutionalSignal(
        id = "sig-001",
        symbol = "RELIANCE",
        direction = "BUY",
        strategyName = "VWAP Reclaim v2.1",
        entryMin = BigDecimal("2975.00"),
        entryMax = BigDecimal("2985.00"),
        stopLoss = BigDecimal("2945.00"),
        target1 = BigDecimal("3035.00"),
        target2 = BigDecimal("3080.00"),
        target3 = BigDecimal("3140.00"),
        confidencePercent = 88,
        timestampText = "14:12 IST"
    ),
    InstitutionalSignal(
        id = "sig-002",
        symbol = "INFY",
        direction = "BUY",
        strategyName = "Golden Cross Breakout",
        entryMin = BigDecimal("1860.00"),
        entryMax = BigDecimal("1868.00"),
        stopLoss = BigDecimal("1840.00"),
        target1 = BigDecimal("1905.00"),
        target2 = BigDecimal("1940.00"),
        target3 = BigDecimal("1980.00"),
        confidencePercent = 91,
        timestampText = "14:05 IST"
    ),
    InstitutionalSignal(
        id = "sig-003",
        symbol = "HDFCBANK",
        direction = "SELL",
        strategyName = "Mean Reversion Exhaustion",
        entryMin = BigDecimal("1645.00"),
        entryMax = BigDecimal("1652.00"),
        stopLoss = BigDecimal("1665.00"),
        target1 = BigDecimal("1620.00"),
        target2 = BigDecimal("1595.00"),
        target3 = BigDecimal("1570.00"),
        confidencePercent = 79,
        timestampText = "13:48 IST"
    )
)

class AlgoLabViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlgoLabUiState())
    val uiState: StateFlow<AlgoLabUiState> = _uiState.asStateFlow()

    init {
        // Pre-compute initial backtest result for immediate viewing
        runHistoricalBacktest()
    }

    fun selectTab(tab: AlgoLabTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun updateTimeframe(timeframe: String) {
        _uiState.update { it.copy(currentStrategy = it.currentStrategy.copy(timeframe = timeframe)) }
    }

    fun addCondition() {
        val current = _uiState.value.currentStrategy.conditions.toMutableList()
        val newId = "c${current.size + 1}"
        current.add(ConditionBlock(newId, "EMA 20", "Greater Than", "EMA 50"))
        _uiState.update { it.copy(currentStrategy = it.currentStrategy.copy(conditions = current)) }
    }

    fun removeCondition(id: String) {
        val current = _uiState.value.currentStrategy.conditions.filter { it.id != id }
        if (current.isNotEmpty()) {
            _uiState.update { it.copy(currentStrategy = it.currentStrategy.copy(conditions = current)) }
        }
    }

    fun runHistoricalBacktest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBacktesting = true) }
            delay(350) // Simulation delay

            // Generate 250 daily bar equity trajectory from ₹10,00,000 base
            val totalBars = 250
            val equityPoints = ArrayList<Float>(totalBars)
            var currentCapital = 1_000_000f
            equityPoints.add(currentCapital)

            var wins = 0
            var losses = 0
            var peakCapital = currentCapital
            var maxDrawdown = 0f

            for (i in 1 until totalBars) {
                val isWin = Random.nextFloat() < 0.642f // 64.2% win rate
                val delta = if (isWin) {
                    wins++
                    currentCapital * (0.015f + (Random.nextFloat() * 0.01f))
                } else {
                    losses++
                    -(currentCapital * (0.008f + (Random.nextFloat() * 0.004f)))
                }

                currentCapital += delta
                equityPoints.add(currentCapital)

                if (currentCapital > peakCapital) {
                    peakCapital = currentCapital
                } else {
                    val dd = (peakCapital - currentCapital) / peakCapital
                    if (dd > maxDrawdown) {
                        maxDrawdown = dd
                    }
                }
            }

            val winRate = (wins.toFloat() / (wins + losses)) * 100f
            val result = BacktestResult(
                strategyName = _uiState.value.currentStrategy.name,
                winRate = winRate,
                profitFactor = 1.85f,
                maxDrawdown = -(maxDrawdown * 100f),
                totalTrades = wins + losses,
                winningTrades = wins,
                losingTrades = losses,
                finalEquity = currentCapital,
                equityCurve = equityPoints
            )

            _uiState.update {
                it.copy(
                    backtestResult = result,
                    isBacktesting = false,
                    currentTab = AlgoLabTab.BACKTEST
                )
            }
        }
    }
}
