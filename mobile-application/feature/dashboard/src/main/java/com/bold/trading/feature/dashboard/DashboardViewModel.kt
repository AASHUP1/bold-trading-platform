package com.bold.trading.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

data class DashboardUiState(
    val nifty: CachedInstrumentEntity? = null,
    val bankNifty: CachedInstrumentEntity? = null,
    val sensex: CachedInstrumentEntity? = null,
    val paperAccount: PaperAccountEntity? = null,
    val topGainers: List<CachedInstrumentEntity> = emptyList(),
    val topLosers: List<CachedInstrumentEntity> = emptyList(),
    val isFeedHealthy: Boolean = true,
    val feedLatencyMs: Long = 135L,
    val isLoading: Boolean = false
)

class DashboardViewModel(
    instrumentDao: InstrumentDao,
    paperAccountDao: PaperAccountDao
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        instrumentDao.getAllInstruments(),
        paperAccountDao.getPaperAccount()
    ) { instruments, account ->
        val nifty = instruments.find { it.symbol == "NIFTY 50" }
        val bankNifty = instruments.find { it.symbol == "BANKNIFTY" }
        val sensex = instruments.find { it.symbol == "SENSEX" }

        val equities = instruments.filter { !it.isIndex }
        val gainers = equities.filter { it.changePercent > BigDecimal.ZERO }.sortedByDescending { it.changePercent }.take(3)
        val losers = equities.filter { it.changePercent < BigDecimal.ZERO }.sortedBy { it.changePercent }.take(3)

        DashboardUiState(
            nifty = nifty,
            bankNifty = bankNifty,
            sensex = sensex,
            paperAccount = account,
            topGainers = gainers,
            topLosers = losers,
            isFeedHealthy = true,
            feedLatencyMs = 135L,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )
}
