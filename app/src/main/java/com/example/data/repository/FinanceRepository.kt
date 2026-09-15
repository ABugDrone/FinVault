package com.example.data.repository

import com.example.data.calendar.CalendarSyncManager
import com.example.data.local.BudgetDao
import com.example.data.local.CategoryRuleDao
import com.example.data.local.RecurringTransactionDao
import com.example.data.local.TransactionDao
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryRule
import com.example.data.model.RecurrenceFrequency
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryRuleDao: CategoryRuleDao,
    private val recurringTransactionDao: RecurringTransactionDao,
    private val budgetDao: BudgetDao,
    private val calendarSyncManager: CalendarSyncManager
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allRules: Flow<List<CategoryRule>> = categoryRuleDao.getAllRules()
    val allRecurring: Flow<List<RecurringTransactionEntity>> = recurringTransactionDao.getAllRecurring()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsBetween(startTime, endTime)
    }

    suspend fun insertTransaction(transaction: TransactionEntity, syncWithCalendar: Boolean = true): Long {
        var eventId: Long? = transaction.calendarEventId
        if (syncWithCalendar && calendarSyncManager.hasCalendarPermission()) {
            eventId = calendarSyncManager.syncTransactionToCalendar(transaction)
        }
        val txToSave = if (eventId != transaction.calendarEventId) {
            transaction.copy(calendarEventId = eventId)
        } else {
            transaction
        }
        return transactionDao.insertTransaction(txToSave)
    }

    suspend fun updateTransaction(transaction: TransactionEntity, syncWithCalendar: Boolean = true) {
        var eventId: Long? = transaction.calendarEventId
        if (syncWithCalendar && calendarSyncManager.hasCalendarPermission()) {
            eventId = calendarSyncManager.syncTransactionToCalendar(transaction)
        }
        val txToSave = if (eventId != transaction.calendarEventId) {
            transaction.copy(calendarEventId = eventId)
        } else {
            transaction
        }
        transactionDao.updateTransaction(txToSave)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        if (transaction.calendarEventId != null && calendarSyncManager.hasCalendarPermission()) {
            calendarSyncManager.removeCalendarEvent(transaction.calendarEventId)
        }
        transactionDao.deleteTransaction(transaction)
    }

    // --- Recurring Transactions ---
    suspend fun insertRecurring(
        recurring: RecurringTransactionEntity,
        createFirstOccurrenceImmediately: Boolean = true
    ): Long {
        val recurringId = recurringTransactionDao.insertRecurring(recurring)
        if (createFirstOccurrenceImmediately) {
            val initialTx = TransactionEntity(
                title = recurring.title,
                amount = recurring.amount,
                type = recurring.type,
                category = recurring.category,
                tags = recurring.tags,
                timestamp = recurring.startDate,
                encryptedNote = recurring.encryptedNote,
                recurringRuleId = recurringId
            )
            insertTransaction(initialTx, syncWithCalendar = recurring.syncWithCalendar)
        }
        return recurringId
    }

    suspend fun updateRecurring(recurring: RecurringTransactionEntity) {
        recurringTransactionDao.updateRecurring(recurring)
    }

    suspend fun toggleRecurringActive(recurring: RecurringTransactionEntity) {
        recurringTransactionDao.updateRecurring(recurring.copy(isActive = !recurring.isActive))
    }

    suspend fun deleteRecurring(recurring: RecurringTransactionEntity) {
        recurringTransactionDao.deleteRecurring(recurring)
    }

    suspend fun processDueRecurringTransactions(): Int {
        val activeList = recurringTransactionDao.getActiveRecurring()
        val now = System.currentTimeMillis()
        var generatedCount = 0

        for (recurring in activeList) {
            var lastDate = recurring.lastGeneratedDate
            var nextDate = calculateNextOccurrence(lastDate, recurring.frequency)
            var updatedRecurring = recurring

            while (nextDate <= now) {
                if (recurring.hasEndDate && recurring.endDateMillis != null && nextDate > recurring.endDateMillis) {
                    updatedRecurring = updatedRecurring.copy(isActive = false)
                    break
                }

                val newTx = TransactionEntity(
                    title = recurring.title,
                    amount = recurring.amount,
                    type = recurring.type,
                    category = recurring.category,
                    tags = recurring.tags,
                    timestamp = nextDate,
                    encryptedNote = recurring.encryptedNote,
                    recurringRuleId = recurring.id
                )
                insertTransaction(newTx, syncWithCalendar = recurring.syncWithCalendar)
                generatedCount++

                lastDate = nextDate
                updatedRecurring = updatedRecurring.copy(lastGeneratedDate = lastDate)
                nextDate = calculateNextOccurrence(lastDate, recurring.frequency)
            }

            recurringTransactionDao.updateRecurring(updatedRecurring)
        }
        return generatedCount
    }

    fun calculateNextOccurrence(currentDate: Long, frequency: RecurrenceFrequency): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = currentDate }
        when (frequency) {
            RecurrenceFrequency.DAILY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            RecurrenceFrequency.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            RecurrenceFrequency.MONTHLY -> cal.add(Calendar.MONTH, 1)
            RecurrenceFrequency.ANNUALLY -> cal.add(Calendar.YEAR, 1)
        }
        return cal.timeInMillis
    }

    // --- Budgets ---
    suspend fun setBudget(category: String, limit: Double, notifyThresholdPercent: Double = 85.0) {
        val existing = budgetDao.getBudgetByCategory(category)
        if (existing != null) {
            budgetDao.updateBudget(existing.copy(monthlyLimit = limit, notifyThresholdPercent = notifyThresholdPercent))
        } else {
            budgetDao.insertBudget(BudgetEntity(category = category, monthlyLimit = limit, notifyThresholdPercent = notifyThresholdPercent))
        }
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }

    suspend fun deleteBudgetByCategory(category: String) {
        budgetDao.deleteByCategory(category)
    }

    // --- Category Rules ---
    suspend fun addCategoryRule(rule: CategoryRule): Long {
        return categoryRuleDao.insertRule(rule)
    }

    suspend fun deleteCategoryRule(rule: CategoryRule) {
        categoryRuleDao.deleteRule(rule)
    }

    suspend fun syncAllToCalendar(transactions: List<TransactionEntity>): Int {
        if (!calendarSyncManager.hasCalendarPermission()) return 0
        var syncedCount = 0
        for (tx in transactions) {
            val eventId = calendarSyncManager.syncTransactionToCalendar(tx)
            if (eventId != null && eventId != tx.calendarEventId) {
                transactionDao.updateTransaction(tx.copy(calendarEventId = eventId))
                syncedCount++
            }
        }
        return syncedCount
    }

    fun hasCalendarPermission(): Boolean = calendarSyncManager.hasCalendarPermission()

    /**
     * Smart Offline Auto-Categorization:
     * Inspects text (title / note) against custom user rules and predefined financial patterns.
     */
    suspend fun inferCategoryAndTags(text: String): Triple<String?, TransactionType?, List<String>> {
        val cleanText = text.trim().lowercase()
        if (cleanText.isEmpty()) {
            return Triple(null, null, emptyList())
        }

        val rules = categoryRuleDao.getAllRules().firstOrNull() ?: emptyList()
        for (rule in rules) {
            val kw = rule.keyword.trim().lowercase()
            if (kw.isNotEmpty() && cleanText.contains(kw)) {
                return Triple(rule.targetCategory, rule.defaultType, rule.defaultTags)
            }
        }

        // Secondary heuristics
        val isLikelyIncome = cleanText.contains("salary") || cleanText.contains("income") ||
                cleanText.contains("earn") || cleanText.contains("pay") || cleanText.contains("deposit") ||
                cleanText.contains("dividend") || cleanText.contains("freelance") || cleanText.contains("client")

        return if (isLikelyIncome) {
            Triple("Income", TransactionType.INCOME, listOf("Earning"))
        } else {
            Triple("General", TransactionType.EXPENSE, emptyList())
        }
    }
}

