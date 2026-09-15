package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldIncome,
    onPrimary = Color.Black,
    primaryContainer = EmeraldIncomeContainer,
    onPrimaryContainer = EmeraldIncome,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    secondaryContainer = CyanAccentContainer,
    onSecondaryContainer = CyanAccent,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = GoldAccentContainer,
    onTertiaryContainer = GoldAccent,
    background = VaultDarkBg,
    onBackground = TextPrimary,
    surface = VaultSurface,
    onSurface = TextPrimary,
    surfaceVariant = VaultSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = VaultSurfaceBorder,
    error = RoseExpense,
    onError = Color.White,
    errorContainer = RoseExpenseContainer,
    onErrorContainer = RoseExpense
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldIncome,
    onPrimary = Color.White,
    primaryContainer = EmeraldIncomeContainer,
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = CyanAccent,
    onSecondary = Color.White,
    secondaryContainer = CyanAccentContainer,
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = GoldAccent,
    onTertiary = Color.White,
    tertiaryContainer = GoldAccentContainer,
    onTertiaryContainer = Color(0xFF78350F),
    background = VaultLightBg,
    onBackground = TextLightPrimary,
    surface = VaultLightSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = VaultLightSurfaceVariant,
    onSurfaceVariant = TextLightSecondary,
    outline = VaultLightBorder,
    error = RoseExpense,
    onError = Color.White,
    errorContainer = RoseExpenseContainer,
    onErrorContainer = RoseExpense
)

@Composable
fun FinVaultTheme(
    darkTheme: Boolean = true, // Default to sleek obsidian dark for privacy finance aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FinVaultTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
