package com.bold.trading.feature.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bold.trading.feature.onboarding.AuthUiEvent
import com.bold.trading.feature.onboarding.AuthViewModel
import com.bold.trading.feature.onboarding.OnboardingStep
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingFlowScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthUiEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    when (state.step) {
        OnboardingStep.Carousel -> {
            OnboardingScreen(
                onGetStarted = { viewModel.onGetStartedClicked() },
                onAlreadyHaveAccount = { viewModel.onAlreadyHaveAccountClicked() },
                modifier = modifier
            )
        }
        OnboardingStep.PhoneEntry,
        OnboardingStep.OtpVerification -> {
            AuthScreen(
                state = state,
                onPhoneNumberChanged = { viewModel.onPhoneNumberChanged(it) },
                onSendOtpClicked = { viewModel.onSendOtpClicked() },
                onOtpChanged = { viewModel.onOtpChanged(it) },
                onFillDemoOtp = { viewModel.onFillDemoOtp() },
                onVerifyOtpClicked = { viewModel.onVerifyOtpClicked() },
                onResendOtp = { viewModel.onResendOtp() },
                onBackClicked = {
                    if (state.step == OnboardingStep.OtpVerification) {
                        viewModel.onBackToPhoneClicked()
                    } else {
                        viewModel.onBackToCarouselClicked()
                    }
                },
                modifier = modifier
            )
        }
        OnboardingStep.Personalization -> {
            PersonalizationScreen(
                state = state,
                onUniverseToggled = { viewModel.onUniverseToggled(it) },
                onDensitySelected = { viewModel.onDensitySelected(it) },
                onRiskBudgetChanged = { viewModel.onRiskBudgetChanged(it) },
                onCompletePersonalization = { viewModel.onCompletePersonalization() },
                modifier = modifier
            )
        }
    }
}
