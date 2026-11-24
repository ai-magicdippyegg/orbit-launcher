package com.orbit.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orbit.launcher.data.AppModel
import com.orbit.launcher.ui.components.AppItem
import com.orbit.launcher.ui.components.ClockWidget
import com.orbit.launcher.ui.viewmodel.LauncherViewModel
import com.orbit.launcher.util.launchApp
import com.orbit.launcher.util.rememberSwipeGestureModifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The home screen. Always shows:
 *   - Date strip
 *   - Clock + battery ring
 *   - Favourites orbit (a single lazy row aligned centre)
 *
 * Gestures:
 *   - Swipe up    → open the app drawer
 *   - Swipe down  → expand the notification shade (or open search)
 *   - Swipe right → open settings
 */
@Composable
fun HomeScreen(
    vm: LauncherViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    onAppLongPress: (AppModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val favorites by vm.favorites.collectAsState()
    val use24Hour by vm.prefs.use24Hour.collectAsState()
    val swipeDownAction by vm.prefs.swipeDownAction.collectAsState()

    val gesture = rememberSwipeGestureModifier(
        onSwipeUp = onOpenDrawer,
        onSwipeDown = {
            when (swipeDownAction) {
                com.orbit.launcher.data.PrefsRepository.SwipeDownAction.NOTIFICATIONS ->
                    com.orbit.launcher.util.expandNotificationDrawer(context)
                com.orbit.launcher.data.PrefsRepository.SwipeDownAction.SEARCH -> onOpenDrawer()
            }
        },
        onSwipeRight = onOpenSettings,
    )

    Box(modifier = modifier.then(gesture).fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            DateStrip()

            ClockWidget(
                use24Hour = use24Hour,
                modifier = Modifier.widthIn(max = 320.dp),
            )

            FavoritesRow(
                favorites = favorites,
                onAppClick = { app ->
                    launchApp(context, app.packageName, app.className)
                },
                onAppLongClick = onAppLongPress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            )
        }
    }
}

@Composable
private fun DateStrip() {
    val today = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = today,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun FavoritesRow(
    favorites: List<AppModel>,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    if (favorites.isEmpty()) {
        Text(
            text = "Long-press apps in the drawer to add them here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = modifier.padding(horizontal = 40.dp),
        )
        return
    }

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
    ) {
        items(
            items = favorites,
            key = { it.id },
        ) { app ->
            AppItem(
                app = app,
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongClick(app) },
            )
        }
    }
}
