package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NavyPrimaryDark,
    onPrimary = NavyOnPrimaryDark,
    primaryContainer = NavyPrimaryContainerDark,
    onPrimaryContainer = NavyOnPrimaryContainerDark,
    secondary = SoftBlueSecondaryDark,
    onSecondary = SoftBlueOnSecondaryDark,
    tertiary = MedicalAccent,
    background = HealthBackgroundDark,
    onBackground = HealthOnBackgroundDark,
    surface = HealthSurfaceDark,
    onSurface = HealthOnSurfaceDark,
    surfaceVariant = HealthSurfaceVariantDark,
    onSurfaceVariant = HealthOnSurfaceVariantDark,
    outline = HealthOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = NavyOnPrimary,
    primaryContainer = NavyPrimaryContainer,
    onPrimaryContainer = NavyOnPrimaryContainer,
    secondary = SoftBlueSecondary,
    onSecondary = SoftBlueOnSecondary,
    tertiary = MedicalAccent,
    background = HealthBackground,
    onBackground = HealthOnBackground,
    surface = HealthSurface,
    onSurface = HealthOnSurface,
    surfaceVariant = HealthSurfaceVariant,
    onSurfaceVariant = HealthOnSurfaceVariant,
    outline = HealthOutline,
    outlineVariant = HealthOutlineVariant
)

@Composable
fun SmartEscalationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep medical brand colors consistent by default
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
