package com.orbit.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orbit.launcher.data.AppModel
import com.orbit.launcher.data.PrefsRepository
import com.orbit.launcher.ui.theme.ACCENT_PRESETS
import com.orbit.launcher.ui.viewmodel.LauncherViewModel

/**
 * Settings screen — theme mode, accent preset, clock format, haptics,
 * swipe-down behaviour, and quick manage of favorites/hidden apps.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: LauncherViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val themeMode by vm.prefs.themeMode.collectAsState(PrefsRepository.ThemeMode.SYSTEM)
    val accentPreset by vm.prefs.accentPreset.collectAsState(-1)
    val use24Hour by vm.prefs.use24Hour.collectAsState(false)
    val haptics by vm.prefs.hapticsEnabled.collectAsState(true)
    val swipeDownAction by vm.prefs.swipeDownAction.collectAsState(PrefsRepository.SwipeDownAction.NOTIFICATIONS)
    val favorites by vm.favorites.collectAsState()
    val hiddenApps by vm.prefs.hiddenApps.collectAsState(emptySet())
    val allApps by vm.allApps.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 24.dp, end = 24.dp, top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize(),
        ) {
            item { SectionHeader("Appearance") }

            item {
                Text(
                    text = "Theme mode",
                    style = MaterialTheme.typography.titleLarge,
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    PrefsRepository.ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = { vm.setThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index, PrefsRepository.ThemeMode.entries.size,
                            ),
                        ) { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    }
                }
            }

            item {
                Text(
                    text = "Accent color",
                    style = MaterialTheme.typography.titleLarge,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ColorSwatch(
                        label = "Auto",
                        color = MaterialTheme.colorScheme.primary,
                        selected = accentPreset == -1,
                        onClick = { vm.setAccentPreset(-1) },
                    )
                    ACCENT_PRESETS.take(7).forEachIndexed { index, preset ->
                        ColorSwatch(
                            label = preset.name,
                            color = preset.light,
                            selected = accentPreset == index,
                            onClick = { vm.setAccentPreset(index) },
                        )
                    }
                }
            }

            item { SectionHeader("Behaviour") }

            item {
                SwitchRow(
                    title = "Use 24-hour clock",
                    subtitle = "Display time as 14:08 instead of 2:08 PM",
                    checked = use24Hour,
                    onCheckedChange = vm::setUse24Hour,
                )
            }

            item {
                SwitchRow(
                    title = "Haptic feedback",
                    subtitle = "Vibrate on gestures and taps",
                    checked = haptics,
                    onCheckedChange = vm::setHaptics,
                )
            }

            item {
                Column {
                    Text(
                        text = "Swipe down action",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        PrefsRepository.SwipeDownAction.entries.forEachIndexed { index, action ->
                            SegmentedButton(
                                selected = swipeDownAction == action,
                                onClick = { vm.setSwipeDownAction(action) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index, PrefsRepository.SwipeDownAction.entries.size,
                                ),
                            ) { Text(action.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        }
                    }
                }
            }

            item { SectionHeader("Favorites (${favorites.size})") }
            if (favorites.isEmpty()) {
                item {
                    Text(
                        text = "No favorites yet. Long-press apps in the drawer to add them.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(
                    items = favorites,
                    key = { it.id },
                ) { app ->
                    AppRowWithActions(
                        app = app,
                        actionLabel = "Remove",
                        onAction = { vm.toggleFavorite(app, isFavorite = false) },
                    )
                }
            }

            item { SectionHeader("Hidden apps") }
            val hiddenList = allApps.filter { it.id in hiddenApps }
            if (hiddenList.isEmpty()) {
                item {
                    Text(
                        text = "Long-press an app in the drawer to hide it.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(
                    items = hiddenList,
                    key = { it.id },
                ) { app ->
                    AppRowWithActions(
                        app = app,
                        actionLabel = "Unhide",
                        onAction = { vm.toggleHidden(app, isHidden = false) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp),
    )
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun AppRowWithActions(
    app: AppModel,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Button(onClick = onAction) { Text(actionLabel) }
    }
}

@Composable
private fun ColorSwatch(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = color, shape = CircleShape)
                .border(
                    width = if (selected) 3.dp else 0.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
