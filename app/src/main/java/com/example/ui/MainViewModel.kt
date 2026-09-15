package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.calendar.CalendarSyncManager
import com.example.data.export.DataExportManager
import com.example.data.local.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetProgress
import com.example.data.model.CategoryRule
import com.example.data.model.CountryLegalProfile
import com.example.data.model.ExportDateRange
import com.example.data.model.ExportFormat
import com.example.data.model.FinancialSummary
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.TimeframeFilter
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.notifications.BudgetNotificationManager
import com.example.data.repository.FinanceRepository
import com.example.data.security.CountryPreferencesManager
import com.example.ui.components.BarGroup
import com.example.ui.components.LinePoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository
    private val calendarSyncManager: CalendarSyncManager
    private val notificationManager: BudgetNotificationManager
    private val countryPreferencesManager: CountryPreferencesManager

    init {
        calendarSyncManager = CalendarSyncManager(application)
        notificationManager = BudgetNotificationManager(application)
        countryPreferencesManager = CountryPreferencesManager(application)
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FinanceRepository(
            transactionDao = database.transactionDao(),
            categoryRuleDao = database.categoryRuleDao(),
            recurringTransactionDao = database.recurringTransactionDao(),
            budgetDao = database.budgetDao(),
            calendarSyncManager = calendarSyncManager
        )

        // Automatically evaluate and process recurring transactions that have come due
        viewModelScope.launch {
            val count = repository.processDueRecurringTransactions()
            if (count > 0) {
                _syncMessage.value = "Processed $count due recurring transactions!"
                checkAllBudgetAlerts()
            }
        }
    }

    fun hasNotificationPermission(): Boolean {
        return notificationManager.hasNotificationPermission()
    }

    fun sendTestNotification(category: String = "Dining & Food", isExceeded: Boolean = false) {
        val symbol = currentJurisdictionProfile.value.currencySymbol
        val budget = allBudgets.value.find { it.category.equals(category, ignoreCase = true) }
        val limit = budget?.monthlyLimit ?: 400.0
        val spent = if (isExceeded) limit * 1.12 else limit * ((budget?.notifyThresholdPercent ?: 85.0) / 100.0)
        notificationManager.sendBudgetAlert(
            category = category,
            spent = spent,
            limit = limit,
            isExceeded = isExceeded,
            currencySymbol = symbol
        )
        val alertLabel = if (isExceeded) "Cap Exceeded (112%)" else "Warning Threshold (${(budget?.notifyThresholdPercent ?: 85.0).toInt()}%)"
        _syncMessage.value = "Dispatched test alert: $category [$alertLabel]"
    }

    val currentJurisdictionProfile: StateFlow<CountryLegalProfile> = countryPreferencesManager.currentProfile
    val isSetupCompleted: StateFlow<Boolean> = countryPreferencesManager.isSetupCompleted

    fun saveLegalJurisdiction(profile: CountryLegalProfile) {
        countryPreferencesManager.saveLegalJurisdictionSelection(profile)
        _syncMessage.value = "Legal jurisdiction established: ${profile.name} (${profile.currencyCode} ${profile.currencySymbol})"
    }

    fun resetJurisdictionSetup() {
        countryPreferencesManager.resetSetupForReconfiguration()
    }

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRules: StateFlow<List<CategoryRule>> = repository.allRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecurring: StateFlow<List<RecurringTransactionEntity>> = repository.allRecurring
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Budget Progress computation for current month
    val budgetProgressList: StateFlow<List<CategoryBudgetProgress>> = combine(
        allTransactions,
        allBudgets
    ) { transactions, budgets ->
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val monthStart = cal.timeInMillis

        budgets.map { budget ->
            val spent = transactions
                .filter { it.type == TransactionType.EXPENSE && it.timestamp >= monthStart && it.category.equals(budget.category, ignoreCase = true) }
                .sumOf { it.amount }
            val percent = if (budget.monthlyLimit > 0) (spent / budget.monthlyLimit).toFloat() else 0f
            val isWarning = percent >= (budget.notifyThresholdPercent / 100f) && percent < 1.0f
            val isExceeded = percent >= 1.0f
            val remaining = (budget.monthlyLimit - spent).coerceAtLeast(0.0)

            CategoryBudgetProgress(
                category = budget.category,
                limit = budget.monthlyLimit,
                spent = spent,
                percent = percent,
                isWarning = isWarning,
                isExceeded = isExceeded,
                remaining = remaining
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _timeframeFilter = MutableStateFlow(TimeframeFilter.MONTHLY)
    val timeframeFilter: StateFlow<TimeframeFilter> = _timeframeFilter.asStateFlow()

    private val _privacyMode = MutableStateFlow(false)
    val privacyMode: StateFlow<Boolean> = _privacyMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow<String?>(null)
    val categoryFilter: StateFlow<String?> = _categoryFilter.asStateFlow()

    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    private val _calendarMonth = MutableStateFlow(Calendar.getInstance())
    val calendarMonth: StateFlow<Calendar> = _calendarMonth.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Filtered transactions based on timeframe, search query, and category filter
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        timeframeFilter,
        searchQuery,
        categoryFilter
    ) { transactions, timeframe, query, catFilter ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        val startTime = when (timeframe) {
            TimeframeFilter.DAILY -> {
                cal.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            TimeframeFilter.WEEKLY -> {
                cal.apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            TimeframeFilter.MONTHLY -> {
                cal.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            TimeframeFilter.ANNUAL -> {
                cal.apply {
                    set(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
        }

        transactions.filter { tx ->
            val withinTime = tx.timestamp >= startTime
            val matchesQuery = query.isBlank() || tx.title.contains(query, ignoreCase = true) ||
                    tx.category.contains(query, ignoreCase = true) ||
                    tx.tags.any { it.contains(query, ignoreCase = true) }
            val matchesCategory = catFilter == null || tx.category == catFilter
            withinTime && matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Summary
    val financialSummary: StateFlow<FinancialSummary> = filteredTransactions.combine(timeframeFilter) { txList, _ ->
        val income = txList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = txList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val net = income - expense
        val rate = if (income > 0.0) ((net / income) * 100).coerceAtLeast(0.0) else 0.0
        FinancialSummary(
            totalIncome = income,
            totalExpense = expense,
            netSavings = net,
            savingsRate = rate,
            transactionCount = txList.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    // Bar Chart Data
    val barChartData: StateFlow<List<BarGroup>> = filteredTransactions.combine(timeframeFilter) { txList, timeframe ->
        computeBarGroups(txList, timeframe)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Line Chart Data (Cumulative net trend)
    val lineChartData: StateFlow<List<LinePoint>> = filteredTransactions.combine(timeframeFilter) { txList, _ ->
        computeLinePoints(txList)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Day Transactions for Calendar view
    val selectedDayTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        selectedDateMillis
    ) { txList, selectedMillis ->
        val selCal = Calendar.getInstance().apply { timeInMillis = selectedMillis }
        txList.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            txCal.get(Calendar.YEAR) == selCal.get(Calendar.YEAR) &&
                    txCal.get(Calendar.DAY_OF_YEAR) == selCal.get(Calendar.DAY_OF_YEAR)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTimeframe(filter: TimeframeFilter) {
        _timeframeFilter.value = filter
    }

    fun togglePrivacyMode() {
        _privacyMode.value = !_privacyMode.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _categoryFilter.value = category
    }

    fun selectDate(millis: Long) {
        _selectedDateMillis.value = millis
    }

    fun selectToday() {
        _selectedDateMillis.value = System.currentTimeMillis()
        _calendarMonth.value = Calendar.getInstance()
    }

    fun nextMonth() {
        val next = _calendarMonth.value.clone() as Calendar
        next.add(Calendar.MONTH, 1)
        _calendarMonth.value = next
    }

    fun prevMonth() {
        val prev = _calendarMonth.value.clone() as Calendar
        prev.add(Calendar.MONTH, -1)
        _calendarMonth.value = prev
    }

    fun addTransaction(transaction: TransactionEntity, syncCalendar: Boolean) {
        viewModelScope.launch {
            repository.insertTransaction(transaction, syncCalendar)
            if (syncCalendar && repository.hasCalendarPermission()) {
                _syncMessage.value = "Synced with device calendar!"
            }

            // Check if this expense pushed category near or over budget
            if (transaction.type == TransactionType.EXPENSE) {
                checkBudgetAlertForCategory(transaction.category, transaction.amount)
            }
        }
    }

    private fun checkBudgetAlertForCategory(category: String, addedAmount: Double) {
        val currentBudgets = allBudgets.value
        val budget = currentBudgets.find { it.category.equals(category, ignoreCase = true) } ?: return
        val currentMonthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val currentSpent = allTransactions.value
            .filter { it.type == TransactionType.EXPENSE && it.timestamp >= currentMonthStart && it.category.equals(category, ignoreCase = true) }
            .sumOf { it.amount } + addedAmount

        val thresholdAmount = budget.monthlyLimit * (budget.notifyThresholdPercent / 100.0)
        val symbol = currentJurisdictionProfile.value.currencySymbol

        if (currentSpent >= budget.monthlyLimit) {
            notificationManager.sendBudgetAlert(
                category = budget.category,
                spent = currentSpent,
                limit = budget.monthlyLimit,
                isExceeded = true,
                currencySymbol = symbol
            )
        } else if (currentSpent >= thresholdAmount) {
            notificationManager.sendBudgetAlert(
                category = budget.category,
                spent = currentSpent,
                limit = budget.monthlyLimit,
                isExceeded = false,
                currencySymbol = symbol
            )
        }
    }

    fun checkAllBudgetAlerts() {
        allBudgets.value.forEach { budget ->
            checkBudgetAlertForCategory(budget.category, 0.0)
        }
    }

    // --- Recurring Transactions ---
    fun addRecurringTransaction(
        recurring: RecurringTransactionEntity,
        createFirstOccurrenceImmediately: Boolean = true
    ) {
        viewModelScope.launch {
            repository.insertRecurring(recurring, createFirstOccurrenceImmediately)
            _syncMessage.value = "Recurring schedule saved!"
            if (recurring.type == TransactionType.EXPENSE && createFirstOccurrenceImmediately) {
                checkBudgetAlertForCategory(recurring.category, recurring.amount)
            }
        }
    }

    fun toggleRecurringActive(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.toggleRecurringActive(recurring)
        }
    }

    fun deleteRecurring(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.deleteRecurring(recurring)
            _syncMessage.value = "Recurring rule removed."
        }
    }

    fun processDueRecurringTransactionsNow() {
        viewModelScope.launch {
            val count = repository.processDueRecurringTransactions()
            if (count > 0) {
                _syncMessage.value = "Processed $count recurring transaction(s)!"
                checkAllBudgetAlerts()
            } else {
                _syncMessage.value = "All recurring transactions are up to date."
            }
        }
    }

    // --- Budgets ---
    fun setCategoryBudget(category: String, limit: Double, notifyThresholdPercent: Double = 85.0) {
        viewModelScope.launch {
            repository.setBudget(category, limit, notifyThresholdPercent)
            val symbol = currentJurisdictionProfile.value.currencySymbol
            _syncMessage.value = "Budget for $category set to $symbol${String.format(Locale.US, "%.2f", limit)} (Alert at ${notifyThresholdPercent.toInt()}%)"
            // Re-evaluate if this category is already approaching or exceeding the new limit
            checkBudgetAlertForCategory(category, 0.0)
        }
    }

    fun deleteCategoryBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            _syncMessage.value = "Budget for ${budget.category} removed."
        }
    }

    fun deleteCategoryBudgetByName(category: String) {
        viewModelScope.launch {
            repository.deleteBudgetByCategory(category)
            _syncMessage.value = "Budget for $category removed."
        }
    }

    // --- Data Export ---
    fun exportFinancialData(
        context: Context,
        range: ExportDateRange,
        customStartMillis: Long,
        customEndMillis: Long,
        format: ExportFormat
    ): Uri {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        val (startTime, endTime, label) = when (range) {
            ExportDateRange.ALL_TIME -> Triple(0L, Long.MAX_VALUE, "All Time")
            ExportDateRange.THIS_MONTH -> {
                cal.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                Triple(cal.timeInMillis, Long.MAX_VALUE, "This Month (${SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date())})")
            }
            ExportDateRange.LAST_MONTH -> {
                cal.apply {
                    add(Calendar.MONTH, -1)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val start = cal.timeInMillis
                val endCal = (cal.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }
                Triple(start, endCal.timeInMillis, "Last Month (${SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(cal.time)})")
            }
            ExportDateRange.LAST_30_DAYS -> {
                val start = now - (30L * 24 * 3600 * 1000)
                Triple(start, now, "Past 30 Days")
            }
            ExportDateRange.YEAR_TO_DATE -> {
                cal.apply {
                    set(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                Triple(cal.timeInMillis, now, "Year to Date (${cal.get(Calendar.YEAR)})")
            }
            ExportDateRange.CUSTOM -> {
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startStr = df.format(Date(customStartMillis))
                val endStr = df.format(Date(customEndMillis))
                Triple(customStartMillis, customEndMillis, "$startStr to $endStr")
            }
        }

        val matchingTransactions = allTransactions.value.filter {
            it.timestamp in startTime..endTime
        }.sortedByDescending { it.timestamp }

        return when (format) {
            ExportFormat.CSV -> {
                DataExportManager.exportToCsv(context, matchingTransactions, label)
            }
            ExportFormat.PDF -> {
                val inc = matchingTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val exp = matchingTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                val net = inc - exp
                val rate = if (inc > 0) ((net / inc) * 100).coerceAtLeast(0.0) else 0.0
                val summary = FinancialSummary(
                    totalIncome = inc,
                    totalExpense = exp,
                    netSavings = net,
                    savingsRate = rate,
                    transactionCount = matchingTransactions.size
                )
                DataExportManager.exportToPdf(context, matchingTransactions, label, summary)
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addCustomRule(keyword: String, category: String, type: TransactionType, tags: List<String>) {
        viewModelScope.launch {
            val rule = CategoryRule(
                keyword = keyword.trim(),
                targetCategory = category.trim(),
                defaultType = type,
                defaultTags = tags
            )
            repository.addCategoryRule(rule)
        }
    }

    fun deleteCustomRule(rule: CategoryRule) {
        viewModelScope.launch {
            repository.deleteCategoryRule(rule)
        }
    }

    fun syncAllToDeviceCalendar() {
        viewModelScope.launch {
            if (!repository.hasCalendarPermission()) {
                _syncMessage.value = "Calendar permission required."
                return@launch
            }
            val count = repository.syncAllToCalendar(allTransactions.value)
            _syncMessage.value = "Successfully synced $count records to default calendar!"
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    fun hasCalendarPermission(): Boolean = repository.hasCalendarPermission()

    suspend fun inferCategoryAndTags(text: String): Triple<String?, TransactionType?, List<String>> {
        return repository.inferCategoryAndTags(text)
    }

    private fun computeBarGroups(transactions: List<TransactionEntity>, timeframe: TimeframeFilter): List<BarGroup> {
        val sorted = transactions.sortedBy { it.timestamp }
        return when (timeframe) {
            TimeframeFilter.DAILY -> {
                // Group by 4-hour intervals of today
                val groups = mutableListOf<BarGroup>()
                val hours = listOf("0-6h", "6-12h", "12-18h", "18-24h")
                for (h in 0..3) {
                    val inc = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        c.get(Calendar.HOUR_OF_DAY) / 6 == h && it.type == TransactionType.INCOME
                    }.sumOf { it.amount }
                    val exp = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        c.get(Calendar.HOUR_OF_DAY) / 6 == h && it.type == TransactionType.EXPENSE
                    }.sumOf { it.amount }
                    groups.add(BarGroup(hours[h], inc, exp))
                }
                groups
            }
            TimeframeFilter.WEEKLY -> {
                val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                val groups = mutableListOf<BarGroup>()
                for (d in 1..7) {
                    val inc = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        c.get(Calendar.DAY_OF_WEEK) == d && it.type == TransactionType.INCOME
                    }.sumOf { it.amount }
                    val exp = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        c.get(Calendar.DAY_OF_WEEK) == d && it.type == TransactionType.EXPENSE
                    }.sumOf { it.amount }
                    groups.add(BarGroup(days[d - 1], inc, exp))
                }
                groups
            }
            TimeframeFilter.MONTHLY -> {
                val groups = mutableListOf<BarGroup>()
                val weeks = listOf("W1", "W2", "W3", "W4", "W5")
                for (w in 0..4) {
                    val inc = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        val day = c.get(Calendar.DAY_OF_MONTH)
                        ((day - 1) / 7).coerceAtMost(4) == w && it.type == TransactionType.INCOME
                    }.sumOf { it.amount }
                    val exp = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        val day = c.get(Calendar.DAY_OF_MONTH)
                        ((day - 1) / 7).coerceAtMost(4) == w && it.type == TransactionType.EXPENSE
                    }.sumOf { it.amount }
                    groups.add(BarGroup(weeks[w], inc, exp))
                }
                groups
            }
            TimeframeFilter.ANNUAL -> {
                val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                val groups = mutableListOf<BarGroup>()
                for (m in 0..11) {
                    val inc = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        c.get(Calendar.MONTH) == m && it.type == TransactionType.INCOME
                    }.sumOf { it.amount }
                    val exp = sorted.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        c.get(Calendar.MONTH) == m && it.type == TransactionType.EXPENSE
                    }.sumOf { it.amount }
                    groups.add(BarGroup(months[m], inc, exp))
                }
                groups
            }
        }
    }

    private fun computeLinePoints(transactions: List<TransactionEntity>): List<LinePoint> {
        val sorted = transactions.sortedBy { it.timestamp }
        if (sorted.isEmpty()) {
            return listOf(LinePoint("Start", 0.0), LinePoint("Now", 0.0))
        }

        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        var cumulative = 0.0
        val points = mutableListOf<LinePoint>()
        points.add(LinePoint("Start", 0.0))

        sorted.forEach { tx ->
            if (tx.type == TransactionType.INCOME) {
                cumulative += tx.amount
            } else {
                cumulative -= tx.amount
            }
            val label = sdf.format(Date(tx.timestamp))
            points.add(LinePoint(label, cumulative))
        }
        return points
    }
}
