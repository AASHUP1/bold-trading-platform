package com.bold.trading.feature.papertrading.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.BoldCard
import com.bold.trading.core.designsystem.components.NumberStepper
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.components.WatermarkBanner
import com.bold.trading.core.designsystem.theme.BoldColors
import java.math.BigDecimal
import java.math.RoundingMode

enum class OrderType(val label: String) {
    MARKET("Market"),
    LIMIT("Limit"),
    SL_M("SL-M")
}

/**
 * Screen S14: Simulated Order Ticket Bottom Sheet.
 * Follows DESIGN.md Section 5.4 & PLAN.md Task 5.1.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTicketBottomSheet(
    instrument: CachedInstrumentEntity,
    availableCash: BigDecimal,
    virtualCapital: BigDecimal,
    onDismiss: () -> Unit,
    onSubmitOrder: (isBuy: Boolean, orderType: OrderType, quantity: Int, price: BigDecimal, stopLoss: BigDecimal?, target: BigDecimal?) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var isBuy by remember { mutableStateOf(true) }
    var orderType by remember { mutableStateOf(OrderType.MARKET) }
    var quantity by remember { mutableIntStateOf(instrument.lotSize) }

    var limitPriceText by remember { mutableStateOf(instrument.lastTradedPrice.toPlainString()) }
    var stopLossText by remember {
        val defaultSl = if (isBuy) instrument.lastTradedPrice.multiply(BigDecimal("0.99")) else instrument.lastTradedPrice.multiply(BigDecimal("1.01"))
        mutableStateOf(defaultSl.setScale(2, RoundingMode.HALF_EVEN).toPlainString())
    }
    var targetText by remember {
        val defaultTgt = if (isBuy) instrument.lastTradedPrice.multiply(BigDecimal("1.025")) else instrument.lastTradedPrice.multiply(BigDecimal("0.975"))
        mutableStateOf(defaultTgt.setScale(2, RoundingMode.HALF_EVEN).toPlainString())
    }

    val activePrice = if (orderType == OrderType.LIMIT) {
        limitPriceText.toBigDecimalOrNull() ?: instrument.lastTradedPrice
    } else {
        instrument.lastTradedPrice
    }

    val totalRequiredCapital = activePrice.multiply(BigDecimal(quantity))
    val stopLossVal = stopLossText.toBigDecimalOrNull()
    val targetVal = targetText.toBigDecimalOrNull()

    // Risk amount calculation
    val riskAmount = if (stopLossVal != null && stopLossVal > BigDecimal.ZERO) {
        val perShareRisk = if (isBuy) (activePrice - stopLossVal).abs() else (stopLossVal - activePrice).abs()
        perShareRisk.multiply(BigDecimal(quantity))
    } else {
        BigDecimal.ZERO
    }

    val maxAllowedRisk = virtualCapital.multiply(BigDecimal("0.01"))
    val isRiskBreached = riskAmount > maxAllowedRisk && stopLossVal != null
    val isCashInsufficient = totalRequiredCapital > availableCash

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BoldColors.SurfaceCard,
        contentColor = BoldColors.TextPrimary,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BoldColors.TextMuted)
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            WatermarkBanner()
            Spacer(modifier = Modifier.height(14.dp))

            // Direction Switcher: BUY (#38D996) vs SELL (#FF657A)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BoldColors.SurfaceElevated)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isBuy) BoldColors.PrimaryBullish else Color.Transparent)
                        .clickable { isBuy = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BUY / LONG",
                        color = if (isBuy) BoldColors.BgApp else BoldColors.TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (!isBuy) BoldColors.AccentBearish else Color.Transparent)
                        .clickable { isBuy = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SELL / SHORT",
                        color = if (!isBuy) BoldColors.BgApp else BoldColors.TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instrument Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = instrument.symbol,
                        color = BoldColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Lot Size: ${instrument.lotSize} · ${instrument.exchange}",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatRupees(instrument.lastTradedPrice),
                        color = BoldColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    StatusBadge(text = "Market Feed", status = BadgeStatus.Healthy)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Type Segmented Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderType.entries.forEach { type ->
                    val isSelected = orderType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BoldColors.SurfaceElevated else BoldColors.SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { orderType = type }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type.label,
                            color = if (isSelected) BoldColors.PrimaryBullish else BoldColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity Stepper (Increments by lotSize)
            Text(
                text = "QUANTITY (SHARES / CONTRACTS)",
                color = BoldColors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            NumberStepper(
                value = quantity.toLong(),
                onValueChange = { quantity = it.toInt() },
                step = instrument.lotSize.toLong(),
                minValue = instrument.lotSize.toLong(),
                maxValue = (instrument.lotSize * 1000).toLong(),
                suffix = " Qty",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stop Loss & Target Inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stop Loss
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STOP LOSS (₹)",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OrderNumericField(
                        value = stopLossText,
                        onValueChange = { stopLossText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Target
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TARGET (₹)",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OrderNumericField(
                        value = targetText,
                        onValueChange = { targetText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Margin & Defensive Risk Summary
            BoldCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isRiskBreached || isCashInsufficient) BoldColors.AccentBearish else BoldColors.BorderSubtle
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SummaryLine("Required Virtual Cash", CurrencyFormatter.formatRupees(totalRequiredCapital))
                    SummaryLine("Available Balance", CurrencyFormatter.formatRupees(availableCash))
                    SummaryLine("Estimated Risk at SL", CurrencyFormatter.formatRupees(riskAmount), isRiskBreached)
                    SummaryLine("Max 1.0% Risk Budget", CurrencyFormatter.formatRupees(maxAllowedRisk))
                    SummaryLine("Simulated Brokerage & Taxes", "₹0.00 (100% Free)", isBull = true)
                }
            }

            if (isRiskBreached) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Risk Guardrail: Trade risk exceeds 1.0% limit (₹${maxAllowedRisk.setScale(2, RoundingMode.HALF_EVEN)}). Tighten SL or reduce quantity.",
                    color = BoldColors.AccentBearish,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            if (isCashInsufficient) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Insufficient Balance: Required ₹${totalRequiredCapital.setScale(2, RoundingMode.HALF_EVEN)} exceeds available cash.",
                    color = BoldColors.AccentBearish,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            val buttonText = if (isBuy) "Place Simulated Buy Order" else "Place Simulated Sell Order"
            val canSubmit = !isRiskBreached && !isCashInsufficient && quantity > 0

            BoldButton(
                text = buttonText,
                onClick = {
                    onSubmitOrder(
                        isBuy,
                        orderType,
                        quantity,
                        activePrice,
                        stopLossVal,
                        targetVal
                    )
                },
                variant = if (isBuy) BoldButtonVariant.Primary else BoldButtonVariant.Destructive,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OrderNumericField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BoldColors.SurfaceElevated)
            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = BoldColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            ),
            cursorBrush = SolidColor(BoldColors.PrimaryBullish),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    isAlert: Boolean = false,
    isBull: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = BoldColors.TextSecondary, fontSize = 11.sp)
        Text(
            text = value,
            color = when {
                isAlert -> BoldColors.AccentBearish
                isBull -> BoldColors.PrimaryBullish
                else -> BoldColors.TextPrimary
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}

private val Color = androidx.compose.ui.graphics.Color
