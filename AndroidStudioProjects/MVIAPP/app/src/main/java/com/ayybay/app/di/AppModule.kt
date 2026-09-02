package com.ayybay.app.di

import android.content.Context
import com.ayybay.app.AyyBayViewModel
import com.ayybay.app.R
import com.ayybay.app.data.PrayerTimeCalculator
import com.ayybay.app.data.local.AppDatabase
import com.ayybay.app.data.location.LocationProvider
import com.ayybay.app.data.local.AuthPreferences
import com.ayybay.app.data.local.HealthPreferences
import com.ayybay.app.data.local.LanguagePreferences
import com.ayybay.app.data.repository.AlarmRepositoryImpl
import com.ayybay.app.data.repository.AuthRepositoryImpl
import com.ayybay.app.data.repository.ContactRepositoryImpl
import com.ayybay.app.data.repository.JobBookmarkRepositoryImpl
import com.ayybay.app.data.repository.LinkRepositoryImpl
import com.ayybay.app.data.repository.NotificationRepositoryImpl
import com.ayybay.app.data.repository.NoteRepositoryImpl
import com.ayybay.app.data.repository.PrayerLogRepositoryImpl
import com.ayybay.app.data.repository.QuranProgressRepositoryImpl
import com.ayybay.app.data.repository.TransactionRepositoryImpl
import com.ayybay.app.data.repository.PrayerTimeRepositoryImpl
import com.ayybay.app.domain.repository.AlarmRepository
import com.ayybay.app.domain.repository.AuthRepository
import com.ayybay.app.domain.repository.ContactRepository
import com.ayybay.app.domain.repository.JobBookmarkRepository
import com.ayybay.app.domain.repository.LinkRepository
import com.ayybay.app.domain.repository.NotificationRepository
import com.ayybay.app.domain.repository.NoteRepository
import com.ayybay.app.domain.repository.PrayerLogRepository
import com.ayybay.app.domain.repository.QuranProgressRepository
import com.ayybay.app.domain.repository.TransactionRepository
import com.ayybay.app.domain.repository.PrayerTimeRepository
import com.ayybay.app.domain.usecase.*
import com.ayybay.app.presentation.viewmodel.AlarmViewModel
import com.ayybay.app.presentation.viewmodel.AuthViewModel
import com.ayybay.app.presentation.viewmodel.HealthViewModel
import com.ayybay.app.presentation.viewmodel.JobsViewModel
import com.ayybay.app.presentation.viewmodel.LanguageViewModel
import com.ayybay.app.presentation.viewmodel.LinkViewModel
import com.ayybay.app.presentation.viewmodel.NotificationViewModel
import com.ayybay.app.presentation.viewmodel.NoteViewModel
import com.ayybay.app.presentation.viewmodel.PhoneBookViewModel
import com.ayybay.app.presentation.viewmodel.TrackerViewModel
import com.ayybay.app.presentation.viewmodel.TransactionViewModel
import com.ayybay.app.presentation.viewmodel.PrayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single { provideAppDatabase(androidContext()) }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().prayerTimeDao() }
    single { get<AppDatabase>().linkDao() }
    single { get<AppDatabase>().noteDao() }
    single { get<AppDatabase>().prayerLogDao() }
    single { get<AppDatabase>().quranProgressDao() }
    single { get<AppDatabase>().alarmDao() }
    single { get<AppDatabase>().jobBookmarkDao() }
    single { get<AppDatabase>().appNotificationDao() }

    // Prayer Calculator
    single { PrayerTimeCalculator() }

    // Location (prayer times, Qibla)
    single { LocationProvider(androidContext()) }

    // Repositories
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<PrayerTimeRepository> {
        PrayerTimeRepositoryImpl(
            prayerTimeDao = get(),
            context = androidContext()
        )
    }
    single<LinkRepository> { LinkRepositoryImpl(get()) }
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    single<PrayerLogRepository> { PrayerLogRepositoryImpl(get()) }
    single<QuranProgressRepository> { QuranProgressRepositoryImpl(get()) }
    single<AlarmRepository> { AlarmRepositoryImpl(alarmDao = get(), context = androidContext()) }
    single<ContactRepository> { ContactRepositoryImpl(androidContext()) }
    single<JobBookmarkRepository> { JobBookmarkRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }

    // Auth
    single { AuthPreferences(androidContext()) }
    single<AuthRepository> {
        AuthRepositoryImpl(
            authPreferences = get(),
            webClientId = androidContext().getString(R.string.google_web_client_id)
        )
    }

    // Language
    single { LanguagePreferences(androidContext()) }

    // Health (Age / BMI / Fitness)
    single { HealthPreferences(androidContext()) }

    // Use Cases - Transaction
    factory { GetAllTransactionsUseCase(get()) }
    factory { AddTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetMonthlySummaryUseCase(get()) }

    // Use Cases - Prayer Times
    factory { GetPrayerTimesUseCase(get()) }
    factory { GetPrayerSettingsUseCase(get()) }
    factory { UpdatePrayerSettingsUseCase(get()) }
    factory { SchedulePrayerNotificationsUseCase(get(), get(), get()) }
    factory { TogglePrayerNotificationUseCase(get()) }

    // Use Cases - Links
    factory { GetAllLinksUseCase(get()) }
    factory { GetLinksByCategoryUseCase(get()) }
    factory { AddLinkUseCase(get()) }
    factory { DeleteLinkUseCase(get()) }

    // Use Cases - Auth
    factory { ObserveAuthUserUseCase(get()) }
    factory { SignInWithGoogleUseCase(get()) }
    factory { SignOutUseCase(get()) }

    // Use Cases - Notes
    factory { GetAllNotesUseCase(get()) }
    factory { UpsertNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { ToggleNotePinUseCase(get()) }

    // Use Cases - Salah Tracker
    factory { GetTodayPrayerLogUseCase(get()) }
    factory { TogglePrayerLogUseCase(get()) }
    factory { GetWeeklyPrayerProgressUseCase(get()) }

    // Use Cases - Quran Progress
    factory { GetQuranProgressUseCase(get()) }
    factory { ToggleSurahCompleteUseCase(get()) }
    factory { MarkSurahReadUseCase(get()) }
    factory { GetQuranWeeklyReadingUseCase(get()) }
    factory { GetQuranStreakUseCase(get()) }

    // Use Cases - Alarms
    factory { GetAllAlarmsUseCase(get()) }
    factory { UpsertAlarmUseCase(get()) }
    factory { DeleteAlarmUseCase(get()) }
    factory { ToggleAlarmUseCase(get()) }
    factory { RescheduleAllAlarmsUseCase(get()) }

    // Use Cases - Phone Book
    factory { GetContactsUseCase(get()) }

    // Use Cases - Jobs
    factory { GetBookmarkedJobIdsUseCase(get()) }
    factory { ToggleJobBookmarkUseCase(get()) }

    // Use Cases - Notifications
    factory { GetNotificationsUseCase(get()) }
    factory { GetUnreadNotificationCountUseCase(get()) }
    factory { MarkNotificationReadUseCase(get()) }
    factory { MarkAllNotificationsReadUseCase(get()) }
    factory { AddNotificationUseCase(get()) }

    // ViewModels
    viewModel {
        TransactionViewModel(
            getAllTransactionsUseCase = get(),
            addTransactionUseCase = get(),
            updateTransactionUseCase = get(),
            deleteTransactionUseCase = get(),
            getMonthlySummaryUseCase = get()
        )
    }

    viewModel {
        PrayerViewModel(
            getPrayerTimesUseCase = get(),
            getPrayerSettingsUseCase = get(),
            updatePrayerSettingsUseCase = get(),
            togglePrayerNotificationUseCase = get(),
            schedulePrayerNotificationsUseCase = get(),
            locationProvider = get()
        )
    }

    viewModel {
        AyyBayViewModel(
            transactionViewModel = get()
        )
    }

    viewModel {
        LinkViewModel(
            getAllLinksUseCase = get(),
            getLinksByCategoryUseCase = get(),
            addLinkUseCase = get(),
            deleteLinkUseCase = get(),
            linkRepository = get()
        )
    }

    viewModel {
        AuthViewModel(
            observeAuthUserUseCase = get(),
            signInWithGoogleUseCase = get(),
            signOutUseCase = get()
        )
    }

    viewModel { LanguageViewModel(languagePreferences = get()) }

    viewModel {
        NoteViewModel(
            getAllNotesUseCase = get(),
            upsertNoteUseCase = get(),
            deleteNoteUseCase = get(),
            toggleNotePinUseCase = get()
        )
    }

    viewModel {
        TrackerViewModel(
            getTodayPrayerLogUseCase = get(),
            togglePrayerLogUseCase = get(),
            getWeeklyPrayerProgressUseCase = get(),
            getQuranProgressUseCase = get(),
            toggleSurahCompleteUseCase = get(),
            markSurahReadUseCase = get(),
            getQuranWeeklyReadingUseCase = get(),
            getQuranStreakUseCase = get()
        )
    }

    viewModel { HealthViewModel(healthPreferences = get()) }

    viewModel {
        AlarmViewModel(
            getAllAlarmsUseCase = get(),
            upsertAlarmUseCase = get(),
            deleteAlarmUseCase = get(),
            toggleAlarmUseCase = get()
        )
    }

    viewModel { PhoneBookViewModel(getContactsUseCase = get()) }

    viewModel {
        JobsViewModel(
            getBookmarkedJobIdsUseCase = get(),
            toggleJobBookmarkUseCase = get()
        )
    }

    viewModel {
        NotificationViewModel(
            getNotificationsUseCase = get(),
            getUnreadNotificationCountUseCase = get(),
            markNotificationReadUseCase = get(),
            markAllNotificationsReadUseCase = get()
        )
    }
}

fun provideAppDatabase(context: Context): AppDatabase {
    return AppDatabase.getDatabase(context)
}
