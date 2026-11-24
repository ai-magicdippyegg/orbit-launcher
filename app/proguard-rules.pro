# Default ProGuard rules for Orbit Launcher.
# Most consumer-only rules are auto-applied by libraries via their own
# consumer-rules. Add project-specific rules below.

# Keep the Application class and entry points.
-keep class com.orbit.launcher.OrbitApp { *; }
-keep class com.orbit.launcher.MainActivity { *; }

# Keep model classes used by Compose / DataStore
-keep class com.orbit.launcher.data.** { *; }

# Kotlin metadata
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions
