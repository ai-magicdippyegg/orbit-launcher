package com.orbit.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "orbit_prefs")

/**
 * Persisted user settings. Backed by Jetpack DataStore so writes happen
 * off the main thread and values flow into Compose reactively.
 */
class PrefsRepository(private val context: Context) {

    enum class ThemeMode { SYSTEM, LIGHT, DARK, AMOLED }

    enum class SwipeDownAction { NOTIFICATIONS, SEARCH }

    object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_PRESET = intPreferencesKey("accent_preset") // -1 = auto
        val USE_24_HOUR = booleanPreferencesKey("use_24_hour")
        val HAPTICS = booleanPreferencesKey("haptics")
        val SWIPE_DOWN_ACTION = stringPreferencesKey("swipe_down_action")
        val FAVORITES = stringSetPreferencesKey("favorites") // ids: "pkg/cls"
        val HIDDEN_APPS = stringSetPreferencesKey("hidden_apps")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map {
        runCatching { ThemeMode.valueOf(it[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name) }
            .getOrDefault(ThemeMode.SYSTEM)
    }

    val accentPreset: Flow<Int> = context.dataStore.data.map { it[Keys.ACCENT_PRESET] ?: -1 }

    val use24Hour: Flow<Boolean> = context.dataStore.data.map { it[Keys.USE_24_HOUR] ?: false }

    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.HAPTICS] ?: true }

    val swipeDownAction: Flow<SwipeDownAction> = context.dataStore.data.map {
        runCatching { SwipeDownAction.valueOf(it[Keys.SWIPE_DOWN_ACTION] ?: SwipeDownAction.NOTIFICATIONS.name) }
            .getOrDefault(SwipeDownAction.NOTIFICATIONS)
    }

    val favorites: Flow<Set<String>> = context.dataStore.data.map { it[Keys.FAVORITES] ?: emptySet() }

    val hiddenApps: Flow<Set<String>> = context.dataStore.data.map { it[Keys.HIDDEN_APPS] ?: emptySet() }

    suspend fun setThemeMode(mode: ThemeMode) = context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    suspend fun setAccentPreset(index: Int) = context.dataStore.edit { it[Keys.ACCENT_PRESET] = index }
    suspend fun setUse24Hour(value: Boolean) = context.dataStore.edit { it[Keys.USE_24_HOUR] = value }
    suspend fun setHaptics(value: Boolean) = context.dataStore.edit { it[Keys.HAPTICS] = value }
    suspend fun setSwipeDownAction(action: SwipeDownAction) =
        context.dataStore.edit { it[Keys.SWIPE_DOWN_ACTION] = action.name }

    suspend fun toggleFavorite(appId: String, isFavorite: Boolean) = context.dataStore.edit {
        val current = it[Keys.FAVORITES] ?: emptySet()
        it[Keys.FAVORITES] = if (isFavorite) current + appId else current - appId
    }

    suspend fun toggleHidden(appId: String, isHidden: Boolean) = context.dataStore.edit {
        val current = it[Keys.HIDDEN_APPS] ?: emptySet()
        it[Keys.HIDDEN_APPS] = if (isHidden) current + appId else current - appId
    }
}
