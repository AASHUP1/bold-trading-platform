package com.bold.trading.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors

data class OnboardingSlide(
    val badge: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val statLabel: String,
    val statValue: String
)

val onboardingSlides = listOf(
    OnboardingSlide(
        badge = "HIGH-SPEED DATA",
        title = "Institutional Market Discovery",
        subtitle = "Sub-second NSE/BSE tick feeds, index market breadth, and interactive technical charts with zero retail delay.",
        icon = Icons.Filled.TrendingUp,
        statLabel = "Feed Latency",
        statValue = "< 150 ms"
    ),
    OnboardingSlide(
        badge = "ALGO STRATEGY LAB",
        title = "Quantitative Strategy Testing",
        subtitle = "Formulate multi-factor momentum and breakout conditions. Backtest against historical bars 100% free with zero fees.",
        icon = Icons.Filled.Analytics,
        statLabel = "Backtest Access",
        statValue = "100% Free"
    ),
    OnboardingSlide(
        badge = "ZERO CAPITAL RISK",
        title = "Risk-Isolated Paper Trading",
        subtitle = "Practice position sizing and risk discipline with ₹10,00,000 virtual capital permanently isolated from live broker gateways.",
        icon = Icons.Filled.Security,
        statLabel = "Virtual Ledger",
        statValue = "₹10,00,000.00"
    )
)

/**
 * Screen S01: Splash & Onboarding Carousel
 * Follows DESIGN.md Section 6: S01 & PRD Section 6
 */
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onAlreadyHaveAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { onboardingSlides.size })

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header & PRD Illustrative Watermark
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WatermarkBanner()
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(BoldColors.PrimaryBullish, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BOLD TERMINAL",
                    color = BoldColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            }
        }

        // Central Feature Carousel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) { page ->
                val slide = onboardingSlides[page]
                BoldCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    borderColor = BoldColors.BorderSubtle
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusBadge(
                            text = slide.badge,
                            status = BadgeStatus.Healthy
                        )

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BoldColors.SurfaceElevated)
                                .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = slide.icon,
                                contentDescription = null,
                                tint = BoldColors.PrimaryBullish,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = slide.title,
                                color = BoldColors.TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = slide.subtitle,
                                color = BoldColors.TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Institutional stat pill
                        Row(
                            modifier = Modifier
                                .background(BoldColors.BgApp, RoundedCornerShape(8.dp))
                                .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${slide.statLabel}: ",
                                color = BoldColors.TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = slide.statValue,
                                color = BoldColors.PrimaryBullish,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Carousel Pager Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingSlides.size) { iteration ->
                    val isCurrent = pagerState.currentPage == iteration
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(6.dp)
                            .width(if (isCurrent) 22.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) BoldColors.PrimaryBullish else BoldColors.BorderSubtle)
                    )
                }
            }
        }

        // Bottom CTAs & Regulatory Disclaimer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoldButton(
                text = "Get started",
                onClick = onGetStarted,
                variant = BoldButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            BoldButton(
                text = "I already have an account",
                onClick = onAlreadyHaveAccount,
                variant = BoldButtonVariant.Outlined,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Trading Indian equities and derivatives involves market risk. BOLD paper accounts use simulated ledger allocations and never route live broker orders.",
                color = BoldColors.TextMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
