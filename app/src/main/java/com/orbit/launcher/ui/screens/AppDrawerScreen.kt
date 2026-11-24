package com.orbit.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orbit.launcher.data.AppModel
import com.orbit.launcher.ui.components.AppItem
import com.orbit.launcher.ui.components.SearchBar
import com.orbit.launcher.ui.viewmodel.LauncherViewModel
import com.orbit.launcher.util.launchApp

/**
 * Bottom-sheet-style app drawer. Sits over a translucent surface, with
 * the search pill at the top and a responsive grid of apps below.
 */
@Composable
fun AppDrawerScreen(
    vm: LauncherViewModel,
    onAppLongPress: (AppModel) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val apps by vm.visibleApps.collectAsState()
    val query by vm.query.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    com.orbit.launcher.util.rememberSwipeGestureModifier(
                        onSwipeDown = onDismiss,
                    )
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = vm::setQuery,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (apps.isEmpty() && query.isNotBlank()) {
                    Text(
                        text = "No apps match \"$query\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 120.dp),
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 88.dp),
                        contentPadding = PaddingValues(
                            start = 20.dp, end = 20.dp, top = 90.dp, bottom = 32.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            items = apps,
                            key = { it.id },
                        ) { app ->
                            AppItem(
                                app = app,
                                onClick = {
                                    launchApp(context, app.packageName, app.className)
                                },
                                onLongClick = { onAppLongPress(app) },
                            )
                        }
                    }
                }
            }
        }
    }
}
