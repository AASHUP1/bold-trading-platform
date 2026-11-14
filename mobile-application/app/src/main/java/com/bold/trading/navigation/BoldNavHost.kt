package com.bold.trading.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.algo.AlgoLabViewModel
import com.bold.trading.feature.algo.ui.AlgoLabScreen
import com.bold.trading.feature.charts.ChartViewModel
import com.bold.trading.feature.charts.ui.ChartScreen
import com.bold.trading.feature.dashboard.DashboardViewModel
import com.bold.trading.feature.dashboard.ui.DashboardScreen
import com.bold.trading.feature.onboarding.AuthViewModel
import com.bold.trading.feature.onboarding.ui.OnboardingFlowScreen
import com.bold.trading.feature.options.OptionChainViewModel
import com.bold.trading.feature.papertrading.PaperTerminalViewModel
import com.bold.trading.feature.scanner.ScannerViewModel
import com.bold.trading.feature.watchlist.WatchlistViewModel
import com.bold.trading.feature.watchlist.ui.WatchlistScreen
import com.bold.trading.ui.DiscoveryScreen
import com.bold.trading.ui.ResearchScreen
import com.bold.trading.ui.TradeDeskScreen

const val ROUTE_ONBOARDING = "onboarding"
const val ROUTE_CHART = "chart/{symbol}"

@Composable
fun BoldNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    watchlistViewModel: WatchlistViewModel,
    scannerViewModel: ScannerViewModel,
    chartViewModel: ChartViewModel,
    paperTerminalViewModel: PaperTerminalViewModel,
    optionChainViewModel: OptionChainViewModel,
    algoLabViewModel: AlgoLabViewModel,
    startDestination: String = ROUTE_ONBOARDING,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUTE_ONBOARDING) {
            OnboardingFlowScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(ROUTE_ONBOARDING) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateTab = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onInstrumentClick = { inst ->
                    navController.navigate("chart/${inst.symbol}")
                },
                onSearchClick = {
                    navController.navigate(Screen.Scanner.route)
                }
            )
        }
        composable(Screen.Scanner.route) {
            DiscoveryScreen(
                watchlistViewModel = watchlistViewModel,
                scannerViewModel = scannerViewModel,
                onSimulateTrade = { inst ->
                    paperTerminalViewModel.openOrderTicket(inst)
                    navController.navigate(Screen.TradeDesk.route)
                },
                onOpenChart = { inst ->
                    navController.navigate("chart/${inst.symbol}")
                }
            )
        }
        composable(
            route = ROUTE_CHART,
            arguments = listOf(navArgument("symbol") { type = NavType.StringType })
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: "NIFTY 50"
            ChartScreen(
                symbol = symbol,
                viewModel = chartViewModel,
                onBack = { navController.popBackStack() },
                onSimulateTrade = { inst ->
                    paperTerminalViewModel.openOrderTicket(inst)
                    navController.navigate(Screen.TradeDesk.route)
                }
            )
        }
        composable(Screen.TradeDesk.route) {
            TradeDeskScreen(
                optionChainViewModel = optionChainViewModel,
                paperTerminalViewModel = paperTerminalViewModel,
                onNavigateToWatchlist = {
                    navController.navigate(Screen.Scanner.route)
                }
            )
        }
        composable(Screen.AlgoLab.route) {
            AlgoLabScreen(
                viewModel = algoLabViewModel,
                onDeployToPaper = {
                    val inst = watchlistViewModel.uiState.value.instruments.firstOrNull()
                    if (inst != null) {
                        paperTerminalViewModel.openOrderTicket(inst)
                    }
                    navController.navigate(Screen.TradeDesk.route)
                },
                onCopySignalToPaper = { signal ->
                    val matchingInst = watchlistViewModel.uiState.value.instruments.find { it.symbol == signal.symbol }
                        ?: watchlistViewModel.uiState.value.instruments.firstOrNull()
                    if (matchingInst != null) {
                        paperTerminalViewModel.openOrderTicket(matchingInst)
                    }
                    navController.navigate(Screen.TradeDesk.route)
                }
            )
        }
        composable(Screen.Research.route) {
            ResearchScreen(
                onSimulateTrade = { symbol ->
                    val matchingInst = watchlistViewModel.uiState.value.instruments.find { it.symbol == symbol }
                        ?: watchlistViewModel.uiState.value.instruments.firstOrNull()
                    if (matchingInst != null) {
                        paperTerminalViewModel.openOrderTicket(matchingInst)
                    }
                    navController.navigate(Screen.TradeDesk.route)
                }
            )
        }
    }
}

@Composable
private fun PlaceholderTabScreen(
    title: String,
    subtitle: String,
    tag: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(16.dp)
    ) {
        WatermarkBanner()

        Spacer(modifier = Modifier.height(32.dp))

        BoldCard(
            modifier = Modifier.fillMaxSize(),
            borderColor = BoldColors.BorderSubtle
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StatusBadge(text = tag, status = BadgeStatus.Healthy)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = title,
                        color = BoldColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        color = BoldColors.TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
