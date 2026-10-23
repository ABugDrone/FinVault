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

    private val _userRole = MutableStateFlow(prefs.getString(KEY_USER_ROLE, "INDIVIDUAL") ?: "INDIVIDUAL")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _activeLedgerId = MutableStateFlow(prefs.getString(KEY_ACTIVE_LEDGER_ID, "primary_ledger") ?: "primary_ledger")
    val activeLedgerId: StateFlow<String> = _activeLedgerId.asStateFlow()

    init {
        // Sync active currency symbol into CryptoManager
        CryptoManager.activeCurrencySymbol = _currentProfile.value.currencySymbol
    }

    private fun loadProfile(): CountryLegalProfile {
        val savedCode = prefs.getString(KEY_COUNTRY_CODE, null)
        return if (!savedCode.isNullOrBlank()) {
            GlobalJurisdictions.findByCode(savedCode)
        } else {
            GlobalJurisdictions.detectDefaultCountry()
        }
    }

    fun isCompleted(): Boolean {
        return prefs.getBoolean(KEY_IS_SETUP_COMPLETED, false)
    }

    fun getLegalAcceptedTimestamp(): Long {
        return prefs.getLong(KEY_LEGAL_ACCEPTED_TIMESTAMP, 0L)
    }

    fun saveLegalJurisdictionSelection(profile: CountryLegalProfile, role: String = "INDIVIDUAL"): CountryLegalProfile {
        val now = System.currentTimeMillis()
        val defaultLedgerId = if (role == "BUSINESS") "business_store_1" else "primary_ledger"
        prefs.edit()
            .putString(KEY_COUNTRY_CODE, profile.code)
            .putString(KEY_COUNTRY_NAME, profile.name)
            .putString(KEY_CURRENCY_CODE, profile.currencyCode)
            .putString(KEY_CURRENCY_SYMBOL, profile.currencySymbol)
            .putString(KEY_USER_ROLE, role)
            .putString(KEY_ACTIVE_LEDGER_ID, defaultLedgerId)
            .putLong(KEY_LEGAL_ACCEPTED_TIMESTAMP, now)
            .putBoolean(KEY_IS_SETUP_COMPLETED, true)
            .apply()

        _currentProfile.value = profile
        _userRole.value = role
        _activeLedgerId.value = defaultLedgerId
        _isSetupCompleted.value = true
        CryptoManager.activeCurrencySymbol = profile.currencySymbol
        return profile
    }

    fun setActiveLedgerId(ledgerId: String) {
        prefs.edit().putString(KEY_ACTIVE_LEDGER_ID, ledgerId).apply()
        _activeLedgerId.value = ledgerId
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
        private const val KEY_USER_ROLE = "selected_user_role"
        private const val KEY_ACTIVE_LEDGER_ID = "active_ledger_id"
    }
}
