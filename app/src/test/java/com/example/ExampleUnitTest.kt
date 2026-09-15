package com.example

import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetProgress
import com.example.data.model.RecurrenceFrequency
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {

    @Test
    fun testRecurrenceFrequencyAdvancement() {
        val baseCal = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 1, 12, 0, 0)
        }
        val baseTime = baseCal.timeInMillis

        // Daily
        val dailyNext = computeNextDueDate(baseTime, RecurrenceFrequency.DAILY)
        val dailyCal = Calendar.getInstance().apply { timeInMillis = dailyNext }
        assertEquals(2, dailyCal.get(Calendar.DAY_OF_MONTH))

        // Weekly
        val weeklyNext = computeNextDueDate(baseTime, RecurrenceFrequency.WEEKLY)
        val weeklyCal = Calendar.getInstance().apply { timeInMillis = weeklyNext }
        assertEquals(8, weeklyCal.get(Calendar.DAY_OF_MONTH))

        // Monthly
        val monthlyNext = computeNextDueDate(baseTime, RecurrenceFrequency.MONTHLY)
        val monthlyCal = Calendar.getInstance().apply { timeInMillis = monthlyNext }
        assertEquals(Calendar.FEBRUARY, monthlyCal.get(Calendar.MONTH))

        // Annually
        val annualNext = computeNextDueDate(baseTime, RecurrenceFrequency.ANNUALLY)
        val annualCal = Calendar.getInstance().apply { timeInMillis = annualNext }
        assertEquals(2027, annualCal.get(Calendar.YEAR))
    }

    @Test
    fun testBudgetProgressCalculation() {
        val budget = BudgetEntity(category = "Groceries", monthlyLimit = 500.0)
        val spent = 420.0
        val percentage = ((spent / budget.monthlyLimit) * 100.0).toFloat()
        val isExceeded = spent >= budget.monthlyLimit
        val isWarning = spent >= (budget.monthlyLimit * 0.8) && !isExceeded
        val remaining = (budget.monthlyLimit - spent).coerceAtLeast(0.0)

        val progress = CategoryBudgetProgress(
            category = budget.category,
            limit = budget.monthlyLimit,
            spent = spent,
            percent = percentage,
            isWarning = isWarning,
            isExceeded = isExceeded,
            remaining = remaining
        )

        assertEquals(84.0f, progress.percent, 0.01f)
        assertTrue(progress.isWarning)
        assertFalse(progress.isExceeded)
        assertEquals(80.0, progress.remaining, 0.01)
    }

    @Test
    fun testBudgetProgressExceeded() {
        val budget = BudgetEntity(category = "Dining", monthlyLimit = 200.0)
        val spent = 250.0
        val isExceeded = spent >= budget.monthlyLimit
        val isWarning = spent >= (budget.monthlyLimit * 0.8) && !isExceeded

        assertTrue(isExceeded)
        assertFalse(isWarning)
    }

    private fun computeNextDueDate(fromMillis: Long, frequency: RecurrenceFrequency): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = fromMillis }
        when (frequency) {
            RecurrenceFrequency.DAILY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            RecurrenceFrequency.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            RecurrenceFrequency.MONTHLY -> cal.add(Calendar.MONTH, 1)
            RecurrenceFrequency.ANNUALLY -> cal.add(Calendar.YEAR, 1)
        }
        return cal.timeInMillis
    }
}
