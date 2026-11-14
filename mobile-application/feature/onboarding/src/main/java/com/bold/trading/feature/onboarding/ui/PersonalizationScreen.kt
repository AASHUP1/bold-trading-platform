package com.bold.trading.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import com.bold.trading.feature.onboarding.AuthUiState

val universeOptions = listOf("NSE", "BSE", "Cash", "Futures", "Options")

data class DensityConfig(
    val name: String,
    val description: String,
    val badge: String
)

val densityOptions = listOf(
    DensityConfig("Beginner", "Simplified single-order ticket & basic candlestick chart", "Visual"),
    DensityConfig("Intermediate", "Multi-strike option chains & technical indicator overlays", "Balanced"),
    DensityConfig("Advanced", "Institutional Greeks, tick tape, scanner & algo rule lab", "Terminal")
)

val riskOptions = listOf(0.5f, 1.0f, 2.0f)

/**
 * Screen S03: Workspace Personalization & Risk Appetite
 * Follows DESIGN.md Section 6: S03 & PRD Section 6
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalizationScreen(
    state: AuthUiState,
    onUniverseToggled: (String) -> Unit,
    onDensitySelected: (String) -> Unit,
    onRiskBudgetChanged: (Float) -> Unit,
    onCompletePersonalization: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            WatermarkBanner()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WORKSPACE CONFIGURATION",
                color = BoldColors.PrimaryBullish,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tailor Your Trading Desk",
                color = BoldColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select your asset universe and risk boundaries. All features are free and risk-isolated.",
                color = BoldColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Trading Universe Selection
            Text(
                text = "1. TRADING UNIVERSE",
                color = BoldColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                universeOptions.forEach { universe ->
                    val isSelected = state.selectedUniverses.contains(universe)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onUniverseToggled(universe)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = BoldColors.PrimaryBullish,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = universe,
                                color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Interface Density Selector
            Text(
                text = "2. INTERFACE DENSITY",
                color = BoldColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                densityOptions.forEach { config ->
                    val isSelected = state.selectedDensity == config.name
                    BoldCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDensitySelected(config.name) },
                        borderColor = if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = config.name,
                                        color = BoldColors.TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StatusBadge(
                                        text = config.badge,
                                        status = if (isSelected) BadgeStatus.Healthy else BadgeStatus.Neutral
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = config.description,
                                    color = BoldColors.TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(BoldColors.PrimaryBullish, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = BoldColors.BgApp,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Virtual Capital & Defensive Risk Budget
            Text(
                text = "3. DEFENSIVE RISK BUDGET",
                color = BoldColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(10.dp))

            BoldCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BoldColors.BorderSubtle
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = null,
                                tint = BoldColors.PrimaryBullish,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Virtual Capital Pre-funded",
                                color = BoldColors.TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "₹10,00,000.00",
                            color = BoldColors.PrimaryBullish,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Max Risk Per Trade (Defensive Guardrail):",
                        color = BoldColors.TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        riskOptions.forEach { risk ->
                            val isSelected = state.riskBudgetPercent == risk
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onRiskBudgetChanged(risk) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$risk%",
                                    color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Confirm Action
        BoldButton(
            text = if (state.isLoading) "Configuring Workspace..." else "Create My Workspace",
            onClick = onCompletePersonalization,
            variant = BoldButtonVariant.Primary,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
