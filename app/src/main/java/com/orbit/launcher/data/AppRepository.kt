package com.orbit.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.UserHandle
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads the list of launchable apps on the device using [LauncherApps]
 * (the modern, user-aware API available since API 21).
 *
 * Icons are cached in a tiny [LruCache] keyed by package name to keep
 * scroll performance smooth on devices with hundreds of installed apps.
 */
class AppRepository(private val context: Context) {

    private val launcherApps: LauncherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    /** 96 dp cache — enough for ~24 visible icons in the drawer + favorites. */
    private val iconCache = LruCache<String, Drawable>(48)

    /** Load every launchable activity for the current user, sorted by label. */
    suspend fun loadApps(): List<AppModel> = withContext(Dispatchers.IO) {
        val activities = try {
            launcherApps.getActivityList(null, android.os.Process.myUserHandle())
        } catch (e: SecurityException) {
            emptyList()
        }

        activities
            .map { info ->
                AppModel(
                    packageName = info.applicationInfo.packageName,
                    label = info.label?.toString().orEmpty().ifBlank { info.applicationInfo.packageName },
                    className = info.componentName.className,
                    isSystem = (info.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0,
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    /** Return (and cache) the launcher icon for a given package, or null. */
    suspend fun iconFor(packageName: String, className: String?): Drawable? =
        withContext(Dispatchers.IO) {
            val key = "$packageName/$className"
            iconCache.get(key) ?: run {
                val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                    .setClassName(packageName, className ?: "")
                val resolveInfo = context.packageManager
                    .queryIntentActivities(intent, PackageManager.MATCH_ALL)
                    .firstOrNull { it.activityInfo.packageName == packageName }
                val drawable = resolveInfo?.loadIcon(context.packageManager)
                if (drawable != null) iconCache.put(key, drawable)
                drawable
            }
        }

    /** Look up a fresh [Drawable] via LauncherApps (no caching). */
    fun iconForSync(packageName: String): Drawable? = try {
        launcherApps.getActivityList(packageName, android.os.Process.myUserHandle())
            .firstOrNull()
            ?.getIcon(context.resources.displayMetrics.densityDpi)
    } catch (e: Exception) { null }
}
