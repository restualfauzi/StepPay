package com.steppay.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    primaryContainer = SoftBlueBg,
    onPrimaryContainer = PrimaryBlue,
    secondary = BrightBlue,
    onSecondary = SurfaceWhite,
    tertiary = SuccessGreen,
    onTertiary = SurfaceWhite,
    background = BackgroundLight,
    onBackground = PrimaryText,
    surface = SurfaceWhite,
    onSurface = PrimaryText,
    surfaceVariant = BorderLight,
    onSurfaceVariant = SecondaryText,
    outline = BorderLight,
    error = DangerRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    primaryContainer = Navy,
    onPrimaryContainer = SurfaceWhite,
    secondary = BrightBlue,
    onSecondary = SurfaceWhite,
    tertiary = SuccessGreen,
    background = DarkNavy,
    onBackground = SurfaceWhite,
    surface = Navy,
    onSurface = SurfaceWhite,
    surfaceVariant = DarkNavy,
    onSurfaceVariant = SecondaryText,
    outline = SecondaryText,
    error = DangerRed
)

@Composable
fun StepPayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}