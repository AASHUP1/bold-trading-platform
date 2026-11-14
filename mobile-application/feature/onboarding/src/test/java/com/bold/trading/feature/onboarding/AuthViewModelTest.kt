package com.bold.trading.feature.onboarding

import com.bold.trading.feature.onboarding.data.UserPreferences
import com.bold.trading.feature.onboarding.data.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockPrefsRepo: FakeUserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockPrefsRepo = FakeUserPreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_defaultsToCarouselAnd120Seconds() {
        val viewModel = AuthViewModel(mockPrefsRepo)
        val state = viewModel.uiState.value

        assertEquals(OnboardingStep.Carousel, state.step)
        assertEquals(0, state.carouselIndex)
        assertEquals("", state.phoneNumber)
        assertEquals("", state.otpCode)
        assertEquals(120, state.otpSecondsRemaining)
        assertFalse(state.isOtpTimerRunning)
        assertNull(state.errorMessage)
    }

    @Test
    fun onGetStartedClicked_transitionsToPhoneEntry() {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onGetStartedClicked()

        assertEquals(OnboardingStep.PhoneEntry, viewModel.uiState.value.step)
    }

    @Test
    fun onPhoneNumberChanged_sanitizesDigitsAndLimitsTo10() {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onPhoneNumberChanged("+91 98765-43210 99")

        assertEquals("9876543210", viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun onSendOtpClicked_shortPhone_setsError() {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onPhoneNumberChanged("98765")
        viewModel.onSendOtpClicked()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(OnboardingStep.Carousel, viewModel.uiState.value.step)
    }

    @Test
    fun onSendOtpClicked_validPhone_startsCountdownAndTransitionsToOtp() = runTest {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onGetStartedClicked()
        viewModel.onPhoneNumberChanged("9876543210")
        viewModel.onSendOtpClicked()

        val state = viewModel.uiState.value
        assertEquals(OnboardingStep.OtpVerification, state.step)
        assertTrue(state.isOtpTimerRunning)
        assertEquals(120, state.otpSecondsRemaining)
    }

    @Test
    fun onFillDemoOtp_populates123456() {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onFillDemoOtp()

        assertEquals("123456", viewModel.uiState.value.otpCode)
    }

    @Test
    fun onVerifyOtpClicked_success_transitionsToPersonalization() = runTest {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onGetStartedClicked()
        viewModel.onPhoneNumberChanged("9876543210")
        viewModel.onSendOtpClicked()
        viewModel.onFillDemoOtp()
        viewModel.onVerifyOtpClicked()

        advanceTimeBy(500)
        assertEquals(OnboardingStep.Personalization, viewModel.uiState.value.step)
        assertFalse(viewModel.uiState.value.isOtpTimerRunning)
    }

    @Test
    fun onUniverseToggled_doesNotRemoveLastUniverse() {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onUniverseToggled("NSE")
        viewModel.onUniverseToggled("Cash")
        viewModel.onUniverseToggled("Futures")

        // At least one must remain
        assertTrue(viewModel.uiState.value.selectedUniverses.isNotEmpty())
    }

    @Test
    fun onCompletePersonalization_savesPreferencesAndEmitsEvent() = runTest {
        val viewModel = AuthViewModel(mockPrefsRepo)
        viewModel.onPhoneNumberChanged("9876543210")
        viewModel.onDensitySelected("Advanced")
        viewModel.onRiskBudgetChanged(2.0f)
        viewModel.onCompletePersonalization()

        advanceTimeBy(100)
        assertEquals("+919876543210", mockPrefsRepo.savedPhone)
        assertEquals("Advanced", mockPrefsRepo.savedDensity)
        assertEquals(2.0f, mockPrefsRepo.savedRisk)
        assertTrue(mockPrefsRepo.savedIsOnboarded)
    }
}

/**
 * Pure Kotlin in-memory fake for UserPreferencesRepository.
 */
private class FakeUserPreferencesRepository : UserPreferencesRepository {

    var savedIsOnboarded: Boolean = false
    var savedPhone: String = ""
    var savedUniverses: Set<String> = emptySet()
    var savedDensity: String = ""
    var savedRisk: Float = 0f

    private val _prefs = MutableStateFlow(UserPreferences())
    override val userPreferencesFlow: Flow<UserPreferences> = _prefs.asStateFlow()

    override suspend fun setOnboardingCompleted(
        phone: String,
        universes: Set<String>,
        density: String,
        riskPercent: Float
    ) {
        savedIsOnboarded = true
        savedPhone = phone
        savedUniverses = universes
        savedDensity = density
        savedRisk = riskPercent
        _prefs.value = UserPreferences(
            isOnboarded = true,
            userPhone = phone,
            selectedUniverses = universes,
            uiDensity = density,
            riskBudgetPercent = riskPercent
        )
    }

    override suspend fun resetOnboarding() {
        savedIsOnboarded = false
        _prefs.value = _prefs.value.copy(isOnboarded = false)
    }
}
