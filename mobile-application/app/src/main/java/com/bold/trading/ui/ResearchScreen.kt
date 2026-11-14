package com.bold.trading.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import java.math.BigDecimal

enum class ResearchSubTab(val title: String) {
    FUNDAMENTALS("Fundamentals (S09)"),
    AI_ASSISTANT("AI Research (S10)")
}

data class PeerMetric(
    val symbol: String,
    val name: String,
    val peRatio: String,
    val pbRatio: String,
    val roe: String,
    val marketCap: String
)

data class ResearchInsight(
    val id: String,
    val query: String,
    val symbol: String,
    val verdict: String,
    val status: BadgeStatus,
    val summary: String,
    val bullets: List<String>,
    val sources: List<String>
)

/**
 * Screen S09 & S10: Institutional Research & AI Analyst Assistant.
 * Follows DESIGN.md Section 6: S09 & S10.
 */
@Composable
fun ResearchScreen(
    onSimulateTrade: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ResearchSubTab.FUNDAMENTALS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(horizontal = 14.dp)
    ) {
        WatermarkBanner(modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(10.dp))

        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "INSTITUTIONAL RESEARCH",
                    color = BoldColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "NSE / BSE Fundamentals & Sourced AI Insights",
                    color = BoldColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
            StatusBadge(text = "100% Free", status = BadgeStatus.Healthy)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Segmented Switcher Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BoldColors.SurfaceElevated)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ResearchSubTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) BoldColors.SurfaceCard else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) BoldColors.BorderSubtle else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { activeTab = tab }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeTab) {
            ResearchSubTab.FUNDAMENTALS -> FundamentalsView(onSimulateTrade = onSimulateTrade)
            ResearchSubTab.AI_ASSISTANT -> AiResearchAssistantView(onSimulateTrade = onSimulateTrade)
        }
    }
}

@Composable
private fun FundamentalsView(
    onSimulateTrade: (String) -> Unit
) {
    val peers = listOf(
        PeerMetric("RELIANCE", "Reliance Ind.", "27.4x", "2.1x", "12.8%", "₹20.1L Cr"),
        PeerMetric("TCS", "Tata Consultancy", "31.2x", "13.4x", "48.2%", "₹14.9L Cr"),
        PeerMetric("HDFCBANK", "HDFC Bank", "18.6x", "2.8x", "16.4%", "₹12.5L Cr"),
        PeerMetric("INFY", "Infosys Ltd", "28.5x", "8.2x", "31.5%", "₹7.7L Cr")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            BoldCard(
                borderColor = BoldColors.BorderSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RELIANCE INDUSTRIES LTD",
                                color = BoldColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Sector: Oil & Gas / Telecom / Retail",
                                color = BoldColors.TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        StatusBadge(text = "Large Cap", status = BadgeStatus.Healthy)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Valuation Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(label = "P/E RATIO", value = "27.4x")
                        MetricItem(label = "P/B RATIO", value = "2.1x")
                        MetricItem(label = "ROE", value = "12.8%")
                        MetricItem(label = "ROCE", value = "11.2%")
                        MetricItem(label = "D/E", value = "0.41x")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Financial Snapshot
                    Text(
                        text = "QUARTERLY SNAPSHOT (Q2 FY25)",
                        color = BoldColors.TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FinancialRowItem("Revenue", "₹2,35,481 Cr", "+6.2% YoY")
                        FinancialRowItem("EBITDA", "₹43,934 Cr", "+4.8% YoY")
                        FinancialRowItem("Net Profit", "₹19,323 Cr", "+9.4% YoY")
                    }
                }
            }
        }

        item {
            Text(
                text = "PEER VALUATION MATRIX",
                color = BoldColors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        items(peers) { peer ->
            BoldCard(
                borderColor = BoldColors.BorderSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = peer.symbol,
                            color = BoldColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = peer.name,
                            color = BoldColors.TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "M.Cap: ${peer.marketCap} | ROE: ${peer.roe}",
                            color = BoldColors.PrimaryBullish,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "P/E ${peer.peRatio}",
                            color = BoldColors.TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "P/B ${peer.pbRatio}",
                            color = BoldColors.TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BoldColors.SurfaceElevated)
                                .clickable { onSimulateTrade(peer.symbol) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Simulate",
                                color = BoldColors.PrimaryBullish,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiResearchAssistantView(
    onSimulateTrade: (String) -> Unit
) {
    var queryText by remember { mutableStateOf("") }

    val sampleQueries = listOf(
        "How resilient are Infosys Q2 margins?",
        "Compare Reliance vs Tata Motors FCF",
        "Explain HDFC Bank credit-deposit ratio",
        "NIFTY 50 valuation vs 10Y median"
    )

    val sampleInsights = listOf(
        ResearchInsight(
            id = "res-01",
            query = "How resilient are Infosys Q2 margins?",
            symbol = "INFY",
            verdict = "BULLISH",
            status = BadgeStatus.Healthy,
            summary = "Infosys demonstrated operating margin expansion of 30 bps QoQ to 21.1% driven by Project Maximus automation and large deal cost takeouts.",
            bullets = listOf(
                "Large deal TCV recorded at $2.4B with 52% net new pipeline.",
                "Attrition normalized to institutional baseline of 12.9%.",
                "FY25 constant currency revenue guidance raised to 3.75% - 4.50%."
            ),
            sources = listOf("Q2 FY25 Earnings Call Transcript", "NSE Financial Filing 17-Oct-2024")
        ),
        ResearchInsight(
            id = "res-02",
            query = "Compare Reliance vs Tata Motors FCF",
            symbol = "RELIANCE",
            verdict = "NEUTRAL",
            status = BadgeStatus.Degraded,
            summary = "Reliance 5G capex cycle has peaked, shifting FCF towards net cash accretion from FY26, whereas Tata Motors JLR order book maintains robust cash conversion.",
            bullets = listOf(
                "Jio ARPU increased to ₹195.1 with 5G monetization ramp.",
                "Retail square footage expansion stabilized at disciplined IRR thresholds.",
                "Oil-to-Chemicals GRM margins face cyclical refining margin compression."
            ),
            sources = listOf("RIL Q2 FY25 Investor Presentation", "Credit Suisse Institutional Research")
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Search / Query Box
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Ask AI Analyst (e.g. margin trends, FCF)...",
                        color = BoldColors.TextSecondary,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI Research",
                        tint = BoldColors.PrimaryBullish,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BoldColors.PrimaryBullish,
                    unfocusedBorderColor = BoldColors.BorderSubtle,
                    focusedContainerColor = BoldColors.SurfaceCard,
                    unfocusedContainerColor = BoldColors.SurfaceCard,
                    focusedTextColor = BoldColors.TextPrimary,
                    unfocusedTextColor = BoldColors.TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        }

        item {
            // Quick Suggestion Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sampleQueries.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(BoldColors.SurfaceElevated)
                            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(14.dp))
                            .clickable { queryText = suggestion }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = suggestion,
                            color = BoldColors.TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        items(sampleInsights) { insight ->
            BoldCard(
                borderColor = BoldColors.BorderSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = insight.symbol,
                            color = BoldColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        StatusBadge(text = insight.verdict, status = insight.status)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${insight.query}\"",
                        color = BoldColors.PrimaryBullish,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = insight.summary,
                        color = BoldColors.TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    insight.bullets.forEach { bullet ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "• ",
                                color = BoldColors.PrimaryBullish,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = bullet,
                                color = BoldColors.TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sources
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SOURCES CITED:",
                                color = BoldColors.TextSecondary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            insight.sources.forEach { src ->
                                Text(
                                    text = "› $src",
                                    color = BoldColors.TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BoldColors.SurfaceElevated)
                                .clickable { onSimulateTrade(insight.symbol) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Simulate Trade",
                                color = BoldColors.PrimaryBullish,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = BoldColors.TextSecondary,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = BoldColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun FinancialRowItem(label: String, value: String, growth: String) {
    Column {
        Text(
            text = label,
            color = BoldColors.TextSecondary,
            fontSize = 10.sp
        )
        Text(
            text = value,
            color = BoldColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = growth,
            color = BoldColors.PrimaryBullish,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
