# Prayer Times (Azan) Feature - Implementation Guide

## Overview

This document describes the implementation of the 5 daily prayer times (Azan) notification system
integrated into the AyyBay MVI App.

## Features Implemented

### 1. Prayer Time Calculation

- **Astronomical calculation engine** using solar declination and equation of time
- **Multiple calculation methods** supported:
    - Muslim World League (MWL)
    - Islamic Society of North America (ISNA)
    - Egyptian General Authority
    - Umm al-Qura University, Makkah
    - University of Islamic Sciences, Karachi
    - Institute of Geophysics, University of Tehran
- **Adjustable Asr time**: Shafi/Hanafi schools of thought

### 2. Five Daily Prayers

1. **Fajr** (Dawn prayer)
2. **Dhuhr** (Noon prayer)
3. **Asr** (Afternoon prayer)
4. **Maghrib** (Sunset prayer)
5. **Isha** (Night prayer)

### 3. Notification System

- **Exact alarm scheduling** for precise notification timing
- **High-priority notifications** with sound and vibration
- **Per-prayer notification toggle** (enable/disable individually)
- **Boot receiver** for rescheduling after device restart
- **Notification channel** for Android 8.0+ compatibility

### 4. User Interface

- **Prayer Times Card** displaying all 5 prayers with times
- **Real-time clock** showing current time
- **Notification toggles** for each prayer
- **Modern Material Design 3** styling
- **Integrated with HomeScreen** for seamless user experience

## Architecture

### Domain Layer

```
com.ayybay.app.domain.model/
├── PrayerTime.kt              # Prayer time data model
├── PrayerSettings.kt         # User settings preferences
└── enums:
    ├── PrayerName              # Fajr, Dhuhr, Asr, Maghrib, Isha
    ├── CalculationMethod      # Different calculation methods
    └── Madhab                 # Shafi, Hanafi

com.ayybay.app.domain.repository/
└── PrayerTimeRepository       # Interface for prayer time operations

com.ayybay.app.domain.usecase/
├── GetPrayerTimesUseCase         # Get prayer times for a date
├── GetPrayerSettingsUseCase      # Get user prayer settings
├── UpdatePrayerSettingsUseCase   # Update prayer settings
├── SchedulePrayerNotificationsUseCase # Schedule notifications
└── TogglePrayerNotificationUseCase    # Toggle individual notifications
```

### Data Layer

```
com.ayybay.app.data/
├── PrayerTimeCalculator.kt       # Astronomical calculations
├── local/
│   ├── AppDatabase.kt            # Database with prayer tables
│   ├── PrayerTimeDao.kt           # Database operations
│   └── entity/
│       ├── PrayerTimeEntity.kt    # Prayer time DB model
│       └── PrayerSettingsEntity.kt # Settings DB model
├── mapper/
│   └── PrayerTimeMapper.kt       # Entity/Domain mapping
└── repository/
    └── PrayerTimeRepositoryImpl.kt # Repository implementation
```

### Presentation Layer

```
com.ayybay.app.presentation/
├── component/
│   └── PrayerTimesCard.kt         # Prayer times UI component
├── screen/
│   └── HomeScreen.kt              # Updated to show prayer times
├── viewmodel/
│   └── PrayerViewModel.kt         # Prayer times management
└── receiver/
    ├── AzanNotificationReceiver.kt  # Broadcast receiver for notifications
    └��─ AzanBootReceiver.kt           # Boot receiver for restart
```

## Database Schema

### Prayer Times Table

```sql
CREATE TABLE prayer_times (
    id INTEGER PRIMARY KEY,
    prayer_name TEXT NOT NULL,
    time INTEGER NOT NULL,
    date INTEGER NOT NULL,
    is_enabled INTEGER NOT NULL DEFAULT 1
)
```

### Prayer Settings Table

```sql
CREATE TABLE prayer_settings (
    id INTEGER PRIMARY KEY,
    location_latitude REAL,
    location_longitude REAL,
    calculation_method TEXT,
    madhab TEXT,
    notifications_enabled INTEGER NOT NULL DEFAULT 1
)
```

## Configuration

### AndroidManifest.xml

Added permissions:

- `POST_NOTIFICATIONS` - Android 13+ notification permission
- `SCHEDULE_EXACT_ALARM` - Precise alarm scheduling
- `RECEIVE_BOOT_COMPLETED` - Boot receiver
- `ACCESS_FINE_LOCATION` - Location-based prayer times
- `ACCESS_COARSE_LOCATION` - Approximate location

Added receivers:

- `AzanBootReceiver` - Reschedules notifications on device restart
- `AzanNotificationReceiver` - Displays prayer time notifications

### Dependency Injection (Koin)

```kotlin
// Prayer Calculator
single { PrayerTimeCalculator() }

// Repository
single { 
    PrayerTimeRepositoryImpl(
        prayerTimeDao = get(),
        context = androidContext()
    )
}

// Use Cases
factory { GetPrayerTimesUseCase(get()) }
factory { GetPrayerSettingsUseCase(get()) }
factory { UpdatePrayerSettingsUseCase(get()) }
factory { SchedulePrayerNotificationsUseCase(get(), get(), get()) }
factory { TogglePrayerNotificationUseCase(get()) }

// ViewModel
viewModel { PrayerViewModel(...) }
```

## Usage

### Setting Up Location (Optional)

The app uses Makkah (21.4225°N, 39.8262°E) as the default location. Users can set their location for
accurate prayer times:

```kotlin
val settings = PrayerSettings(
    locationLatitude = 23.7104,  // Example: Dhaka
    locationLongitude = 90.4074,
    calculationMethod = CalculationMethod.MWL,
    madhab = Madhab.SHAFI,
    notificationsEnabled = true
)

prayerViewModel.updatePrayerSettings(settings)
```

### Scheduling Notifications

Notifications are automatically scheduled when:

- App is first opened
- Settings are updated
- Device is rebooted

Notifications are scheduled for the current day and the next 6 days.

### Toggling Individual Prayers

Users can enable/disable notifications for specific prayers:

```kotlin
prayerViewModel.togglePrayerNotification(
    prayerName = PrayerName.FAJR,
    enabled = false  // Disable Fajr notification
)
```

## Notification Appearance

When a prayer time arrives, users receive:

- **Title**: "🕌 [Prayer Name] - Time for Prayer"
- **Content**: "It's time for [Prayer Name] prayer at [Time]"
- **Sound**: Default notification sound with vibration
- **Priority**: High importance
- **Icon**: Mosque icon

## Calculation Details

### Prayer Time Method

The app uses astronomical calculations based on:

1. **Solar Declination**: Sun's position relative to celestial equator
2. **Equation of Time**: Difference between apparent and mean solar time
3. **Hour Angle**: Angular distance between observer's meridian and sun's position

### Twilight Angles

Different calculation methods use different twilight angles:

- Fajr: Sun's depression below horizon (15-19.5°)
- Isha: Sun's depression below horizon (15-19.5°)

### Asr Calculation

Two methods available:

- **Shafi**: Shadow length = 1 + object length
- **Hanafi**: Shadow length = 2 x object length

## Testing

### Manual Testing

1. Change device time to test prayer notifications
2. Toggle individual prayer notifications
3. Set custom location coordinates
4. Test behavior after device reboot
5. Verify notification channel appears in system settings

### Notification Testing

```bash
# Trigger test notification
adb shell am broadcast -a "com.ayybay.app.AZAN_NOTIFICATION" \
  --es prayer_name "Fajr" \
  --el prayer_time <timestamp>
```

## Permissions Flow

1. **App Launch** → Request POST_NOTIFICATIONS permission (Android 13+)
2. **Permission Granted** → Schedule prayer notifications for next 7 days
3. **Device Reboot** → AzanBootReceiver reschedules notifications
4. **Prayer Arrives** → AzanNotificationReceiver shows notification

## Error Handling

- **Location not set**: Uses Makkah coordinates
- **Notifications disabled**: Still calculates prayer times
- **Boot receiver failure**: Notifications scheduled when app opens
- **Database errors**: Silent fallback to calculator-only mode

## Future Enhancements

1. **Integration with external API**: Aladhan API for verified prayer times
2. **Qibla direction**: Compass integration for prayer direction
3. **Audio Adhan**: Play actual Azan audio
4. **Prayer reminders**: Second notification 10 minutes before
5. **Prayer tracking**: Mark prayers as completed
6. **Widget support**: Home screen widget for prayer times
7. **Calendar sync**: Export prayer times to device calendar

## Dependencies Used

- **Android Jetpack**: WorkManager, AlarmManager
- **Koin**: Dependency injection
- **Room**: Local database
- **Material 3**: UI components
- **Kotlin Coroutines**: Asynchronous operations

## Notes

### Database Version

Database version updated from 1 to 2 to accommodate prayer times tables. Uses destructive migration
for simplicity in this implementation.

### Time Zones

Prayer times are calculated in device's local time zone, ensuring accurate notification timing
regardless of location.

### Battery Optimization

The app uses `setExactAndAllowWhileIdle()` for minimal battery impact while maintaining precision.

## Support

For issues or questions related to the prayer times feature, please refer to:

- Android documentation on AlarmManager
- Islamic prayer calculation resources
- Material Design guidelines

---

**Version**: 1.0.0  
**Last Updated**: January 2026  
**Platform**: Android (API 24+)