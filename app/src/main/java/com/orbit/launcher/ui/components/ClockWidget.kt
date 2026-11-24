package com.orbit.launcher.ui.components

import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Home-screen clock widget: large thin numerals at the centre with a
 * subtle accent-coloured ring showing battery percentage around the
 * outside. Updates once a minute (or when the minute changes).
 */
@Composable
fun ClockWidget(
    use24Hour: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    var batteryPct by remember { mutableIntStateOf(0) }

    // Tick every 5s — cheap, and snaps to the right minute boundary quickly.
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            batteryPct = readBatteryPct(context)
            delay(5_000)
        }
    }

    val accent = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier
            .padding(24.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        // Battery ring.
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 6.dp.toPx()
            val inset = stroke
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            drawArc(
                color = accent.copy(alpha = 0.18f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * batteryPct / 100f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }

        // Time numerals.
        val timeFormat = if (use24Hour) "HH:mm" else "h:mm"
        val timeStr = SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date(now))
        Text(
            text = timeStr,
            color = onSurface,
            fontSize = 64.sp,
            fontWeight = FontWeight.Thin,
        )
    }
}

/**
 * Reads the current battery percentage via the sticky [BatteryManager] intent.
 */
private fun readBatteryPct(context: android.content.Context): Int {
    val filter = android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
    val battery = context.registerReceiver(null, filter) ?: return 0
    val level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    return if (level >= 0 && scale > 0) (level * 100 / scale) else 0
}
