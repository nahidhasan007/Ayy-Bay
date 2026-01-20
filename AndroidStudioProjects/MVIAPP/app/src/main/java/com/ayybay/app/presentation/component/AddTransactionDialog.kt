package com.ayybay.app.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    transaction: Transaction? = null,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var category by remember { mutableStateOf(transaction?.category ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var transactionType by remember { mutableStateOf(transaction?.type ?: TransactionType.INCOME) }

    val categories = listOf(
        "Salary", "Freelance", "Investment", "Other Income",
        "Food", "Transportation", "Entertainment", "Shopping",
        "Bills", "Healthcare", "Education", "Other Expense"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (transaction == null) "Add Transaction" else "Edit Transaction",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Transaction Type",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = transactionType == TransactionType.INCOME,
                        onClick = { transactionType = TransactionType.INCOME },
                        label = { Text("Income") },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = transactionType == TransactionType.EXPENSE,
                        onClick = { transactionType = TransactionType.EXPENSE },
                        label = { Text("Expense") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        val newTransaction = Transaction(
                            id = transaction?.id ?: 0,
                            type = transactionType,
                            amount = amountValue,
                            category = category,
                            description = description,
                            date = transaction?.date ?: Date()
                        )
                        onConfirm(newTransaction)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = amount.isNotBlank() && category.isNotBlank() && description.isNotBlank()
                ) {
                    Text(
                        text = if (transaction == null) "Add Transaction" else "Update Transaction"
                    )
                }
            }
        }
    }
}