package com.bold.trading.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.theme.BoldColors

/**
 * Institutional Watermark Banner strictly mandated by PRD Section 7 & DESIGN.md Section 5.7.
 * Prevents paper simulation from being mistaken for real broker execution.
 */
@Composable
fun WatermarkBanner(
    modifier: Modifier = Modifier,
    text: String = "ILLUSTRATIVE / Live quote not connected",
    showBadge: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(
                color = BoldColors.AccentWarning.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                color = BoldColors.AccentWarning.copy(alpha = 0.40f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(BoldColors.AccentWarning, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "● $text",
                color = BoldColors.AccentWarning,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }

        if (showBadge) {
            StatusBadge(
                text = "100% Free",
                status = BadgeStatus.Healthy
            )
        }
    }
}
