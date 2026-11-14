package com.bold.trading.feature.onboarding.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bold_user_prefs")

data class UserPreferences(
    val isOnboarded: Boolean = false,
    val userPhone: String = "",
    val selectedUniverses: Set<String> = setOf("NSE", "Cash", "Futures"),
    val uiDensity: String = "Advanced",
    val riskBudgetPercent: Float = 1.0f,
    val virtualCapital: Long = 1_000_000L
)

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun setOnboardingCompleted(
        phone: String,
        universes: Set<String>,
        density: String,
        riskPercent: Float
    )
    suspend fun resetOnboarding()
}

class DataStoreUserPreferencesRepository(private val context: Context) : UserPreferencesRepository {

    private object PreferencesKeys {
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val SELECTED_UNIVERSES = stringSetPreferencesKey("selected_universes")
        val UI_DENSITY = stringPreferencesKey("ui_density")
        val RISK_BUDGET_PERCENT = floatPreferencesKey("risk_budget_percent")
        val VIRTUAL_CAPITAL = longPreferencesKey("virtual_capital")
    }

    override val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            isOnboarded = prefs[PreferencesKeys.IS_ONBOARDED] ?: false,
            userPhone = prefs[PreferencesKeys.USER_PHONE] ?: "",
            selectedUniverses = prefs[PreferencesKeys.SELECTED_UNIVERSES] ?: setOf("NSE", "Cash", "Futures"),
            uiDensity = prefs[PreferencesKeys.UI_DENSITY] ?: "Advanced",
            riskBudgetPercent = prefs[PreferencesKeys.RISK_BUDGET_PERCENT] ?: 1.0f,
            virtualCapital = prefs[PreferencesKeys.VIRTUAL_CAPITAL] ?: 1_000_000L
        )
    }

    override suspend fun setOnboardingCompleted(
        phone: String,
        universes: Set<String>,
        density: String,
        riskPercent: Float
    ) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_ONBOARDED] = true
            prefs[PreferencesKeys.USER_PHONE] = phone
            prefs[PreferencesKeys.SELECTED_UNIVERSES] = universes
            prefs[PreferencesKeys.UI_DENSITY] = density
            prefs[PreferencesKeys.RISK_BUDGET_PERCENT] = riskPercent
            prefs[PreferencesKeys.VIRTUAL_CAPITAL] = 1_000_000L
        }
    }

    override suspend fun resetOnboarding() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_ONBOARDED] = false
        }
    }
}
