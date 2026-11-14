package com.bold.trading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.core.designsystem.theme.BoldTheme
import com.bold.trading.feature.algo.AlgoLabViewModel
import com.bold.trading.feature.charts.ChartViewModel
import com.bold.trading.feature.dashboard.DashboardViewModel
import com.bold.trading.feature.onboarding.AuthViewModel
import com.bold.trading.feature.onboarding.data.DataStoreUserPreferencesRepository
import com.bold.trading.feature.options.OptionChainViewModel
import com.bold.trading.feature.papertrading.PaperTerminalViewModel
import com.bold.trading.feature.scanner.ScannerViewModel
import com.bold.trading.feature.watchlist.WatchlistViewModel
import com.bold.trading.navigation.BoldNavHost
import com.bold.trading.navigation.BottomNavBar
import com.bold.trading.navigation.ROUTE_ONBOARDING
import com.bold.trading.navigation.Screen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as BoldApplication
        val instrumentDao = app.database.instrumentDao()
        val paperAccountDao = app.database.paperAccountDao()
        val prefsRepo = DataStoreUserPreferencesRepository(applicationContext)

        val authViewModel = AuthViewModel(prefsRepo)
        val dashboardViewModel = DashboardViewModel(instrumentDao, paperAccountDao)
        val watchlistViewModel = WatchlistViewModel(instrumentDao)
        val scannerViewModel = ScannerViewModel(instrumentDao)
        val chartViewModel = ChartViewModel(instrumentDao, app.database.candleDao())
        val paperTerminalViewModel = PaperTerminalViewModel(paperAccountDao, instrumentDao)
        val optionChainViewModel = OptionChainViewModel(instrumentDao, paperAccountDao)
        val algoLabViewModel = AlgoLabViewModel()

        setContent {
            BoldTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ROUTE_ONBOARDING

                // Check if user has already completed onboarding
                val userPrefs by prefsRepo.userPreferencesFlow.collectAsState(initial = null)
                val startDest = if (userPrefs?.isOnboarded == true) Screen.Home.route else ROUTE_ONBOARDING

                // Only render bottom bar on the 5 core terminal tabs
                val isBottomBarVisible = Screen.bottomTabs.any { it.route == currentRoute }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BoldColors.BgApp,
                    bottomBar = {
                        if (isBottomBarVisible) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { screen ->
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Home.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    BoldNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel,
                        watchlistViewModel = watchlistViewModel,
                        scannerViewModel = scannerViewModel,
                        chartViewModel = chartViewModel,
                        paperTerminalViewModel = paperTerminalViewModel,
                        optionChainViewModel = optionChainViewModel,
                        algoLabViewModel = algoLabViewModel,
                        startDestination = startDest,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
