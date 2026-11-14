package com.bold.trading.feature.watchlist

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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModelTest {

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
    fun initialState_filtersCoreHoldingsByDefault() = runTest {
        val viewModel = WatchlistViewModel(fakeDao)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(WatchlistTab.CORE_HOLDINGS, state.selectedTab)
        // Indices should be filtered out from Core Holdings
        assertTrue(state.filteredInstruments.none { it.isIndex })
        assertTrue(state.filteredInstruments.any { it.symbol == "RELIANCE" })
    }

    @Test
    fun onTabSelected_indices_showsOnlyBenchmarkIndices() = runTest {
        val viewModel = WatchlistViewModel(fakeDao)
        advanceUntilIdle()

        viewModel.onTabSelected(WatchlistTab.INDICES)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(WatchlistTab.INDICES, state.selectedTab)
        assertTrue(state.filteredInstruments.all { it.isIndex })
        assertTrue(state.filteredInstruments.any { it.symbol == "NIFTY 50" })
    }

    @Test
    fun onSearchQueryChanged_filtersMatchingSymbols() = runTest {
        val viewModel = WatchlistViewModel(fakeDao)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("infy")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredInstruments.size)
        assertEquals("INFY", state.filteredInstruments.first().symbol)
    }

    @Test
    fun onInstrumentSelected_updatesSelectedInstrument() = runTest {
        val viewModel = WatchlistViewModel(fakeDao)
        advanceUntilIdle()

        val target = DatabaseSeeder.INITIAL_INSTRUMENTS.first()
        viewModel.onInstrumentSelected(target)
        advanceUntilIdle()

        assertEquals(target.symbol, viewModel.uiState.value.selectedInstrument?.symbol)

        viewModel.onInstrumentSelected(null)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.selectedInstrument)
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
