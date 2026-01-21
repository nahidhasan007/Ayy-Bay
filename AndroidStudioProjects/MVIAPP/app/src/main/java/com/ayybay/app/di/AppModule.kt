package com.ayybay.app.di

import android.content.Context
import android.app.Application
import com.ayybay.app.AyyBayViewModel
import com.ayybay.app.data.PrayerTimeCalculator
import com.ayybay.app.data.local.AppDatabase
import com.ayybay.app.data.repository.TransactionRepositoryImpl
import com.ayybay.app.data.repository.PrayerTimeRepositoryImpl
import com.ayybay.app.domain.repository.TransactionRepository
import com.ayybay.app.domain.repository.PrayerTimeRepository
import com.ayybay.app.domain.usecase.*
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
}

fun provideAppDatabase(context: Context): AppDatabase {
    return AppDatabase.getDatabase(context)
}