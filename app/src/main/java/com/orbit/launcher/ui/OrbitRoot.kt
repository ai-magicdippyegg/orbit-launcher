package com.orbit.launcher.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.launcher.data.AppModel
import com.orbit.launcher.data.PrefsRepository
import com.orbit.launcher.ui.screens.AppDrawerScreen
import com.orbit.launcher.ui.screens.HomeScreen
import com.orbit.launcher.ui.screens.SettingsScreen
import com.orbit.launcher.ui.theme.OrbitLauncherTheme
import com.orbit.launcher.ui.viewmodel.LauncherViewModel

/**
 * Top-level navigation state for Orbit. The launcher is intentionally
 * single-screen-first: we use Compose state (not a nav graph) for the
 * drawer/settings transitions because they're quick overlays.
 *
 * Layers, back to front:
 *   1. Home screen (always visible behind overlays)
 *   2. App drawer (translucent sheet, animated in/out)
 *   3. Settings (full-screen surface, replaces everything)
 *   4. Long-press action dialog (modal)
 */
@Composable
fun OrbitRoot() {
    val vm: LauncherViewModel = viewModel()
    val themeMode by vm.prefs.themeMode.collectAsState(PrefsRepository.ThemeMode.SYSTEM)
    val accentPreset by vm.prefs.accentPreset.collectAsState(-1)

    var showDrawer by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var longPressTarget by remember { mutableStateOf<AppModel?>(null) }
    val favorites by vm.favorites.collectAsState()

    OrbitLauncherTheme(
        themeMode = themeMode,
        accentPresetIndex = accentPreset,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            HomeScreen(
                vm = vm,
                onOpenDrawer = { showDrawer = true },
                onOpenSettings = { showSettings = true },
                onAppLongPress = { longPressTarget = it },
            )

            AnimatedVisibility(
                visible = showDrawer,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                AppDrawerScreen(
                    vm = vm,
                    onAppLongPress = { longPressTarget = it },
                    onDismiss = { showDrawer = false },
                )
            }

            AnimatedVisibility(
                visible = showSettings,
                enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 4 }) + fadeOut(),
            ) {
                SettingsScreen(
                    vm = vm,
                    onBack = { showSettings = false },
                )
            }

            longPressTarget?.let { app ->
                LongPressDialog(
                    app = app,
                    isFavorite = favorites.any { it.id == app.id },
                    onAddFavorite = { vm.toggleFavorite(app, isFavorite = true) },
                    onRemoveFavorite = { vm.toggleFavorite(app, isFavorite = false) },
                    onHide = { vm.toggleHidden(app, isHidden = true) },
                    onAppInfo = { /* TODO: open system app info */ },
                    onDismiss = { longPressTarget = null },
                )
            }
        }
    }
}

@Composable
private fun LongPressDialog(
    app: AppModel,
    isFavorite: Boolean,
    onAddFavorite: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onHide: () -> Unit,
    onAppInfo: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.label) },
        text = {
            Column {
                if (isFavorite) {
                    DialogAction("Remove from favorites", onRemoveFavorite)
                    DialogAction("Hide app", onHide)
                } else {
                    DialogAction("Add to favorites", onAddFavorite)
                    DialogAction("Hide app", onHide)
                }
                DialogAction("App info", onAppInfo)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Composable
private fun DialogAction(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
