package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CountryLegalProfile
import com.example.data.model.GlobalJurisdictions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CountryPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentProfile = MutableStateFlow(loadProfile())
    val currentProfile: StateFlow<CountryLegalProfile> = _currentProfile.asStateFlow()

    private val _isSetupCompleted = MutableStateFlow(prefs.getBoolean(KEY_IS_SETUP_COMPLETED, false))
    val isSetupCompleted: StateFlow<Boolean> = _isSetupCompleted.asStateFlow()

    init {
        // Sync active currency symbol into CryptoManager
        CryptoManager.activeCurrencySymbol = _currentProfile.value.currencySymbol
    }

    private fun loadProfile(): CountryLegalProfile {
        val code = prefs.getString(KEY_COUNTRY_CODE, "US") ?: "US"
        return GlobalJurisdictions.findByCode(code)
    }

    fun isCompleted(): Boolean {
        return prefs.getBoolean(KEY_IS_SETUP_COMPLETED, false)
    }

    fun getLegalAcceptedTimestamp(): Long {
        return prefs.getLong(KEY_LEGAL_ACCEPTED_TIMESTAMP, 0L)
    }

    fun saveLegalJurisdictionSelection(profile: CountryLegalProfile): CountryLegalProfile {
        val now = System.currentTimeMillis()
        prefs.edit()
            .putString(KEY_COUNTRY_CODE, profile.code)
            .putString(KEY_COUNTRY_NAME, profile.name)
            .putString(KEY_CURRENCY_CODE, profile.currencyCode)
            .putString(KEY_CURRENCY_SYMBOL, profile.currencySymbol)
            .putLong(KEY_LEGAL_ACCEPTED_TIMESTAMP, now)
            .putBoolean(KEY_IS_SETUP_COMPLETED, true)
            .apply()

        _currentProfile.value = profile
        _isSetupCompleted.value = true
        CryptoManager.activeCurrencySymbol = profile.currencySymbol
        return profile
    }

    fun resetSetupForReconfiguration() {
        prefs.edit()
            .putBoolean(KEY_IS_SETUP_COMPLETED, false)
            .apply()
        _isSetupCompleted.value = false
    }

    companion object {
        private const val PREFS_NAME = "finvault_jurisdiction_prefs"
        private const val KEY_IS_SETUP_COMPLETED = "is_setup_completed"
        private const val KEY_COUNTRY_CODE = "selected_country_code"
        private const val KEY_COUNTRY_NAME = "selected_country_name"
        private const val KEY_CURRENCY_CODE = "selected_currency_code"
        private const val KEY_CURRENCY_SYMBOL = "selected_currency_symbol"
        private const val KEY_LEGAL_ACCEPTED_TIMESTAMP = "legal_accepted_timestamp"
    }
}
