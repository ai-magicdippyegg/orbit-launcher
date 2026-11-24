package com.orbit.launcher.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.launcher.data.AppModel
import com.orbit.launcher.data.AppRepository
import com.orbit.launcher.data.PrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Central UI-state holder for Orbit. Streams both the installed-app list
 * and the persisted prefs into Compose-observable flows.
 *
 * Designed as a single source of truth so that the home screen, app drawer,
 * and settings screen all see the same state.
 */
class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)
    val prefs = PrefsRepository(app)

    // ---- Apps -----------------------------------------------------------

    private val _allApps = MutableStateFlow<List<AppModel>>(emptyList())
    val allApps: StateFlow<List<AppModel>> = _allApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun setQuery(value: String) { _query.value = value }

    /**
     * Visible apps: not hidden, and matching the current search query (if any).
     */
    val visibleApps: StateFlow<List<AppModel>> =
        combine(_allApps, prefs.hiddenApps, _query) { apps, hidden, q ->
            val filtered = apps.filter { it.id !in hidden }
            val needle = q.trim()
            if (needle.isEmpty()) filtered
            else filtered.filter {
                it.label.contains(needle, ignoreCase = true) ||
                    it.packageName.contains(needle, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Favorite apps in the order the user added them (we keep insertion order
     * by sorting by their stable ids set).
     */
    val favorites: StateFlow<List<AppModel>> =
        combine(_allApps, prefs.favorites) { apps, favIds ->
            apps.filter { it.id in favIds }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        refresh()
    }

    /** Re-query the launcher activities. */
    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _allApps.value = repo.loadApps()
            _isLoading.value = false
        }
    }

    // ---- Toggles -------------------------------------------------------

    fun toggleFavorite(app: AppModel, isFavorite: Boolean) {
        viewModelScope.launch { prefs.toggleFavorite(app.id, isFavorite) }
    }

    fun toggleHidden(app: AppModel, isHidden: Boolean) {
        viewModelScope.launch { prefs.toggleHidden(app.id, isHidden) }
    }

    fun setThemeMode(mode: PrefsRepository.ThemeMode) =
        viewModelScope.launch { prefs.setThemeMode(mode) }

    fun setAccentPreset(index: Int) =
        viewModelScope.launch { prefs.setAccentPreset(index) }

    fun setUse24Hour(value: Boolean) =
        viewModelScope.launch { prefs.setUse24Hour(value) }

    fun setHaptics(value: Boolean) =
        viewModelScope.launch { prefs.setHaptics(value) }

    fun setSwipeDownAction(action: PrefsRepository.SwipeDownAction) =
        viewModelScope.launch { prefs.setSwipeDownAction(action) }
}
