package com.bold.trading.feature.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.core.charts.model.CandleModel
import com.bold.trading.core.charts.model.TechnicalIndicators
import com.bold.trading.core.database.dao.CandleDao
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.entity.CachedCandleEntity
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.random.Random

enum class ChartInterval(val label: String, val seconds: Long) {
    ONE_MIN("1m", 60),
    FIVE_MIN("5m", 300),
    FIFTEEN_MIN("15m", 900),
    ONE_HOUR("1H", 3600),
    ONE_DAY("1D", 86400)
}

data class ChartUiState(
    val instrument: CachedInstrumentEntity? = null,
    val selectedInterval: ChartInterval = ChartInterval.FIFTEEN_MIN,
    val candles: List<CandleModel> = emptyList(),
    val ema20: List<Float?> = emptyList(),
    val ema50: List<Float?> = emptyList(),
    val vwap: List<Float?> = emptyList(),
    val rsi14: List<Float?> = emptyList(),
    val showEma20: Boolean = true,
    val showEma50: Boolean = true,
    val showVwap: Boolean = false,
    val showRsi: Boolean = true,
    val showVolume: Boolean = true,
    val inspectedCandle: CandleModel? = null,
    val isLoading: Boolean = false
)

class ChartViewModel(
    private val instrumentDao: InstrumentDao,
    private val candleDao: CandleDao? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    fun loadInstrument(symbol: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            instrumentDao.getInstrumentBySymbol(symbol).collect { inst ->
                if (inst != null) {
                    val cachedList = candleDao?.getCandles(symbol, _uiState.value.selectedInterval.label)?.firstOrNull()
                    val candles = if (!cachedList.isNullOrEmpty()) {
                        cachedList.map { c ->
                            CandleModel(
                                timestamp = c.timestamp,
                                open = c.open.toFloat(),
                                high = c.high.toFloat(),
                                low = c.low.toFloat(),
                                close = c.close.toFloat(),
                                volume = c.volume.toFloat()
                            )
                        }
                    } else {
                        val generated = generateMockCandles(inst.lastTradedPrice.toFloat(), 100)
                        candleDao?.insertCandles(
                            generated.map { gm ->
                                CachedCandleEntity(
                                    symbol = symbol,
                                    timeframe = _uiState.value.selectedInterval.label,
                                    timestamp = gm.timestamp,
                                    open = BigDecimal(gm.open.toDouble()).setScale(2, java.math.RoundingMode.HALF_UP),
                                    high = BigDecimal(gm.high.toDouble()).setScale(2, java.math.RoundingMode.HALF_UP),
                                    low = BigDecimal(gm.low.toDouble()).setScale(2, java.math.RoundingMode.HALF_UP),
                                    close = BigDecimal(gm.close.toDouble()).setScale(2, java.math.RoundingMode.HALF_UP),
                                    volume = gm.volume.toLong()
                                )
                            }
                        )
                        generated
                    }

                    val ema20 = TechnicalIndicators.calculateEMA(candles, 20)
                    val ema50 = TechnicalIndicators.calculateEMA(candles, 50)
                    val vwap = TechnicalIndicators.calculateVWAP(candles)
                    val rsi = TechnicalIndicators.calculateRSI(candles, 14)

                    _uiState.update {
                        it.copy(
                            instrument = inst,
                            candles = candles,
                            ema20 = ema20,
                            ema50 = ema50,
                            vwap = vwap,
                            rsi14 = rsi,
                            inspectedCandle = candles.lastOrNull(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onIntervalSelected(interval: ChartInterval) {
        _uiState.update { it.copy(selectedInterval = interval) }
        val currentLtp = _uiState.value.instrument?.lastTradedPrice?.toFloat() ?: 24000f
        val newCandles = generateMockCandles(currentLtp, 100)

        _uiState.update {
            it.copy(
                candles = newCandles,
                ema20 = TechnicalIndicators.calculateEMA(newCandles, 20),
                ema50 = TechnicalIndicators.calculateEMA(newCandles, 50),
                vwap = TechnicalIndicators.calculateVWAP(newCandles),
                rsi14 = TechnicalIndicators.calculateRSI(newCandles, 14),
                inspectedCandle = newCandles.lastOrNull()
            )
        }
    }

    fun toggleEma20() = _uiState.update { it.copy(showEma20 = !it.showEma20) }
    fun toggleEma50() = _uiState.update { it.copy(showEma50 = !it.showEma50) }
    fun toggleVwap() = _uiState.update { it.copy(showVwap = !it.showVwap) }
    fun toggleRsi() = _uiState.update { it.copy(showRsi = !it.showRsi) }
    fun toggleVolume() = _uiState.update { it.copy(showVolume = !it.showVolume) }

    fun onCrosshairCandle(candle: CandleModel?) {
        _uiState.update { it.copy(inspectedCandle = candle ?: it.candles.lastOrNull()) }
    }

    private fun generateMockCandles(currentPrice: Float, count: Int): List<CandleModel> {
        val candles = ArrayList<CandleModel>(count)
        var price = currentPrice * 0.94f // Start slightly lower to trend toward current price
        val now = System.currentTimeMillis()

        for (i in 0 until count) {
            val deltaPct = (Random.nextDouble() - 0.485) * 0.012
            val open = price
            val close = (open * (1.0 + deltaPct)).toFloat()
            val high = maxOf(open, close) + (open * Random.nextFloat() * 0.005f)
            val low = minOf(open, close) - (open * Random.nextFloat() * 0.005f)
            val volume = Random.nextLong(20_000, 450_000)

            candles.add(
                CandleModel(
                    timestamp = now - ((count - i) * 900_000L),
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )
            price = close
        }
        return candles
    }
}
