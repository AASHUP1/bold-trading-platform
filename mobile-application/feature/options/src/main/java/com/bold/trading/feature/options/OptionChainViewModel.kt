package com.bold.trading.feature.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.database.entity.CachedPaperPositionEntity
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import com.bold.trading.feature.options.model.OptionLeg
import com.bold.trading.feature.options.model.OptionStrikeRow
import com.bold.trading.feature.options.model.StrategyPayoffCalculator
import com.bold.trading.feature.options.model.StrategyPayoffResult
import com.bold.trading.feature.options.model.StrategyPreset
import com.bold.trading.feature.papertrading.risk.RiskEngineValidator
import com.bold.trading.feature.papertrading.risk.RiskValidationResult
import com.bold.trading.feature.papertrading.ui.FillReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.random.Random

data class OptionChainUiState(
    val underlyingSymbol: String = "NIFTY 50",
    val spotPrice: BigDecimal = BigDecimal("24360.50"),
    val lotSize: Int = 25,
    val selectedExpiry: String = "28-NOV-2024",
    val availableExpiries: List<String> = listOf("28-NOV-2024", "05-DEC-2024", "26-DEC-2024"),
    val strikes: List<OptionStrikeRow> = emptyList(),
    val atmStrike: BigDecimal = BigDecimal("24350.00"),
    val selectedPreset: StrategyPreset? = null,
    val activeStrategyLegs: List<OptionLeg> = emptyList(),
    val payoffResult: StrategyPayoffResult? = null,
    val isPayoffSheetOpen: Boolean = false,
    val lastBasketReceipt: FillReceipt? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class OptionChainViewModel(
    private val instrumentDao: InstrumentDao,
    private val paperAccountDao: PaperAccountDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(OptionChainUiState(isLoading = true))
    val uiState: StateFlow<OptionChainUiState> = _uiState

    init {
        loadOptionChain("NIFTY 50")
    }

    fun selectUnderlying(symbol: String) {
        loadOptionChain(symbol)
    }

    fun selectExpiry(expiry: String) {
        _uiState.update { it.copy(selectedExpiry = expiry) }
        rebuildStrikes(_uiState.value.underlyingSymbol, _uiState.value.spotPrice)
    }

    fun selectStrategyPreset(preset: StrategyPreset) {
        val spot = _uiState.value.spotPrice.toFloat()
        val atm = _uiState.value.atmStrike
        val step = if (_uiState.value.underlyingSymbol == "NIFTY 50") BigDecimal("50.00") else BigDecimal("100.00")

        val legs = when (preset) {
            StrategyPreset.BULL_CALL_SPREAD -> listOf(
                OptionLeg(strike = atm, isCall = true, isBuy = true, quantity = 1, premium = estimateCallPremium(spot, atm.toFloat())),
                OptionLeg(strike = atm.add(step.multiply(BigDecimal(2))), isCall = true, isBuy = false, quantity = 1, premium = estimateCallPremium(spot, atm.toFloat() + (step.toFloat() * 2)))
            )
            StrategyPreset.BEAR_PUT_SPREAD -> listOf(
                OptionLeg(strike = atm, isCall = false, isBuy = true, quantity = 1, premium = estimatePutPremium(spot, atm.toFloat())),
                OptionLeg(strike = atm.subtract(step.multiply(BigDecimal(2))), isCall = false, isBuy = false, quantity = 1, premium = estimatePutPremium(spot, atm.toFloat() - (step.toFloat() * 2)))
            )
            StrategyPreset.IRON_CONDOR -> listOf(
                OptionLeg(strike = atm.subtract(step.multiply(BigDecimal(4))), isCall = false, isBuy = true, quantity = 1, premium = estimatePutPremium(spot, atm.toFloat() - (step.toFloat() * 4))),
                OptionLeg(strike = atm.subtract(step.multiply(BigDecimal(2))), isCall = false, isBuy = false, quantity = 1, premium = estimatePutPremium(spot, atm.toFloat() - (step.toFloat() * 2))),
                OptionLeg(strike = atm.add(step.multiply(BigDecimal(2))), isCall = true, isBuy = false, quantity = 1, premium = estimateCallPremium(spot, atm.toFloat() + (step.toFloat() * 2))),
                OptionLeg(strike = atm.add(step.multiply(BigDecimal(4))), isCall = true, isBuy = true, quantity = 1, premium = estimateCallPremium(spot, atm.toFloat() + (step.toFloat() * 4)))
            )
            StrategyPreset.LONG_STRADDLE -> listOf(
                OptionLeg(strike = atm, isCall = true, isBuy = true, quantity = 1, premium = estimateCallPremium(spot, atm.toFloat())),
                OptionLeg(strike = atm, isCall = false, isBuy = true, quantity = 1, premium = estimatePutPremium(spot, atm.toFloat()))
            )
        }

        val payoff = StrategyPayoffCalculator.calculatePayoff(legs, spot, _uiState.value.lotSize)
        _uiState.update {
            it.copy(
                selectedPreset = preset,
                activeStrategyLegs = legs,
                payoffResult = payoff,
                isPayoffSheetOpen = true,
                errorMessage = null
            )
        }
    }

    fun closePayoffSheet() {
        _uiState.update { it.copy(isPayoffSheetOpen = false, errorMessage = null) }
    }

    fun dismissBasketReceipt() {
        _uiState.update { it.copy(lastBasketReceipt = null) }
    }

    fun executeSimulatedBasket() {
        viewModelScope.launch {
            val state = _uiState.value
            val legs = state.activeStrategyLegs
            val payoff = state.payoffResult ?: return@launch

            val account = paperAccountDao.getPaperAccount().stateIn(viewModelScope).value ?: PaperAccountEntity()
            val totalRequiredCash = payoff.netDebitCredit

            if (payoff.isNetDebit && totalRequiredCash > account.availableCash) {
                _uiState.update { it.copy(errorMessage = "Insufficient cash for basket spread: Required ₹$totalRequiredCash, available ₹${account.availableCash}") }
                return@launch
            }

            // Deduct net debit / add credit
            val newCash = if (payoff.isNetDebit) account.availableCash.subtract(totalRequiredCash) else account.availableCash.add(totalRequiredCash)
            val newMargin = account.marginUsed.add(totalRequiredCash)
            paperAccountDao.updateCashAndMargin(account.id, newCash, newMargin)

            // Append immutable ledger record
            val txId = "BSK-${Random.nextInt(10000, 99999)}"
            val ledgerEntry = LedgerEntryEntity(
                id = "ledger-$txId",
                paperAccountId = account.id,
                orderId = txId,
                entryType = "BASKET_SPREAD",
                amount = totalRequiredCash,
                balanceAfter = newCash,
                description = "Simulated ${state.selectedPreset?.displayName ?: "Multi-Leg"} (${legs.size} Legs) on ${state.underlyingSymbol}"
            )
            paperAccountDao.insertLedgerEntry(ledgerEntry)

            // Add positions for each leg
            legs.forEach { leg ->
                val legSymbol = "${state.underlyingSymbol} ${leg.strike.toInt()} ${if (leg.isCall) "CE" else "PE"}"
                paperAccountDao.insertOrUpdatePosition(
                    CachedPaperPositionEntity(
                        instrumentId = "opt-${legSymbol.replace(" ", "-")}",
                        symbol = legSymbol,
                        quantity = if (leg.isBuy) leg.quantity * state.lotSize else -leg.quantity * state.lotSize,
                        averageEntryPrice = leg.premium,
                        currentLtp = leg.premium,
                        unrealizedPnl = BigDecimal.ZERO
                    )
                )
            }

            _uiState.update {
                it.copy(
                    isPayoffSheetOpen = false,
                    lastBasketReceipt = FillReceipt(
                        transactionId = txId,
                        symbol = "${state.underlyingSymbol} ${state.selectedPreset?.displayName ?: "Basket"}",
                        isBuy = payoff.isNetDebit,
                        quantity = legs.size,
                        fillPrice = totalRequiredCash,
                        totalAmount = totalRequiredCash
                    )
                )
            }
        }
    }

    private fun loadOptionChain(symbol: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val (spot, lotSize) = if (symbol == "NIFTY 50") {
                Pair(BigDecimal("24360.50"), 25)
            } else {
                Pair(BigDecimal("51820.00"), 15)
            }

            _uiState.update {
                it.copy(
                    underlyingSymbol = symbol,
                    spotPrice = spot,
                    lotSize = lotSize,
                    isLoading = false
                )
            }
            rebuildStrikes(symbol, spot)
        }
    }

    private fun rebuildStrikes(symbol: String, spot: BigDecimal) {
        val step = if (symbol == "NIFTY 50") 50 else 100
        val spotInt = spot.setScale(0, RoundingMode.HALF_UP).toInt()
        val atmStrikeInt = (spotInt / step) * step
        val atmStrike = BigDecimal(atmStrikeInt)

        val rows = ArrayList<OptionStrikeRow>(21)
        val strikeCountEachSide = 10

        for (i in -strikeCountEachSide..strikeCountEachSide) {
            val strikeVal = atmStrikeInt + (i * step)
            val strike = BigDecimal(strikeVal)
            val isAtm = strikeVal == atmStrikeInt

            val spotFloat = spot.toFloat()
            val strikeFloat = strikeVal.toFloat()

            val callLtp = estimateCallPremium(spotFloat, strikeFloat)
            val putLtp = estimatePutPremium(spotFloat, strikeFloat)

            val callDelta = (0.50f + ((spotFloat - strikeFloat) / (strikeFloat * 0.08f))).coerceIn(0.05f, 0.95f)
            val putDelta = (callDelta - 1.0f).coerceIn(-0.95f, -0.05f)

            rows.add(
                OptionStrikeRow(
                    strike = strike,
                    isAtm = isAtm,
                    callLtp = callLtp,
                    callOi = Random.nextLong(450_000, 3_800_000),
                    callIv = 13.4f + (abs(i) * 0.25f),
                    callDelta = callDelta,
                    callVolume = Random.nextLong(80_000, 950_000),
                    putLtp = putLtp,
                    putOi = Random.nextLong(320_000, 4_100_000),
                    putIv = 13.6f + (abs(i) * 0.28f),
                    putDelta = putDelta,
                    putVolume = Random.nextLong(75_000, 890_000)
                )
            )
        }

        _uiState.update {
            it.copy(
                strikes = rows,
                atmStrike = atmStrike
            )
        }
    }

    private fun estimateCallPremium(spot: Float, strike: Float): BigDecimal {
        val intrinsic = max(0f, spot - strike)
        val timeValue = (spot * 0.012f) * exp(-abs(spot - strike) / (spot * 0.04f))
        val total = (intrinsic + timeValue).coerceAtLeast(1.50f)
        return BigDecimal(total.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
    }

    private fun estimatePutPremium(spot: Float, strike: Float): BigDecimal {
        val intrinsic = max(0f, strike - spot)
        val timeValue = (spot * 0.012f) * exp(-abs(strike - spot) / (spot * 0.04f))
        val total = (intrinsic + timeValue).coerceAtLeast(1.50f)
        return BigDecimal(total.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
    }
}
