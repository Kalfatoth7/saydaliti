package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = HealthOsPrimaryContainer,
    onPrimary = HealthOsOnPrimaryContainer,
    primaryContainer = HealthOsPrimary,
    onPrimaryContainer = HealthOsPrimaryContainer,
    secondary = HealthOsSecondary,
    onSecondary = HealthOsOnSecondary,
    secondaryContainer = HealthOsSecondaryContainer,
    onSecondaryContainer = HealthOsOnSecondaryContainer,
    tertiary = HealthOsTertiaryContainer,
    onTertiary = HealthOsOnTertiaryContainer,
    background = HealthOsDarkBackground,
    onBackground = HealthOsDarkOnBackground,
    surface = HealthOsDarkSurface,
    onSurface = HealthOsDarkOnSurface,
    surfaceVariant = HealthOsDarkSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = HealthOsPrimary,
    onPrimary = HealthOsOnPrimary,
    primaryContainer = HealthOsPrimaryContainer,
    onPrimaryContainer = HealthOsOnPrimaryContainer,
    secondary = HealthOsSecondary,
    onSecondary = HealthOsOnSecondary,
    secondaryContainer = HealthOsSecondaryContainer,
    onSecondaryContainer = HealthOsOnSecondaryContainer,
    tertiary = HealthOsTertiary,
    onTertiary = HealthOsOnTertiary,
    tertiaryContainer = HealthOsTertiaryContainer,
    onTertiaryContainer = HealthOsOnTertiaryContainer,
    background = HealthOsLightBackground,
    onBackground = HealthOsLightOnBackground,
    surface = HealthOsLightSurface,
    onSurface = HealthOsLightOnSurface,
    surfaceVariant = HealthOsLightSurfaceVariant
)

@Composable
fun HealthOsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force false to ensure premium custom theme is used instead of system wallpaper colors
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

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
