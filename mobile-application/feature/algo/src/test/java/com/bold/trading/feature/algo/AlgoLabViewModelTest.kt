package com.bold.trading.feature.algo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class AlgoLabViewModelTest {

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
    fun initialState_computesHistoricalBacktest() = runTest {
        val viewModel = AlgoLabViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.backtestResult)
        assertEquals(250, state.backtestResult?.equityCurve?.size)
        assertTrue((state.backtestResult?.winRate ?: 0f) > 50f)
        assertTrue(state.signals.isNotEmpty())
    }

    @Test
    fun addAndRemoveCondition_updatesStrategyConditions() = runTest {
        val viewModel = AlgoLabViewModel()
        advanceUntilIdle()

        val initialCount = viewModel.uiState.value.currentStrategy.conditions.size
        viewModel.addCondition()
        assertEquals(initialCount + 1, viewModel.uiState.value.currentStrategy.conditions.size)

        val lastConditionId = viewModel.uiState.value.currentStrategy.conditions.last().id
        viewModel.removeCondition(lastConditionId)
        assertEquals(initialCount, viewModel.uiState.value.currentStrategy.conditions.size)
    }

    @Test
    fun selectTab_switchesTabsCorrectly() = runTest {
        val viewModel = AlgoLabViewModel()
        advanceUntilIdle()

        viewModel.selectTab(AlgoLabTab.SIGNALS)
        assertEquals(AlgoLabTab.SIGNALS, viewModel.uiState.value.currentTab)

        viewModel.selectTab(AlgoLabTab.BUILDER)
        assertEquals(AlgoLabTab.BUILDER, viewModel.uiState.value.currentTab)
    }

    @Test
    fun updateTimeframe_updatesStrategyTimeframe() = runTest {
        val viewModel = AlgoLabViewModel()
        advanceUntilIdle()

        viewModel.updateTimeframe("1H")
        assertEquals("1H", viewModel.uiState.value.currentStrategy.timeframe)
    }
}
