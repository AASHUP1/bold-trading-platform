package com.bold.trading.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.theme.BoldColors

/**
 * Institutional Number Stepper mandated by DESIGN.md Section 5.2.
 * Enforces 48dp minimum touch target compliance and tabular font rendering.
 */
@Composable
fun NumberStepper(
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    step: Long = 1L,
    minValue: Long = 1L,
    maxValue: Long = 100_000L,
    prefix: String = "",
    suffix: String = ""
) {
    val canDecrement = value - step >= minValue
    val canIncrement = value + step <= maxValue

    Row(
        modifier = modifier
            .height(48.dp)
            .background(BoldColors.SurfaceCard, RoundedCornerShape(10.dp))
            .border(1.dp, BoldColors.BorderSubtle, RoundedCornerShape(10.dp))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Decrement button with 48dp touch target
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (canDecrement) BoldColors.SurfaceElevated else Color.Transparent)
                .clickable(
                    enabled = canDecrement,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = BoldColors.TextPrimary)
                ) {
                    if (canDecrement) onValueChange(value - step)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                color = if (canDecrement) BoldColors.TextPrimary else BoldColors.TextMuted,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Center numerical value with tabular font
        Text(
            text = "$prefix$value$suffix",
            modifier = Modifier
                .defaultMinSize(minWidth = 72.dp)
                .padding(horizontal = 12.dp),
            color = BoldColors.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )

        // Increment button with 48dp touch target
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (canIncrement) BoldColors.SurfaceElevated else Color.Transparent)
                .clickable(
                    enabled = canIncrement,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = BoldColors.TextPrimary)
                ) {
                    if (canIncrement) onValueChange(value + step)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = if (canIncrement) BoldColors.TextPrimary else BoldColors.TextMuted,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
