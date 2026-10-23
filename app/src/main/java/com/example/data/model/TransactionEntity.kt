package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    ANNUALLY
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val tags: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val encryptedNote: String = "",
    val calendarEventId: Long? = null,
    val recurringRuleId: Long? = null,
    val ledgerId: String = "primary_ledger",
    val businessClassification: String = "" // "STOCK_PURCHASE", "STOCK_SALES", "DEBT_COLLECTION", "OWNER_DRAWING", "OPERATING_EXPENSE", "GENERAL"
)

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val tags: List<String> = emptyList(),
    val frequency: RecurrenceFrequency,
    val startDate: Long = System.currentTimeMillis(),
    val lastGeneratedDate: Long = System.currentTimeMillis(),
    val hasEndDate: Boolean = false,
    val endDateMillis: Long? = null,
    val encryptedNote: String = "",
    val syncWithCalendar: Boolean = true,
    val isActive: Boolean = true,
    val ledgerId: String = "primary_ledger"
)

@Entity(tableName = "category_budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val monthlyLimit: Double,
    val notifyThresholdPercent: Double = 85.0,
    val ledgerId: String = "primary_ledger"
)

data class CategoryBudgetProgress(
    val category: String,
    val limit: Double,
    val spent: Double,
    val percent: Float,
    val isWarning: Boolean,
    val isExceeded: Boolean,
    val remaining: Double
)

@Entity(tableName = "category_rules")
data class CategoryRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
    val targetCategory: String,
    val defaultType: TransactionType,
    val defaultTags: List<String> = emptyList()
)

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netSavings: Double = 0.0,
    val savingsRate: Double = 0.0,
    val transactionCount: Int = 0
)

enum class TimeframeFilter {
    DAILY,
    WEEKLY,
    MONTHLY,
    ANNUAL
}
