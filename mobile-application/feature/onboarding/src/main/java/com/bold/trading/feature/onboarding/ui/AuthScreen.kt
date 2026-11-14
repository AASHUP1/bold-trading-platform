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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.bold.trading.feature.onboarding.AuthUiState
import com.bold.trading.feature.onboarding.OnboardingStep

/**
 * Screen S02: Mobile OTP Authentication Slice
 * Follows DESIGN.md Section 6: S02 & PRD Section 6
 */
@Composable
fun AuthScreen(
    state: AuthUiState,
    onPhoneNumberChanged: (String) -> Unit,
    onSendOtpClicked: () -> Unit,
    onOtpChanged: (String) -> Unit,
    onFillDemoOtp: () -> Unit,
    onVerifyOtpClicked: () -> Unit,
    onResendOtp: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoldColors.BgApp)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            WatermarkBanner()
            Spacer(modifier = Modifier.height(16.dp))

            // Back button and header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BoldColors.TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.step == OnboardingStep.PhoneEntry) "SIGN IN / REGISTER" else "VERIFY IDENTITY",
                    color = BoldColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.step == OnboardingStep.PhoneEntry) {
                // Phone Number Entry View
                Text(
                    text = "Enter your mobile number",
                    color = BoldColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We will send a 6-digit one-time password to verify your institutional paper trading terminal.",
                    color = BoldColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Mobile Input Field with +91 Prefix
                BoldCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (state.errorMessage != null) BoldColors.AccentBearish else BoldColors.BorderSubtle
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+91",
                            color = BoldColors.TextSecondary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(BoldColors.BorderSubtle)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        BasicTextField(
                            value = state.phoneNumber,
                            onValueChange = onPhoneNumberChanged,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = BoldColors.TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(BoldColors.PrimaryBullish),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (state.phoneNumber.isEmpty()) {
                                    Text(
                                        text = "98765 43210",
                                        color = BoldColors.TextMuted,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.errorMessage,
                        color = BoldColors.AccentBearish,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Demo hint
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BoldColors.SurfaceCard, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(text = "Sandbox", status = BadgeStatus.Healthy)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Any 10-digit number is accepted for testing.",
                        color = BoldColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }

            } else {
                // OTP Code Verification View
                Text(
                    text = "Enter verification code",
                    color = BoldColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A 6-digit OTP was dispatched to +91 ${state.phoneNumber}.",
                    color = BoldColors.TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 6-digit OTP Boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(6) { index ->
                        val char = state.otpCode.getOrNull(index)?.toString() ?: ""
                        val isCurrentBox = state.otpCode.length == index
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BoldColors.SurfaceCard)
                                .border(
                                    width = 1.dp,
                                    color = if (isCurrentBox) BoldColors.PrimaryBullish else BoldColors.BorderSubtle,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                color = BoldColors.TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Hidden text field for keyboard capture
                BasicTextField(
                    value = state.otpCode,
                    onValueChange = onOtpChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(0.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(color = androidx.compose.ui.graphics.Color.Transparent),
                    cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Transparent)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = BoldColors.AccentBearish,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Timer & Demo Quick Fill Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val minutes = state.otpSecondsRemaining / 60
                    val seconds = state.otpSecondsRemaining % 60
                    val timerText = String.format("%02d:%02d", minutes, seconds)

                    Text(
                        text = if (state.otpSecondsRemaining > 0) "Expires in $timerText" else "Code expired",
                        color = if (state.otpSecondsRemaining > 0) BoldColors.TextSecondary else BoldColors.AccentBearish,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "Fill Demo OTP (123456)",
                        color = BoldColors.PrimaryBullish,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onFillDemoOtp() }
                    )
                }
            }
        }

        // Bottom CTA Action
        Column(modifier = Modifier.fillMaxWidth()) {
            if (state.step == OnboardingStep.PhoneEntry) {
                BoldButton(
                    text = "Get Verification Code",
                    onClick = onSendOtpClicked,
                    variant = BoldButtonVariant.Primary,
                    enabled = state.phoneNumber.length == 10,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                BoldButton(
                    text = if (state.isLoading) "Verifying..." else "Verify & Continue",
                    onClick = onVerifyOtpClicked,
                    variant = BoldButtonVariant.Primary,
                    enabled = state.otpCode.length == 6 && !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.otpSecondsRemaining == 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    BoldButton(
                        text = "Resend Code",
                        onClick = onResendOtp,
                        variant = BoldButtonVariant.Outlined,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
