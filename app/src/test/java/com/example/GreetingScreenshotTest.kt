package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.FinancialSummary
import com.example.data.model.TimeframeFilter
import com.example.ui.screens.HeroFinancialCard
import com.example.ui.theme.FinVaultTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      FinVaultTheme {
        HeroFinancialCard(
          summary = FinancialSummary(
            totalIncome = 4500.0,
            totalExpense = 1420.0,
            netSavings = 3080.0,
            savingsRate = 68.4,
            transactionCount = 12
          ),
          timeframe = TimeframeFilter.MONTHLY,
          isPrivacyMode = false
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
