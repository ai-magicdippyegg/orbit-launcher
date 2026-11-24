package com.orbit.launcher.data

import android.graphics.drawable.Drawable

/**
 * A user-facing representation of an installed app on the device.
 *
 * @property packageName Android package name, used as a stable id.
 * @property label       Display label shown to the user.
 * @property className   The launchable component class, or null if none.
 * @property isSystem    True if the app is part of the system image.
 */
data class AppModel(
    val packageName: String,
    val label: String,
    val className: String?,
    val isSystem: Boolean,
) {
    val id: String get() = "$packageName/$className"

    companion object
}
