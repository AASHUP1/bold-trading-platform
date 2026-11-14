package com.bold.trading.feature.charts

import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.seed.DatabaseSeeder
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ChartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeDao = FakeInstrumentDao(DatabaseSeeder.INITIAL_INSTRUMENTS)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadInstrument_generates100CandlesAndTechnicalIndicators() = runTest {
        val viewModel = ChartViewModel(fakeDao)
        viewModel.loadInstrument("NIFTY 50")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("NIFTY 50", state.instrument?.symbol)
        assertEquals(100, state.candles.size)
        assertEquals(100, state.ema20.size)
        assertEquals(100, state.ema50.size)
        assertEquals(100, state.vwap.size)
        assertEquals(100, state.rsi14.size)
        assertNotNull(state.inspectedCandle)
    }

    @Test
    fun onIntervalSelected_switchesIntervalAndRegeneratesCandles() = runTest {
        val viewModel = ChartViewModel(fakeDao)
        viewModel.loadInstrument("RELIANCE")
        advanceUntilIdle()

        viewModel.onIntervalSelected(ChartInterval.ONE_DAY)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ChartInterval.ONE_DAY, state.selectedInterval)
        assertEquals(100, state.candles.size)
    }

    @Test
    fun indicatorToggles_switchVisibilityStates() {
        val viewModel = ChartViewModel(fakeDao)

        assertTrue(viewModel.uiState.value.showEma20)
        viewModel.toggleEma20()
        assertFalse(viewModel.uiState.value.showEma20)

        assertFalse(viewModel.uiState.value.showVwap)
        viewModel.toggleVwap()
        assertTrue(viewModel.uiState.value.showVwap)
    }
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
