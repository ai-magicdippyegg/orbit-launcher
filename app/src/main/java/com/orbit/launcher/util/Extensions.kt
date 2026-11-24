package com.orbit.launcher.util

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.UserHandle
import android.provider.Settings
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Launch a launcher activity by package + class. Uses [LauncherApps]
 * because it is user-aware and the canonical way to start an app from
 * a third-party launcher on modern Android.
 */
fun launchApp(context: Context, packageName: String, className: String?) {
    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val userHandle: UserHandle = android.os.Process.myUserHandle()
    val components = launcherApps.getActivityList(packageName, userHandle)
    val info = components.firstOrNull {
        className == null || it.componentName.className == className
    } ?: components.firstOrNull() ?: return
    try {
        launcherApps.startMainActivity(info.componentName, userHandle, null, null)
    } catch (_: SecurityException) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        context.startActivity(intent)
    }
}

/**
 * Open the system notification shade. On modern Android this requires the
 * `EXPAND_STATUS_BAR` permission (declared in our manifest) and the system
 * broadcasts the `EXPAND_STATUS_BAR` intent which we send here.
 */
fun expandNotificationDrawer(context: Context) {
    @Suppress("DEPRECATION")
    val intent = Intent("android.intent.action.EXPAND_STATUS_BAR").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.sendBroadcast(intent) }
}

fun openSystemSettings(context: Context) {
    val intent = Intent(Settings.ACTION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

/**
 * Detect a swipe gesture with a minimum threshold and cardinal direction.
 *
 * Calls [onSwipeUp] / [onSwipeDown] / [onSwipeLeft] / [onSwipeRight] when
 * the drag exceeded [thresholdDp] in that direction. Internally uses
 * [detectDragGestures] and inspects the accumulated delta on drag end.
 */
@Composable
fun rememberSwipeGestureModifier(
    thresholdDp: Float = 60f,
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
): Modifier {
    val density = LocalDensity.current
    val thresholdPx = with(density) { thresholdDp.dp.toPx() }
    return remember(thresholdPx, onSwipeUp, onSwipeDown, onSwipeLeft, onSwipeRight) {
        Modifier.pointerInput(thresholdPx) {
            var total: Offset = Offset.Zero
            detectDragGestures(
                onDragStart = { total = Offset.Zero },
                onDragEnd = {
                    val dx = total.x
                    val dy = total.y
                    if (abs(dx) > thresholdPx || abs(dy) > thresholdPx) {
                        when {
                            abs(dx) > abs(dy) ->
                                if (dx > 0) onSwipeRight() else onSwipeLeft()
                            dy > 0 -> onSwipeDown()
                            else -> onSwipeUp()
                        }
                    }
                    total = Offset.Zero
                },
                onDragCancel = { total = Offset.Zero },
                onDrag = { change, dragAmount ->
                    total += dragAmount
                    change.consume()
                },
            )
        }
    }
}
