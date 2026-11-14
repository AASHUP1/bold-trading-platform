package com.bold.trading.feature.papertrading

import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.entity.CachedPaperPositionEntity
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import com.bold.trading.core.database.seed.DatabaseSeeder
import com.bold.trading.feature.papertrading.ui.OrderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class PaperTerminalViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakePaperAccountDao
    private lateinit var fakeInstDao: FakeInstrumentDao

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakePaperAccountDao(DatabaseSeeder.INITIAL_PAPER_ACCOUNT)
        fakeInstDao = FakeInstrumentDao(DatabaseSeeder.INITIAL_INSTRUMENTS)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun executeSimulatedOrder_validOrder_updatesAccountAndGeneratesReceipt() = runTest {
        val viewModel = PaperTerminalViewModel(fakeDao, fakeInstDao)
        advanceUntilIdle()

        val inst = DatabaseSeeder.INITIAL_INSTRUMENTS.first()
        viewModel.executeSimulatedOrder(
            instrument = inst,
            isBuy = true,
            orderType = OrderType.MARKET,
            quantity = 10,
            price = BigDecimal("3000.00"),
            stopLoss = BigDecimal("2950.00"),
            target = BigDecimal("3100.00")
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.lastFillReceipt)
        assertEquals("BUY / LONG", if (state.lastFillReceipt!!.isBuy) "BUY / LONG" else "SELL")
        assertEquals(10, state.lastFillReceipt!!.quantity)
        assertEquals(BigDecimal("30000.00"), state.lastFillReceipt!!.totalAmount)

        // Cash deducted from ₹10,00,000 -> ₹9,70,000
        assertEquals(BigDecimal("970000.00"), fakeDao.accountState.value.availableCash)
        // Position created
        assertEquals(1, fakeDao.positionsState.value.size)
    }

    @Test
    fun squareOffPosition_liquidatesPositionAndCreditsCash() = runTest {
        val viewModel = PaperTerminalViewModel(fakeDao, fakeInstDao)
        advanceUntilIdle()

        val position = CachedPaperPositionEntity(
            instrumentId = "inst-reliance",
            symbol = "RELIANCE",
            quantity = 10,
            averageEntryPrice = BigDecimal("2900.00"),
            currentLtp = BigDecimal("3000.00"),
            unrealizedPnl = BigDecimal("1000.00")
        )
        fakeDao.insertOrUpdatePosition(position)
        advanceUntilIdle()

        viewModel.squareOffPosition(position)
        advanceUntilIdle()

        assertTrue(fakeDao.positionsState.value.isEmpty())
        assertTrue(fakeDao.ledgerState.value.any { it.entryType == "SQUARE_OFF" })
    }
}

private class FakePaperAccountDao(initialAccount: PaperAccountEntity) : PaperAccountDao {
    val accountState = MutableStateFlow(initialAccount)
    val positionsState = MutableStateFlow<List<CachedPaperPositionEntity>>(emptyList())
    val ledgerState = MutableStateFlow<List<LedgerEntryEntity>>(listOf(DatabaseSeeder.INITIAL_LEDGER_ENTRY))

    override fun getPaperAccount(accountId: String): Flow<PaperAccountEntity?> = accountState
    override fun getLedgerEntries(accountId: String): Flow<List<LedgerEntryEntity>> = ledgerState
    override fun getAllPositions(): Flow<List<CachedPaperPositionEntity>> = positionsState

    override suspend fun getPositionByInstrumentId(instrumentId: String): CachedPaperPositionEntity? =
        positionsState.value.find { it.instrumentId == instrumentId }

    override suspend fun insertOrUpdatePosition(position: CachedPaperPositionEntity) {
        val current = positionsState.value.toMutableList()
        current.removeAll { it.instrumentId == position.instrumentId }
        current.add(position)
        positionsState.value = current
    }

    override suspend fun deletePosition(instrumentId: String) {
        positionsState.value = positionsState.value.filter { it.instrumentId != instrumentId }
    }

    override suspend fun deleteAllPositions() {
        positionsState.value = emptyList()
    }

    override suspend fun insertAccount(account: PaperAccountEntity) {
        accountState.value = account
    }

    override suspend fun insertLedgerEntry(entry: LedgerEntryEntity) {
        ledgerState.value = listOf(entry) + ledgerState.value
    }

    override suspend fun updateCashAndMargin(accountId: String, newCash: BigDecimal, newMargin: BigDecimal) {
        accountState.value = accountState.value.copy(availableCash = newCash, marginUsed = newMargin)
    }

    override suspend fun addRealizedPnl(accountId: String, pnlDelta: BigDecimal) {
        accountState.value = accountState.value.copy(realizedPnl = accountState.value.realizedPnl.add(pnlDelta))
    }

    override suspend fun getCount(): Int = 1
}

private class FakeInstrumentDao(private val list: List<CachedInstrumentEntity>) : InstrumentDao {
    override fun getAllInstruments(): Flow<List<CachedInstrumentEntity>> = MutableStateFlow(list)
    override fun getInstrumentBySymbol(symbol: String): Flow<CachedInstrumentEntity?> =
        MutableStateFlow(list.find { it.symbol == symbol })
    override fun searchInstruments(query: String): Flow<List<CachedInstrumentEntity>> =
        MutableStateFlow(list.filter { it.symbol.contains(query, ignoreCase = true) })
    override suspend fun insertInstruments(instruments: List<CachedInstrumentEntity>) {}
    override suspend fun updateQuote(
        symbol: String,
        newLtp: BigDecimal,
        newChange: BigDecimal,
        newChangePercent: BigDecimal,
        now: Long
    ) {}
    override suspend fun getCount(): Int = list.size
}
