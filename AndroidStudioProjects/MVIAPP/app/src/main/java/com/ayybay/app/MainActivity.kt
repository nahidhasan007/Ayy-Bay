package com.ayybay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.screen.HomeScreen
import com.ayybay.app.ui.theme.MVIAPPTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val ayyBayViewModel by viewModel<AyyBayViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVIAPPTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AyyBayApp(ayyBayViewModel)
                }
            }
        }
    }
}

@Composable
fun AyyBayApp(
    viewModel: AyyBayViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onAddTransaction = { transaction ->
            viewModel.handleIntent(TransactionUiIntent.AddTransaction(transaction))
        },
        onUpdateTransaction = { transaction ->
            viewModel.handleIntent(TransactionUiIntent.UpdateTransaction(transaction))
        },
        onDeleteTransaction = { transaction ->
            viewModel.handleIntent(TransactionUiIntent.DeleteTransaction(transaction))
        }
    )
}