package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GlobalJurisdictions
import com.example.data.security.CountryPreferencesManager
import com.example.data.security.CryptoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FinVault", appName)
  }

  @Test
  fun `country legal profile selection updates currency and persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefsManager = CountryPreferencesManager(context)

    // Select United Kingdom
    val ukProfile = GlobalJurisdictions.findByCode("GB")
    prefsManager.saveLegalJurisdictionSelection(ukProfile)

    assertTrue(prefsManager.isSetupCompleted.value)
    assertEquals("GB", prefsManager.currentProfile.value.code)
    assertEquals("£", prefsManager.currentProfile.value.currencySymbol)
    assertEquals("GBP", prefsManager.currentProfile.value.currencyCode)
    assertEquals("£", CryptoManager.activeCurrencySymbol)

    val formatted = CryptoManager.formatMaskedAmount(1250.50, isPrivacyMode = false)
    assertEquals("£1,250.50", formatted)

    // Select Japan
    val japanProfile = GlobalJurisdictions.findByCode("JP")
    prefsManager.saveLegalJurisdictionSelection(japanProfile)
    assertEquals("¥", CryptoManager.activeCurrencySymbol)
    val formattedYen = CryptoManager.formatMaskedAmount(50000.0, isPrivacyMode = false)
    assertEquals("¥50,000.00", formattedYen)
  }
}
