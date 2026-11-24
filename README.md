<div align="center">

# 🪐 Orbit Launcher

**A minimal, gesture-driven Android home screen built with Jetpack Compose.**

Your apps in orbit. Nothing else.

</div>

---

Orbit is a lightweight, distraction-free launcher that puts your most-used apps a tap away and gets out of your way the rest of the time. Built from scratch with modern Android tools — **Kotlin, Jetpack Compose, Material 3, and Material You dynamic color**.

## ✨ Features

- **Radial favorites** — Your most-used apps arranged in a clean orbit around the clock.
- **Swipe-up app drawer** — All apps a flick away, with fuzzy search.
- **Material You** — Dynamic wallpaper-based theming on Android 12+, with a tasteful fallback palette on older devices.
- **Clock widget** — Always-on home-screen clock with date, day, and a subtle battery ring.
- **Hide apps** — Hide pre-installed bloatware you can't uninstall.
- ** gestures** — Swipe up for the drawer, swipe down for the notification shade, pinch for settings.
- **Open source** — Apache 2.0, no telemetry, no ads, no network calls. Ever.

## 📸 Design language

Orbit leans on **soft, deep-space neutrals** and a single accent pulled from your wallpaper. The home screen is intentionally sparse:

```
        ┌──── date ────┐
        │   Thursday    │
        │  Nov 24 2026  │
        └───────────────┘
              🕑
        ┌── clock + battery ring ──┐
        │       1 4 : 0 8           │
        └───────────────────────────┘

        🅰️      🅱️      🅲️      🅳️
        ── partial orbit of favorite apps ──
```

The drawer slides in from the bottom as a translucent sheet; the search bar lives at the top and filters instantly.

## 🛠️ Tech stack

| Layer            | Choice                                                |
|------------------|------------------------------------------------------|
| UI               | Jetpack Compose + Material 3                         |
| Language         | Kotlin 2.0                                          |
| State            | `ViewModel` + `StateFlow`                            |
| Persistence      | `DataStore` preferences                              |
| Theming          | `dynamicColorScheme` (Android 12+) + fallback palette |
| Build            | Gradle Kotlin DSL, Version Catalog (`libs.versions.toml`) |
| CI               | GitHub Actions — builds a debug APK on every push    |

## 🚀 Build it

Requirements: **Android Studio Ladybug+**, JDK 17, Android SDK 34.

```bash
# clone
git clone https://github.com/ai-magicdippyegg/orbit-launcher.git
cd orbit-launcher

# build a debug APK
./gradlew assembleDebug
# output: app/build/outputs/apk/debug/app-debug.apk
```

Or just open the project in Android Studio and hit ▶️.

## 📦 Install

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
# Then: Settings → Apps → Default apps → Home app → Orbit
```

## 🧭 Project layout

```
app/src/main/java/com/orbit/launcher/
├── MainActivity.kt              # single activity, hosts Compose root
├── OrbitApp.kt                  # Application class
├── data/
│   ├── AppModel.kt              # installed-app model
│   ├── AppRepository.kt         # PM-backed app list source
│   └── PrefsRepository.kt       # DataStore settings
├── ui/
│   ├── theme/                   # Color, Theme, Type
│   ├── components/              # ClockWidget, AppItem, SearchBar
│   ├── screens/                 # HomeScreen, AppDrawerScreen, SettingsScreen
│   └── viewmodel/
│       └── LauncherViewModel.kt # central state holder
└── util/Extensions.kt
```

## ⚙️ Settings

- Theme: System / Light / Dark / AMOLED
- Accent: Auto (wallpaper) or pick from 8 presets
- Clock format: 12h / 24h
- Favorites: pick up to 6
- Hidden apps
- Haptics on/off
- Swipe-down action: notifications / search

## 🤝 Contributing

PRs welcome. Please run `./gradlew spotlessCheck` (if you add Spotless) before submitting. Keep PRs focused.

## 📝 License

Apache 2.0 — see [LICENSE](LICENSE). Orbit Launcher is not affiliated with Google, Android, or Jetpack. It's just a clean launcher that respects you.

---

<div align="center">

**Your apps. In orbit. Nothing else.**

</div>
