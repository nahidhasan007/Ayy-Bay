package com.ayybay.app.di

import android.content.Context
import com.ayybay.app.AyyBayViewModel
import com.ayybay.app.R
import com.ayybay.app.data.PrayerTimeCalculator
import com.ayybay.app.data.local.AppDatabase
import com.ayybay.app.data.local.AuthPreferences
import com.ayybay.app.data.local.LanguagePreferences
import com.ayybay.app.data.repository.AuthRepositoryImpl
import com.ayybay.app.data.repository.LinkRepositoryImpl
import com.ayybay.app.data.repository.TransactionRepositoryImpl
import com.ayybay.app.data.repository.PrayerTimeRepositoryImpl
import com.ayybay.app.domain.repository.AuthRepository
import com.ayybay.app.domain.repository.LinkRepository
import com.ayybay.app.domain.repository.TransactionRepository
import com.ayybay.app.domain.repository.PrayerTimeRepository
import com.ayybay.app.domain.usecase.*
import com.ayybay.app.presentation.viewmodel.AuthViewModel
import com.ayybay.app.presentation.viewmodel.LanguageViewModel
import com.ayybay.app.presentation.viewmodel.LinkViewModel
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

    // Prayer Calculator
    single { PrayerTimeCalculator() }

    // Repositories
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<PrayerTimeRepository> {
        PrayerTimeRepositoryImpl(
            prayerTimeDao = get(),
            context = androidContext()
        )
    }
    single<LinkRepository> { LinkRepositoryImpl(get()) }

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
            schedulePrayerNotificationsUseCase = get()
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
}

fun provideAppDatabase(context: Context): AppDatabase {
    return AppDatabase.getDatabase(context)
}
