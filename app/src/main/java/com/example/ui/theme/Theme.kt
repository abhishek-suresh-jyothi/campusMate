package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Surface,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryFixedDim,
    onPrimary = OnPrimaryFixed,
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryFixedDim,
    onSecondary = OnSecondaryFixed,
    secondaryContainer = Secondary,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryFixedDim,
    onTertiary = OnTertiaryFixed,
    background = Color(0xFF10141D),
    onBackground = Color(0xFFE2E7FF),
    surface = Color(0xFF10141D),
    onSurface = Color(0xFFE2E7FF),
    surfaceVariant = Color(0xFF283044),
    onSurfaceVariant = Color(0xFFC7C4D7),
    surfaceContainerLowest = Color(0xFF151924),
    surfaceContainerLow = Color(0xFF1A1F2D),
    surfaceContainer = Color(0xFF202637),
    surfaceContainerHigh = Color(0xFF272F43),
    surfaceContainerHighest = Color(0xFF303950),
    inverseSurface = Color(0xFFE2E7FF),
    inverseOnSurface = Color(0xFF10141D),
    outline = Color(0xFF8E90A6),
    outlineVariant = Color(0xFF464554),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun CampusMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CampusMateTypography,
        content = content
    )
}
