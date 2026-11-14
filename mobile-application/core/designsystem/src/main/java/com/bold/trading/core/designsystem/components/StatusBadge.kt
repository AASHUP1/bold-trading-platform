package com.bold.trading.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

enum class BadgeStatus {
    Healthy,
    Degraded,
    Danger,
    Neutral
}

@Composable
fun StatusBadge(
    text: String,
    status: BadgeStatus = BadgeStatus.Neutral,
    showDot: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, dotColor) = when (status) {
        BadgeStatus.Healthy -> Triple(BoldColors.BullishFaint, BoldColors.PrimaryBullish, BoldColors.PrimaryBullish)
        BadgeStatus.Degraded -> Triple(BoldColors.WarningFaint, BoldColors.AccentWarning, BoldColors.AccentWarning)
        BadgeStatus.Danger -> Triple(BoldColors.BearishFaint, BoldColors.AccentBearish, BoldColors.AccentBearish)
        BadgeStatus.Neutral -> Triple(BoldColors.SurfaceElevated, BoldColors.TextSecondary, BoldColors.TextSecondary)
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .border(1.dp, dotColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
    }
}
