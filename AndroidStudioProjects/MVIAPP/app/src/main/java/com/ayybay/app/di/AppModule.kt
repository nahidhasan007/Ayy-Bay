package com.ayybay.app.di

import android.content.Context
import com.ayybay.app.AyyBayViewModel
import com.ayybay.app.data.local.AppDatabase
import com.ayybay.app.data.repository.TransactionRepositoryImpl
import com.ayybay.app.domain.repository.TransactionRepository
import com.ayybay.app.domain.usecase.AddTransactionUseCase
import com.ayybay.app.domain.usecase.DeleteTransactionUseCase
import com.ayybay.app.domain.usecase.GetAllTransactionsUseCase
import com.ayybay.app.domain.usecase.GetMonthlySummaryUseCase
import com.ayybay.app.domain.usecase.UpdateTransactionUseCase
import com.ayybay.app.presentation.viewmodel.TransactionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single { provideAppDatabase(androidContext()) }
    single { get<AppDatabase>().transactionDao() }

    // Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }

    // Use Cases
    factory { GetAllTransactionsUseCase(get()) }
    factory { AddTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetMonthlySummaryUseCase(get()) }

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
        AyyBayViewModel(
            transactionViewModel = get()
        )
    }
}

fun provideAppDatabase(context: Context): AppDatabase {
    return AppDatabase.getDatabase(context)
}