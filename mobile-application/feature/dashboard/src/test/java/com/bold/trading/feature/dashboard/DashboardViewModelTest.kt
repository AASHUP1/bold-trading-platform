package com.bold.trading.feature.dashboard

import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import com.bold.trading.core.database.seed.DatabaseSeeder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_combinesInstrumentsAndAccountData() = runTest {
        val fakeInstrumentDao = FakeInstrumentDao(DatabaseSeeder.INITIAL_INSTRUMENTS)
        val fakePaperAccountDao = FakePaperAccountDao(DatabaseSeeder.INITIAL_PAPER_ACCOUNT)

        val viewModel = DashboardViewModel(fakeInstrumentDao, fakePaperAccountDao)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.nifty)
        assertEquals("NIFTY 50", state.nifty?.symbol)
        assertNotNull(state.bankNifty)
        assertEquals("BANKNIFTY", state.bankNifty?.symbol)
        assertNotNull(state.paperAccount)
        assertEquals(BigDecimal("1000000.00"), state.paperAccount?.virtualCashBalance)
        assertTrue(state.topGainers.isNotEmpty())
    }
}

private class FakeInstrumentDao(private val list: List<CachedInstrumentEntity>) : InstrumentDao {
    private val flow = MutableStateFlow(list)

    override fun getAllInstruments(): Flow<List<CachedInstrumentEntity>> = flow
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

private class FakePaperAccountDao(private val account: PaperAccountEntity) : PaperAccountDao {
    private val flow = MutableStateFlow<PaperAccountEntity?>(account)

    override fun getPaperAccount(): Flow<PaperAccountEntity?> = flow
    override suspend fun insertPaperAccount(account: PaperAccountEntity) {}
    override suspend fun updateCashBalance(newCash: BigDecimal, newMargin: BigDecimal) {}
    override suspend fun updatePnl(realizedPnl: BigDecimal, unrealizedPnl: BigDecimal) {}
    override suspend fun resetAccount(defaultCapital: BigDecimal) {}
}
