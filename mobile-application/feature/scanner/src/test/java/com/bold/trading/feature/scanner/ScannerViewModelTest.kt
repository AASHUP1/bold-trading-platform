package com.bold.trading.feature.scanner

import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.seed.DatabaseSeeder
import com.bold.trading.feature.scanner.model.ScannerPreset
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ScannerViewModelTest {

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
    fun initialState_filtersBreakoutStocks() = runTest {
        val viewModel = ScannerViewModel(fakeDao)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ScannerPreset.BREAKOUT, state.selectedPreset)
        assertTrue(state.matchedResults.isNotEmpty())
        assertTrue(state.matchedResults.all { it.instrument.changePercent >= BigDecimal("0.50") })
    }

    @Test
    fun onPresetSelected_volumeSpike_filtersHighVolumeStocks() = runTest {
        val viewModel = ScannerViewModel(fakeDao)
        advanceUntilIdle()

        viewModel.onPresetSelected(ScannerPreset.VOLUME_SPIKE)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ScannerPreset.VOLUME_SPIKE, state.selectedPreset)
        assertTrue(state.matchedResults.all { it.instrument.volume >= 8_000_000L })
    }
}

private class FakeInstrumentDao(private val list: List<CachedInstrumentEntity>) : InstrumentDao {
    override fun getAllInstruments(): Flow<List<CachedInstrumentEntity>> = MutableStateFlow(list)
    override fun getInstrumentBySymbol(symbol: String): Flow<CachedInstrumentEntity?> =
        MutableStateFlow(list.find { it.symbol == symbol })
    override fun searchInstruments(query: String): Flow<List<CachedInstrumentEntity>> =
        MutableStateFlow(list.filter { it.symbol.contains(query, ignoreCase = true) })
    override suspend fun insertInstruments(instruments: List<CachedInstrumentEntity>) {}
    override suspend fun updateQuote(symbol: String, newLtp: BigDecimal, newChange: BigDecimal, newChangePercent: BigDecimal, now: Long) {}
    override suspend fun getCount(): Int = list.size
}
