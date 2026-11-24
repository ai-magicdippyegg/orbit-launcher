package com.orbit.launcher

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.orbit.launcher.ui.OrbitRoot
import com.orbit.launcher.ui.theme.OrbitLauncherTheme

/**
 * Single-activity launcher entry point.
 *
 * Edge-to-edge by default; the system bars are drawn behind the home screen
 * so the wallpaper shows through cleanly. We hide the nav bar in immersive
 * mode but keep the status bar visible (and swipeable) on the home screen.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            OrbitLauncherTheme {
                OrbitRoot()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Light immersive mode: keep status bar visible but hide nav bar.
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

/** Tag used by anything that needs a [Activity] reference from Compose. */
const val ORBIT_ACTIVITY_TAG = "orbit-launcher"
