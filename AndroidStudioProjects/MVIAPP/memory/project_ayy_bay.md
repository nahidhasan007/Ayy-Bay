---
name: project-ayy-bay
description: Ayy Bay Android app - Islamic prayer times + Adhan player + transaction tracker for Bangladesh
metadata:
  type: project
---

App is at /Users/technonext/StudioProjects/Ayy-Bay/AndroidStudioProjects/MVIAPP/

**Why:** Prayer time app focused on Bangladesh (Dhaka). Uses Karachi calculation method + Hanafi madhab.

**Key architecture:**
- MVI pattern (TransactionUiIntent/State/Effect)
- Koin DI
- Room DB for prayer times + transactions
- AlarmManager + ForegroundService for Adhan playback
- Adhan library v1.2.1 (com.batoulapps.adhan:adhan) for prayer time calculation

**Root cause of Adhan not playing on time (fixed 2026-06-09):**
1. PrayerAlarmScheduler had hardcoded times (5:00, 13:00, 17:00, 18:10, 20:00) — replaced with Adhan library
2. PrayerTimeCalculator had a timezone bug (declared `bangladeshTimezoneOffset = 6.0` but never used it) — replaced entirely with Adhan library
3. AzanNotificationReceiver.rescheduleForNextDay() added +24h from current time instead of recalculating — fixed to use PrayerTimeCalculator
4. SchedulePrayerNotificationsUseCase scheduled 7 days but all shared ordinal request codes (0-4), so only day 6 actually stuck — fixed to schedule next occurrence only
5. Default settings were MWL/Shafi instead of Karachi/Hanafi — fixed

**How to apply:** When touching prayer time or alarm logic, always use the Adhan library (not custom astronomical formulas). The single scheduling path is: SchedulePrayerNotificationsUseCase → PrayerTimeRepositoryImpl.schedulePrayerNotification() → AlarmManager with requestCode=prayerName.ordinal. AzanNotificationReceiver reschedules the next day's prayer using PrayerTimeCalculator.