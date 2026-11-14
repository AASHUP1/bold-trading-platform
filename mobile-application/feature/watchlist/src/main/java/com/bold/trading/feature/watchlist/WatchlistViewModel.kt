package com.bold.trading.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class WatchlistTab(val displayName: String) {
    CORE_HOLDINGS("Core Holdings"),
    FO_RADAR("F&O Radar"),
    INDICES("Indices")
}

data class WatchlistUiState(
    val selectedTab: WatchlistTab = WatchlistTab.CORE_HOLDINGS,
    val searchQuery: String = "",
    val instruments: List<CachedInstrumentEntity> = emptyList(),
    val filteredInstruments: List<CachedInstrumentEntity> = emptyList(),
    val selectedInstrument: CachedInstrumentEntity? = null,
    val isLoading: Boolean = false
)

class WatchlistViewModel(
    private val instrumentDao: InstrumentDao
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(WatchlistTab.CORE_HOLDINGS)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedInstrument = MutableStateFlow<CachedInstrumentEntity?>(null)

    val uiState: StateFlow<WatchlistUiState> = combine(
        instrumentDao.getAllInstruments(),
        _selectedTab,
        _searchQuery,
        _selectedInstrument
    ) { allInstruments, tab, query, selected ->
        val filteredByTab = when (tab) {
            WatchlistTab.CORE_HOLDINGS -> allInstruments.filter { !it.isIndex }
            WatchlistTab.FO_RADAR -> allInstruments.filter { it.lotSize > 1 || it.symbol.contains("FUT") || it.symbol.contains("NIFTY") }
            WatchlistTab.INDICES -> allInstruments.filter { it.isIndex }
        }

        val finalFiltered = if (query.isBlank()) {
            filteredByTab
        } else {
            val q = query.trim().lowercase()
            filteredByTab.filter {
                it.symbol.lowercase().contains(q) ||
                it.name.lowercase().contains(q) ||
                it.sector.lowercase().contains(q)
            }
        }

        WatchlistUiState(
            selectedTab = tab,
            searchQuery = query,
            instruments = allInstruments,
            filteredInstruments = finalFiltered,
            selectedInstrument = selected,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WatchlistUiState(isLoading = true)
    )

    fun onTabSelected(tab: WatchlistTab) {
        _selectedTab.update { tab }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.update { query }
    }

    fun onInstrumentSelected(instrument: CachedInstrumentEntity?) {
        _selectedInstrument.update { instrument }
    }

    fun onClearSearch() {
        _searchQuery.update { "" }
    }
}
