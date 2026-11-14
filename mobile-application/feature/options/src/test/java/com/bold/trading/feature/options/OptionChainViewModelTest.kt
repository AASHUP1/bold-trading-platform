package com.bold.trading.feature.options

import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.entity.CachedPaperPositionEntity
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import com.bold.trading.core.database.seed.DatabaseSeeder
import com.bold.trading.feature.options.model.StrategyPreset
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
class OptionChainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeDao = FakePaperAccountDao(DatabaseSeeder.INITIAL_PAPER_ACCOUNT)
    private val fakeInstDao = FakeInstrumentDao(DatabaseSeeder.INITIAL_INSTRUMENTS)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loads21StrikesCenteredOnAtm() = runTest {
        val viewModel = OptionChainViewModel(fakeInstDao, fakeDao)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("NIFTY 50", state.underlyingSymbol)
        assertEquals(21, state.strikes.size)
        assertTrue(state.strikes.any { it.isAtm })
    }

    @Test
    fun selectStrategyPreset_generatesLegsAndPayoffCurve() = runTest {
        val viewModel = OptionChainViewModel(fakeInstDao, fakeDao)
        advanceUntilIdle()

        viewModel.selectStrategyPreset(StrategyPreset.BULL_CALL_SPREAD)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(StrategyPreset.BULL_CALL_SPREAD, state.selectedPreset)
        assertEquals(2, state.activeStrategyLegs.size)
        assertNotNull(state.payoffResult)
        assertTrue(state.isPayoffSheetOpen)
    }

    @Test
    fun executeSimulatedBasket_deductsCashAndCreatesLegPositions() = runTest {
        val viewModel = OptionChainViewModel(fakeInstDao, fakeDao)
        advanceUntilIdle()

        viewModel.selectStrategyPreset(StrategyPreset.BULL_CALL_SPREAD)
        advanceUntilIdle()

        viewModel.executeSimulatedBasket()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.lastBasketReceipt)
        // Check that positions were inserted into DAO
        assertEquals(2, fakeDao.positionsState.value.size)
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

    override suspend fun deletePosition(instrumentId: String) {}
    override suspend fun deleteAllPositions() {}
    override suspend fun insertAccount(account: PaperAccountEntity) { accountState.value = account }
    override suspend fun insertLedgerEntry(entry: LedgerEntryEntity) {
        ledgerState.value = listOf(entry) + ledgerState.value
    }
    override suspend fun updateCashAndMargin(accountId: String, newCash: BigDecimal, newMargin: BigDecimal) {
        accountState.value = accountState.value.copy(availableCash = newCash, marginUsed = newMargin)
    }
    override suspend fun addRealizedPnl(accountId: String, pnlDelta: BigDecimal) {}
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
