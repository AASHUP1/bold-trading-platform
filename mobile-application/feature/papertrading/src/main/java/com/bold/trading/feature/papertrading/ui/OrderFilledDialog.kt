package com.bold.trading.feature.papertrading.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.common.formatters.CurrencyFormatter
import com.bold.trading.core.designsystem.components.BadgeStatus
import com.bold.trading.core.designsystem.components.BoldButton
import com.bold.trading.core.designsystem.components.BoldButtonVariant
import com.bold.trading.core.designsystem.components.StatusBadge
import com.bold.trading.core.designsystem.theme.BoldColors
import java.math.BigDecimal

data class FillReceipt(
    val transactionId: String,
    val symbol: String,
    val isBuy: Boolean,
    val quantity: Int,
    val fillPrice: BigDecimal,
    val totalAmount: BigDecimal,
    val timestampEpoch: Long = System.currentTimeMillis()
)

/**
 * Institutional Simulated Order Fill Receipt Dialog.
 * Follows DESIGN.md Section 5.4 & PLAN.md Task 5.3.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFilledDialog(
    receipt: FillReceipt,
    onDismiss: () -> Unit,
    onViewPositions: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BoldColors.SurfaceCard)
                .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success checkmark icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(BoldColors.PrimaryBullish.copy(alpha = 0.15f))
                    .border(1.dp, BoldColors.PrimaryBullish, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Success",
                    tint = BoldColors.PrimaryBullish,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SIMULATED ORDER FILLED",
                color = BoldColors.TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ID: ${receipt.transactionId}",
                color = BoldColors.TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Order Metrics Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BoldColors.SurfaceElevated, RoundedCornerShape(10.dp))
                    .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReceiptRow(
                    label = "Direction",
                    value = if (receipt.isBuy) "BUY / LONG" else "SELL / SHORT",
                    isBull = receipt.isBuy
                )
                ReceiptRow(
                    label = "Instrument",
                    value = receipt.symbol
                )
                ReceiptRow(
                    label = "Quantity",
                    value = "${receipt.quantity} Qty"
                )
                ReceiptRow(
                    label = "Execution Price",
                    value = CurrencyFormatter.formatRupees(receipt.fillPrice)
                )
                ReceiptRow(
                    label = "Total Value",
                    value = CurrencyFormatter.formatRupees(receipt.totalAmount)
                )
                ReceiptRow(
                    label = "Simulated Brokerage",
                    value = "₹0.00 (100% Free)",
                    isFree = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Simulation Watermark Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BoldColors.AccentWarning.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                    .border(1.dp, BoldColors.AccentWarning.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "● SIMULATION ONLY / Paper Ledger Updated",
                    color = BoldColors.AccentWarning,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BoldButton(
                    text = "View Positions",
                    onClick = onViewPositions,
                    variant = BoldButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth()
                )

                BoldButton(
                    text = "Done",
                    onClick = onDismiss,
                    variant = BoldButtonVariant.Outlined,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBull: Boolean? = null,
    isFree: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = BoldColors.TextSecondary,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = when {
                isFree -> BoldColors.PrimaryBullish
                isBull == true -> BoldColors.PrimaryBullish
                isBull == false -> BoldColors.AccentBearish
                else -> BoldColors.TextPrimary
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
