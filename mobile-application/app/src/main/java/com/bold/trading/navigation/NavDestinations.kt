package com.bold.trading.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes and bottom bar destination definitions.
 * Follows DESIGN.md Section 5.5: 5 Standard Tabs.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home)
    data object Scanner : Screen("scanner", "Scanner", Icons.Filled.Search)
    data object TradeDesk : Screen("trade_desk", "Trade Desk", Icons.Filled.AutoGraph)
    data object AlgoLab : Screen("algo_lab", "Algo Lab", Icons.Filled.Analytics)
    data object Research : Screen("research", "Research", Icons.Filled.Psychology)

    companion object {
        val bottomTabs = listOf(Home, Scanner, TradeDesk, AlgoLab, Research)
    }
}
