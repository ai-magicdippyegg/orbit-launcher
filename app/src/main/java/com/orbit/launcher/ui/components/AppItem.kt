package com.orbit.launcher.ui.components

import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.orbit.launcher.data.AppModel

/**
 * A single app icon + label. The icon is loaded lazily from the system
 * [LauncherApps] and rendered to an [ImageBitmap]; the label is clamped to
 * a single line for visual consistency in grids.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(
    app: AppModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    labelMaxLines: Int = 1,
) {
    val context = LocalContext.current
    var iconDrawable: Drawable? by remember(app.id) { mutableStateOf(null) }

    // Lazy-load the icon. We do this synchronously inside Compose because
    // LauncherApps already caches bitmaps internally.
    if (iconDrawable == null) {
        iconDrawable = remember(app.id) { loadIcon(context, app) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = LocalContentColor.current),
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            iconDrawable?.let { drawable ->
                val bitmap = remember(drawable) {
                    drawable.toBitmap(96, 96).asImageBitmap()
                }
                Image(
                    bitmap = bitmap,
                    contentDescription = app.label,
                    modifier = Modifier.size(48.dp),
                )
            }
        }

        Text(
            text = app.label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            maxLines = labelMaxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

private fun loadIcon(context: android.content.Context, app: AppModel): Drawable? = try {
    val launcherApps = context.getSystemService(android.content.Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val activities = launcherApps.getActivityList(app.packageName, android.os.Process.myUserHandle())
    activities.firstOrNull {
        app.className == null || it.componentName.className == app.className
    }?.getIcon(context.resources.displayMetrics.densityDpi)
} catch (e: Exception) { null }
