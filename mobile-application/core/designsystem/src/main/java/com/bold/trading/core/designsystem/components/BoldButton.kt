package com.bold.trading.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bold.trading.core.designsystem.theme.BoldColors

enum class ButtonVariant {
    Primary,
    Secondary,
    Destructive,
    Outline,
    Outlined
}

typealias BoldButtonVariant = ButtonVariant

@Composable
fun BoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val containerColor = when (variant) {
        ButtonVariant.Primary -> BoldColors.PrimaryBullish
        ButtonVariant.Secondary -> BoldColors.SurfaceCard
        ButtonVariant.Destructive -> BoldColors.AccentBearish
        ButtonVariant.Outline, ButtonVariant.Outlined -> Color.Transparent
    }

    val contentColor = when (variant) {
        ButtonVariant.Primary, ButtonVariant.Destructive -> BoldColors.BgApp
        ButtonVariant.Secondary, ButtonVariant.Outline, ButtonVariant.Outlined -> BoldColors.TextPrimary
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
