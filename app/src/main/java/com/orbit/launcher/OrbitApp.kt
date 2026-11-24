package com.orbit.launcher

import android.app.Application

/**
 * Orbit launcher Application entry point.
 *
 * Kept intentionally tiny — Compose handles most global state via the
 * activity-scoped [androidx.lifecycle.ViewModel] graph. Any future DI
 * container or global singletons would live here.
 */
class OrbitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Future: timber / strict mode init / DI container.
    }
}
