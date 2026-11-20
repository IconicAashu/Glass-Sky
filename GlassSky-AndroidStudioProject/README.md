# Glass Sky — Premium Native Android Weather Application

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09.00-4285F4.svg?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Enabled-795548.svg)](https://m3.material.io/)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-3DDC84.svg?logo=android&logoColor=white)](https://android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-green.svg)](https://developer.android.com)

**Glass Sky** is an exceptionally crafted, production-ready native Android weather application inspired by modern glassmorphism, 3D volumetric weather illustrations, and minimalist typography.

---

## ✨ Features

- **3D Volumetric Weather Illustrations**: Custom Compose Canvas 3D rendering engine for the sun, clouds, glossy raindrop capsules, crescent moon, and golden lightning.
- **Atmospheric Backgrounds & Radial Glow**: Dynamic ambient canvas adapting to real-time weather conditions and day/night cycles.
- **100% Real Weather Data**: Powered by the **Open-Meteo REST API** (`https://api.open-meteo.com/v1/forecast`) — zero mock data.
- **Real-Time Air Quality Index (AQI)**: Integrated with the Open-Meteo Air Quality API to deliver live US AQI, PM2.5, PM10, O₃, NO₂, and health advisory guidance.
- **Live Weather Radar Visualizer**: Interactive Doppler loop simulation with switchable layers (*Rain Radar*, *Wind Stream*, *Thermal Heat*) and scrubbable timeline.
- **Hourly & 7-Day Extended Forecasts**: Squircle frosted forecast cards with miniature 3D illustrations, precipitation probabilities, and gradient temperature range bars.
- **City Search with Autocomplete & Recent Searches**: Instant debounced search via Open-Meteo Geocoding API with saved local search history and popular global cities.
- **Floating Glass Bottom Navigation Bar**: Floating dark glass pill dock (*Home*, *Search*, *Notifications*, *Weather Map*).
- **Comprehensive Settings**: Light, Dark, and System theme switching, metric/imperial unit conversions, animation intensity, notification preferences, and privacy controls.
- **Offline First & Caching**: Multi-tiered cache with 15-minute validity and offline fallback.

---

## 🏗️ Architecture & Technology Stack

- **Language**: Kotlin 2.0.20
- **UI Framework**: Jetpack Compose with Material 3
- **Design Paradigm**: MVVM (Model-View-ViewModel) + Repository Pattern + Clean Architecture
- **State Management**: Reactive `StateFlow` and Compose `collectAsState()`
- **Networking**: Ktor 2.3.12 HTTP Client with OkHttp engine and Kotlinx Serialization
- **Location Telemetry**: Google Play Services Location (`FusedLocationProviderClient`)
- **Permissions**: Accompanist Permissions 0.36.0
- **Asynchronous**: Kotlin Coroutines (structured concurrency with `Dispatchers.IO` and `viewModelScope`)
- **Build System**: Gradle 8.7 with Version Catalog (`libs.versions.toml`)

---

## 🚀 How to Open and Build in Android Studio

### Prerequisites
1. **Android Studio** (Koala, Ladybug, or newer).
2. **Java Development Kit (JDK)**: JDK 17.
3. **Android SDK**: Android 14 (API 34) platform and Build-Tools 34.0.0.

### Opening the Project
1. Open Android Studio.
2. Select **File > Open...**
3. Choose this `android/` directory.
4. Android Studio will automatically recognize the project and run Gradle Sync.

### Running the App
- Select your target device or emulator (API 26 or higher).
- Click **Run ▶** (`Shift + F10`).

### Building from Command Line
```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test
```

---

## 📦 Project Structure

```
android/
├── app/
│   ├── build.gradle.kts           # App-level build configuration & dependencies
│   ├── proguard-rules.pro         # Proguard/R8 optimization rules
│   └── src/main/
│       ├── AndroidManifest.xml    # Permissions & application manifest
│       ├── java/com/glasssky/weather/
│       │   ├── GlassSkyApplication.kt
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── location/LocationTracker.kt
│       │   │   ├── model/WeatherModels.kt
│       │   │   ├── model/WeatherCondition.kt
│       │   │   ├── network/OpenMeteoService.kt
│       │   │   └── repository/WeatherRepository.kt
│       │   └── ui/
│       │       ├── components/    # 3D illustration engine, cards, bottom nav, AQI
│       │       ├── screens/       # Home, Search, Map, Notifications, Forecast, Settings
│       │       └── theme/         # Color palettes, typography, theme provider
│       └── res/                   # Drawables, mipmaps, strings, XML resources
├── gradle/
│   ├── libs.versions.toml         # Gradle Version Catalog
│   └── wrapper/                   # Gradle wrapper binaries & properties
├── build.gradle.kts               # Top-level build file
├── gradle.properties              # JVM arguments & AndroidX flags
├── gradlew / gradlew.bat          # Gradle execution wrappers
├── settings.gradle.kts            # Plugin management & project settings
└── .gitignore                     # Clean Git exclusion rules
```

---

## 🏪 Publishing to Orion Store / Google Play

1. Generate your production release keystore:
   ```bash
   keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release
   ```
2. Configure your signing credentials via environment variables or Gradle properties.
3. Generate the signed release package:
   ```bash
   ./gradlew bundleRelease
   ```
4. Upload the generated `.aab` file from `app/build/outputs/bundle/release/` to the Orion Store / Google Play Developer Console.

---

## 👨‍💻 Developer Credit

**Designed & Developed with ❤️ by Aashu**  
**Glass Sky**

---

## 📜 Attribution & License

- Meteorological and air quality telemetry provided by [Open-Meteo](https://open-meteo.com/) under ODbL 1.0.
- Source code released under the MIT License.
