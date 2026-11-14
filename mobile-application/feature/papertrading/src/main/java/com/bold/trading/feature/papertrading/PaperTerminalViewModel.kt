package com.bold.trading.feature.papertrading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.entity.CachedPaperPositionEntity
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import com.bold.trading.feature.papertrading.risk.RiskEngineValidator
import com.bold.trading.feature.papertrading.risk.RiskValidationResult
import com.bold.trading.feature.papertrading.ui.FillReceipt
import com.bold.trading.feature.papertrading.ui.OrderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

enum class PaperTab(val label: String) {
    POSITIONS("Active Positions"),
    LEDGER("Virtual Ledger Journal")
}

data class PaperUiState(
    val account: PaperAccountEntity? = null,
    val positions: List<CachedPaperPositionEntity> = emptyList(),
    val ledgerEntries: List<LedgerEntryEntity> = emptyList(),
    val selectedTab: PaperTab = PaperTab.POSITIONS,
    val activeTicketInstrument: CachedInstrumentEntity? = null,
    val lastFillReceipt: FillReceipt? = null,
    val errorMessage: String? = null,
    val isResetDialogOpen: Boolean = false,
    val isLoading: Boolean = false
)

class PaperTerminalViewModel(
    private val paperAccountDao: PaperAccountDao,
    private val instrumentDao: InstrumentDao
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(PaperTab.POSITIONS)
    private val _activeTicketInstrument = MutableStateFlow<CachedInstrumentEntity?>(null)
    private val _lastFillReceipt = MutableStateFlow<FillReceipt?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isResetDialogOpen = MutableStateFlow(false)

    val uiState: StateFlow<PaperUiState> = combine(
        paperAccountDao.getPaperAccount(),
        paperAccountDao.getAllPositions(),
        paperAccountDao.getLedgerEntries(),
        _selectedTab,
        _activeTicketInstrument,
        _lastFillReceipt
    ) { account, positions, ledger, tab, ticketInst, receipt ->
        PaperUiState(
            account = account,
            positions = positions,
            ledgerEntries = ledger,
            selectedTab = tab,
            activeTicketInstrument = ticketInst,
            lastFillReceipt = receipt,
            errorMessage = _errorMessage.value,
            isResetDialogOpen = _isResetDialogOpen.value,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaperUiState(isLoading = true)
    )

    fun onTabSelected(tab: PaperTab) {
        _selectedTab.update { tab }
    }

    fun openOrderTicket(instrument: CachedInstrumentEntity) {
        _errorMessage.update { null }
        _activeTicketInstrument.update { instrument }
    }

    fun closeOrderTicket() {
        _activeTicketInstrument.update { null }
        _errorMessage.update { null }
    }

    fun dismissReceipt() {
        _lastFillReceipt.update { null }
    }

    fun openResetDialog() = _isResetDialogOpen.update { true }
    fun closeResetDialog() = _isResetDialogOpen.update { false }

    fun executeSimulatedOrder(
        instrument: CachedInstrumentEntity,
        isBuy: Boolean,
        orderType: OrderType,
        quantity: Int,
        price: BigDecimal,
        stopLoss: BigDecimal?,
        target: BigDecimal?
    ) {
        viewModelScope.launch {
            val account = uiState.value.account ?: return@launch
            val virtualCapital = account.virtualCapital
            val availableCash = account.availableCash
            val realizedPnl = account.realizedPnl

            // 1. Pre-trade Risk Validation
            val validation = RiskEngineValidator.validateOrder(
                virtualCapital = virtualCapital,
                availableCash = availableCash,
                realizedLossToday = realizedPnl,
                entryPrice = price,
                quantity = quantity,
                stopLoss = stopLoss,
                isBuy = isBuy
            )

            when (validation) {
                is RiskValidationResult.Rejected -> {
                    _errorMessage.update { validation.reason }
                    return@launch
                }
                is RiskValidationResult.Approved -> {
                    // 2. Perform Virtual Ledger Fill
                    val totalOrderValue = price.multiply(BigDecimal(quantity))
                    val newCash = if (isBuy) availableCash.subtract(totalOrderValue) else availableCash.add(totalOrderValue)
                    val newMargin = if (isBuy) account.marginUsed.add(totalOrderValue) else account.marginUsed

                    paperAccountDao.updateCashAndMargin(account.id, newCash, newMargin)

                    // 3. Append immutable ledger transaction
                    val txId = "SIM-${Random.nextInt(10000, 99999)}"
                    val ledgerEntry = LedgerEntryEntity(
                        id = "ledger-$txId",
                        paperAccountId = account.id,
                        orderId = txId,
                        entryType = if (isBuy) "BUY_FILL" else "SELL_FILL",
                        amount = totalOrderValue,
                        balanceAfter = newCash,
                        description = "${if (isBuy) "BUY" else "SELL"} $quantity ${instrument.symbol} @ ₹${price.setScale(2, RoundingMode.HALF_EVEN)}"
                    )
                    paperAccountDao.insertLedgerEntry(ledgerEntry)

                    // 4. Update or Insert Position
                    val existing = paperAccountDao.getPositionByInstrumentId(instrument.id)
                    val signedQty = if (isBuy) quantity else -quantity

                    val updatedPosition = if (existing == null) {
                        CachedPaperPositionEntity(
                            instrumentId = instrument.id,
                            symbol = instrument.symbol,
                            quantity = signedQty,
                            averageEntryPrice = price,
                            currentLtp = price,
                            unrealizedPnl = BigDecimal.ZERO
                        )
                    } else {
                        val combinedQty = existing.quantity + signedQty
                        val newAvg = if (combinedQty != 0) {
                            (existing.averageEntryPrice.multiply(BigDecimal(existing.quantity.toLong().coerceAtLeast(0)))
                                .add(price.multiply(BigDecimal(signedQty.toLong().coerceAtLeast(0)))))
                                .divide(BigDecimal(combinedQty.toLong().coerceAtLeast(1)), 2, RoundingMode.HALF_EVEN)
                        } else {
                            price
                        }
                        existing.copy(
                            quantity = combinedQty,
                            averageEntryPrice = newAvg,
                            currentLtp = price
                        )
                    }

                    if (updatedPosition.quantity == 0) {
                        paperAccountDao.deletePosition(instrument.id)
                    } else {
                        paperAccountDao.insertOrUpdatePosition(updatedPosition)
                    }

                    // 5. Show Receipt Dialog
                    _activeTicketInstrument.update { null }
                    _lastFillReceipt.update {
                        FillReceipt(
                            transactionId = txId,
                            symbol = instrument.symbol,
                            isBuy = isBuy,
                            quantity = quantity,
                            fillPrice = price,
                            totalAmount = totalOrderValue
                        )
                    }
                }
            }
        }
    }

    fun squareOffPosition(position: CachedPaperPositionEntity) {
        viewModelScope.launch {
            val account = uiState.value.account ?: return@launch
            val isLong = position.quantity > 0
            val absQty = kotlin.math.abs(position.quantity)

            val pnl = (position.currentLtp - position.averageEntryPrice).multiply(BigDecimal(position.quantity))
            val cashToReturn = position.currentLtp.multiply(BigDecimal(absQty))
            val newCash = account.availableCash.add(cashToReturn)
            val newMargin = (account.marginUsed - position.averageEntryPrice.multiply(BigDecimal(absQty))).coerceAtLeast(BigDecimal.ZERO)

            paperAccountDao.updateCashAndMargin(account.id, newCash, newMargin)
            paperAccountDao.addRealizedPnl(account.id, pnl)

            val txId = "SQ-${Random.nextInt(10000, 99999)}"
            val ledgerEntry = LedgerEntryEntity(
                id = "ledger-$txId",
                paperAccountId = account.id,
                orderId = txId,
                entryType = "SQUARE_OFF",
                amount = cashToReturn,
                balanceAfter = newCash,
                description = "Square-off ${position.symbol} $absQty Qty (P&L: ${if (pnl >= BigDecimal.ZERO) "+" else ""}₹${pnl.setScale(2, RoundingMode.HALF_EVEN)})"
            )
            paperAccountDao.insertLedgerEntry(ledgerEntry)
            paperAccountDao.deletePosition(position.instrumentId)
        }
    }

    fun resetAccount() {
        viewModelScope.launch {
            paperAccountDao.resetAccountToDefault()
            _isResetDialogOpen.update { false }
        }
    }
}
