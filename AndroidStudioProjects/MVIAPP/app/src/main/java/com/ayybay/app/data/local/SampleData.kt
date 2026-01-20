package com.ayybay.app.data.local

import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType
import java.util.Calendar
import java.util.Date

object SampleData {

    fun getSampleTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()

        return listOf(
            Transaction(
                id = 1,
                type = TransactionType.INCOME,
                amount = 5000.0,
                category = "Salary",
                description = "Monthly salary",
                date = getDate(calendar, 5)
            ),
            Transaction(
                id = 2,
                type = TransactionType.EXPENSE,
                amount = 1200.0,
                category = "Rent",
                description = "Monthly rent payment",
                date = getDate(calendar, 5)
            ),
            Transaction(
                id = 3,
                type = TransactionType.EXPENSE,
                amount = 350.0,
                category = "Food",
                description = "Grocery shopping",
                date = getDate(calendar, 8)
            ),
            Transaction(
                id = 4,
                type = TransactionType.INCOME,
                amount = 800.0,
                category = "Freelance",
                description = "Web development project",
                date = getDate(calendar, 10)
            ),
            Transaction(
                id = 5,
                type = TransactionType.EXPENSE,
                amount = 150.0,
                category = "Transportation",
                description = "Gas and car maintenance",
                date = getDate(calendar, 12)
            ),
            Transaction(
                id = 6,
                type = TransactionType.EXPENSE,
                amount = 200.0,
                category = "Entertainment",
                description = "Movies and dining out",
                date = getDate(calendar, 15)
            ),
            Transaction(
                id = 7,
                type = TransactionType.EXPENSE,
                amount = 100.0,
                category = "Bills",
                description = "Electricity and internet",
                date = getDate(calendar, 18)
            ),
            Transaction(
                id = 8,
                type = TransactionType.INCOME,
                amount = 500.0,
                category = "Investment",
                description = "Stock dividend",
                date = getDate(calendar, 20)
            )
        )
    }

    private fun getDate(calendar: Calendar, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.time
    }
}