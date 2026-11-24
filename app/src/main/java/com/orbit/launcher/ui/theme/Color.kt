package com.orbit.launcher.ui.theme

import androidx.compose.ui.graphics.Color

// Fallback palette for devices below Android 12 (no Material You).
// A deep-space indigo with a warm amber accent — feels like night sky + orbiting sun.

val OrbitIndigo10 = Color(0xFF0B0F1F)
val OrbitIndigo20 = Color(0xFF141A33)
val OrbitIndigo40 = Color(0xFF2A3258)
val OrbitIndigo80 = Color(0xFFB4BCFF)
val OrbitIndigo90 = Color(0xFFDDE0FF)

val OrbitAmber40 = Color(0xFFFFB74D)
val OrbitAmber80 = Color(0xFFFFD9A0)

// Preset accent swatches exposed in Settings.
data class AccentPreset(val name: String, val light: Color, val dark: Color)

val ACCENT_PRESETS: List<AccentPreset> = listOf(
    AccentPreset("Aurora",   Color(0xFF6750A4), Color(0xFFD0BCFF)), // default purple
    AccentPreset("Sunset",   Color(0xFFC00033), Color(0xFFFFB4C2)),
    AccentPreset("Tangerine",Color(0xFFB85B00), Color(0xFFFFB870)),
    AccentPreset("Mint",     Color(0xFF006B5A), Color(0xFF7BE0C0)),
    AccentPreset("Sky",      Color(0xFF0061A4), Color(0xFF9ECBFF)),
    AccentPreset("Indigo",   Color(0xFF293F9E), Color(0xFFB4BCFF)),
    AccentPreset("Magenta", Color(0xFF9B0064), Color(0xFFFFD0E5)),
    AccentPreset("Graphite", Color(0xFF333333), Color(0xFFBDBDBD)),
)
