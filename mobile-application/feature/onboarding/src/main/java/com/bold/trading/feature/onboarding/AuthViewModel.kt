package com.bold.trading.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bold.trading.feature.onboarding.data.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OnboardingStep {
    Carousel,
    PhoneEntry,
    OtpVerification,
    Personalization
}

data class AuthUiState(
    val step: OnboardingStep = OnboardingStep.Carousel,
    val carouselIndex: Int = 0,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val otpSecondsRemaining: Int = 120,
    val isOtpTimerRunning: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedUniverses: Set<String> = setOf("NSE", "Cash", "Futures"),
    val selectedDensity: String = "Advanced",
    val riskBudgetPercent: Float = 1.0f,
    val virtualCapital: Long = 1_000_000L
)

sealed interface AuthUiEvent {
    data object NavigateToHome : AuthUiEvent
}

class AuthViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AuthUiEvent>()
    val eventFlow: SharedFlow<AuthUiEvent> = _eventFlow.asSharedFlow()

    private var timerJob: Job? = null

    fun onCarouselPageChanged(index: Int) {
        _uiState.update { it.copy(carouselIndex = index) }
    }

    fun onGetStartedClicked() {
        _uiState.update { it.copy(step = OnboardingStep.PhoneEntry, errorMessage = null) }
    }

    fun onAlreadyHaveAccountClicked() {
        _uiState.update { it.copy(step = OnboardingStep.PhoneEntry, errorMessage = null) }
    }

    fun onPhoneNumberChanged(phone: String) {
        val sanitized = phone.filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(phoneNumber = sanitized, errorMessage = null) }
    }

    fun onSendOtpClicked() {
        val phone = _uiState.value.phoneNumber
        if (phone.length < 10) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid 10-digit Indian mobile number") }
            return
        }

        _uiState.update {
            it.copy(
                step = OnboardingStep.OtpVerification,
                errorMessage = null,
                otpCode = "",
                otpSecondsRemaining = 120,
                isOtpTimerRunning = true
            )
        }
        startOtpCountdown()
    }

    fun onOtpChanged(otp: String) {
        val sanitized = otp.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = sanitized, errorMessage = null) }
    }

    fun onFillDemoOtp() {
        _uiState.update { it.copy(otpCode = "123456", errorMessage = null) }
    }

    fun onVerifyOtpClicked() {
        val otp = _uiState.value.otpCode
        if (otp.length != 6) {
            _uiState.update { it.copy(errorMessage = "Please enter the complete 6-digit verification code") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(400) // Brief mock network verification latency

            timerJob?.cancel()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOtpTimerRunning = false,
                    step = OnboardingStep.Personalization
                )
            }
        }
    }

    fun onResendOtp() {
        if (_uiState.value.otpSecondsRemaining > 0) return
        _uiState.update {
            it.copy(
                otpSecondsRemaining = 120,
                isOtpTimerRunning = true,
                errorMessage = null
            )
        }
        startOtpCountdown()
    }

    fun onUniverseToggled(universe: String) {
        val current = _uiState.value.selectedUniverses.toMutableSet()
        if (current.contains(universe)) {
            if (current.size > 1) { // Require at least one universe
                current.remove(universe)
            }
        } else {
            current.add(universe)
        }
        _uiState.update { it.copy(selectedUniverses = current) }
    }

    fun onDensitySelected(density: String) {
        _uiState.update { it.copy(selectedDensity = density) }
    }

    fun onRiskBudgetChanged(risk: Float) {
        _uiState.update { it.copy(riskBudgetPercent = risk) }
    }

    fun onCompletePersonalization() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            preferencesRepository.setOnboardingCompleted(
                phone = "+91" + state.phoneNumber,
                universes = state.selectedUniverses,
                density = state.selectedDensity,
                riskPercent = state.riskBudgetPercent
            )
            _uiState.update { it.copy(isLoading = false) }
            _eventFlow.emit(AuthUiEvent.NavigateToHome)
        }
    }

    fun onBackToPhoneClicked() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                step = OnboardingStep.PhoneEntry,
                isOtpTimerRunning = false,
                errorMessage = null
            )
        }
    }

    fun onBackToCarouselClicked() {
        _uiState.update { it.copy(step = OnboardingStep.Carousel, errorMessage = null) }
    }

    private fun startOtpCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.otpSecondsRemaining > 0) {
                delay(1000L)
                _uiState.update {
                    it.copy(otpSecondsRemaining = (it.otpSecondsRemaining - 1).coerceAtLeast(0))
                }
            }
            _uiState.update { it.copy(isOtpTimerRunning = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
