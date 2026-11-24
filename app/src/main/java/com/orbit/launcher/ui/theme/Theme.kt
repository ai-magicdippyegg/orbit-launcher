package com.orbit.launcher.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.orbit.launcher.data.PrefsRepository

/**
 * Material 3 color scheme resolved from prefs. When the user picks an accent
 * preset, the scheme is built around [ACCENT_PRESETS]; otherwise we fall back
 * to dynamic wallpaper color (Android 12+) or a sensible default.
 */
@Composable
fun OrbitLauncherTheme(
    themeMode: PrefsRepository.ThemeMode = PrefsRepository.ThemeMode.SYSTEM,
    accentPresetIndex: Int = -1,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        PrefsRepository.ThemeMode.SYSTEM -> systemDark
        PrefsRepository.ThemeMode.LIGHT -> false
        PrefsRepository.ThemeMode.DARK, PrefsRepository.ThemeMode.AMOLED -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        accentPresetIndex in ACCENT_PRESETS.indices -> {
            val preset = ACCENT_PRESETS[accentPresetIndex]
            if (darkTheme) darkSchemeFor(preset.dark, themeMode == PrefsRepository.ThemeMode.AMOLED)
            else lightSchemeFor(preset.light)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                if (themeMode == PrefsRepository.ThemeMode.AMOLED) dynamicDarkColorScheme(context).copy(
                    background = Color.Black, surface = Color.Black,
                )
                else dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        else -> {
            if (darkTheme) darkSchemeFor(OrbitIndigo80, themeMode == PrefsRepository.ThemeMode.AMOLED)
            else lightSchemeFor(OrbitAmber40)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OrbitTypography,
        content = content,
    )
}

private fun lightSchemeFor(accent: Color) = lightColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent.copy(alpha = 0.18f),
    onPrimaryContainer = accent,
    secondary = accent.copy(alpha = 0.7f),
    onSecondary = Color.White,
    background = Color(0xFFFAF9FB),
    onBackground = Color(0xFF1A1B22),
    surface = Color(0xFFFDFBFD),
    onSurface = Color(0xFF1A1B22),
    surfaceVariant = Color(0xFFE7E1EC),
    onSurfaceVariant = Color(0xFF49454F),
)

private fun darkSchemeFor(accent: Color, amoled: Boolean) = darkColorScheme(
    primary = accent,
    onPrimary = Color.Black,
    primaryContainer = accent.copy(alpha = 0.20f),
    onPrimaryContainer = accent,
    secondary = accent.copy(alpha = 0.7f),
    onSecondary = Color.Black,
    background = if (amoled) Color.Black else OrbitIndigo10,
    onBackground = if (amoled) Color(0xFFEDEDED) else OrbitIndigo90,
    surface = if (amoled) Color.Black else OrbitIndigo20,
    onSurface = if (amoled) Color(0xFFEDEDED) else OrbitIndigo90,
    surfaceVariant = OrbitIndigo40,
    onSurfaceVariant = OrbitIndigo80,
)
