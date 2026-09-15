package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.AddTransactionSheet
import com.example.ui.components.BudgetManageDialog
import com.example.ui.components.ExportDataDialog
import com.example.ui.components.RecurringManageDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PreSetupScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VaultSettingsScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.FinVaultTheme
import com.example.ui.theme.VaultSurface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinVaultTheme {
                FinVaultApp()
            }
        }
    }
}

@Composable
fun FinVaultApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var hasCalendarPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
        )
    }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.READ_CALENDAR] == true &&
                permissions[Manifest.permission.WRITE_CALENDAR] == true
        hasCalendarPermission = granted
        if (granted) {
            viewModel.syncAllToDeviceCalendar()
        }
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            viewModel.checkAllBudgetAlerts()
        }
    }

    val requestNotificationPermission: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Handle deep navigation from system notification PendingIntent
    LaunchedEffect(Unit) {
        val activity = context as? ComponentActivity
        val extraNav = activity?.intent?.getStringExtra("EXTRA_NAVIGATE_TO")
        if (extraNav == "budgets") {
            showBudgetDialog = true
            activity.intent.removeExtra("EXTRA_NAVIGATE_TO")
        }
    }

    // State bindings
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val summary by viewModel.financialSummary.collectAsStateWithLifecycle()
    val timeframe by viewModel.timeframeFilter.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.privacyMode.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
    val selectedDateMillis by viewModel.selectedDateMillis.collectAsStateWithLifecycle()
    val calendarMonth by viewModel.calendarMonth.collectAsStateWithLifecycle()
    val selectedDayTransactions by viewModel.selectedDayTransactions.collectAsStateWithLifecycle()
    val barChartData by viewModel.barChartData.collectAsStateWithLifecycle()
    val lineChartData by viewModel.lineChartData.collectAsStateWithLifecycle()
    val rules by viewModel.allRules.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()
    val allRecurring by viewModel.allRecurring.collectAsStateWithLifecycle()
    val allBudgets by viewModel.allBudgets.collectAsStateWithLifecycle()
    val budgetProgressList by viewModel.budgetProgressList.collectAsStateWithLifecycle()
    val isSetupCompleted by viewModel.isSetupCompleted.collectAsStateWithLifecycle()
    val currentJurisdiction by viewModel.currentJurisdictionProfile.collectAsStateWithLifecycle()

    var isSplashVisible by remember { mutableStateOf(true) }
    var isReconfiguringJurisdiction by remember { mutableStateOf(false) }

    var activeNavTab by remember { mutableIntStateOf(0) }
    var isAddSheetOpen by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showRecurringDialog by remember { mutableStateOf(false) }

    LaunchedEffect(syncMessage) {
        syncMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSyncMessage()
        }
    }

    if (isSplashVisible) {
        SplashScreen(
            isSetupCompleted = isSetupCompleted,
            onSplashFinished = {
                isSplashVisible = false
            }
        )
        return
    }

    if (!isSetupCompleted || isReconfiguringJurisdiction) {
        PreSetupScreen(
            initialCountryCode = currentJurisdiction.code,
            isReconfiguring = isReconfiguringJurisdiction,
            onCompleteSetup = { profile ->
                viewModel.saveLegalJurisdiction(profile)
                isReconfiguringJurisdiction = false
            },
            onCancelReconfiguration = if (isReconfiguringJurisdiction) {
                { isReconfiguringJurisdiction = false }
            } else null
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = VaultSurface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    NavDestination(
                        title = "Ledger",
                        selectedIcon = Icons.Filled.ReceiptLong,
                        unselectedIcon = Icons.Outlined.ReceiptLong,
                        tag = "nav_ledger"
                    ),
                    NavDestination(
                        title = "Analytics",
                        selectedIcon = Icons.Filled.PieChart,
                        unselectedIcon = Icons.Outlined.PieChart,
                        tag = "nav_analytics"
                    ),
                    NavDestination(
                        title = "Calendar",
                        selectedIcon = Icons.Filled.CalendarMonth,
                        unselectedIcon = Icons.Outlined.CalendarMonth,
                        tag = "nav_calendar"
                    ),
                    NavDestination(
                        title = "Vault",
                        selectedIcon = Icons.Filled.Shield,
                        unselectedIcon = Icons.Outlined.Shield,
                        tag = "nav_vault"
                    )
                )

                navItems.forEachIndexed { index, item ->
                    val isSelected = activeNavTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeNavTab = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyanAccent,
                            indicatorColor = CyanAccent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeNavTab) {
                0 -> {
                    DashboardScreen(
                        summary = summary,
                        transactions = filteredTransactions,
                        timeframe = timeframe,
                        isPrivacyMode = isPrivacyMode,
                        searchQuery = searchQuery,
                        categoryFilter = categoryFilter,
                        onTimeframeChange = { viewModel.setTimeframe(it) },
                        onTogglePrivacy = { viewModel.togglePrivacyMode() },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onAddClick = { isAddSheetOpen = true },
                        onOpenBudgets = { showBudgetDialog = true },
                        onOpenRecurring = { showRecurringDialog = true },
                        onOpenExport = { showExportDialog = true }
                    )
                }
                1 -> {
                    AnalyticsScreen(
                        summary = summary,
                        transactions = filteredTransactions,
                        barGroups = barChartData,
                        linePoints = lineChartData,
                        timeframe = timeframe,
                        budgetProgress = budgetProgressList,
                        isPrivacyMode = isPrivacyMode,
                        onTimeframeChange = { viewModel.setTimeframe(it) },
                        onTogglePrivacy = { viewModel.togglePrivacyMode() },
                        onOpenBudgetDialog = { showBudgetDialog = true },
                        onOpenExportDialog = { showExportDialog = true }
                    )
                }
                2 -> {
                    CalendarScreen(
                        currentMonthCalendar = calendarMonth,
                        selectedDateMillis = selectedDateMillis,
                        allTransactions = allTransactions,
                        dayTransactions = selectedDayTransactions,
                        hasCalendarPermission = hasCalendarPermission,
                        isPrivacyMode = isPrivacyMode,
                        onDateSelected = { viewModel.selectDate(it) },
                        onPreviousMonth = { viewModel.prevMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onTodayClick = { viewModel.selectToday() },
                        onSyncAllClick = { viewModel.syncAllToDeviceCalendar() },
                        onRequestCalendarPermission = {
                            calendarPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR
                                )
                            )
                        },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onTogglePrivacy = { viewModel.togglePrivacyMode() }
                    )
                }
                3 -> {
                    VaultSettingsScreen(
                        rules = rules,
                        totalTransactionsCount = allTransactions.size,
                        budgetsCount = allBudgets.size,
                        recurringCount = allRecurring.size,
                        isPrivacyMode = isPrivacyMode,
                        hasCalendarPermission = hasCalendarPermission,
                        onTogglePrivacy = { viewModel.togglePrivacyMode() },
                        onSyncAllCalendar = { viewModel.syncAllToDeviceCalendar() },
                        onRequestCalendarPermission = {
                            calendarPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR
                                )
                            )
                        },
                        onAddCustomRule = { kw, cat, t, tgs ->
                            viewModel.addCustomRule(kw, cat, t, tgs)
                        },
                        onDeleteRule = { viewModel.deleteCustomRule(it) },
                        onOpenBudgets = { showBudgetDialog = true },
                        onOpenRecurring = { showRecurringDialog = true },
                        onOpenExport = { showExportDialog = true },
                        jurisdictionProfile = currentJurisdiction,
                        onChangeJurisdiction = { isReconfiguringJurisdiction = true },
                        hasNotificationPermission = hasNotificationPermission,
                        onRequestNotificationPermission = requestNotificationPermission,
                        onSendTestBudgetAlert = { category, isExceeded ->
                            viewModel.sendTestNotification(category, isExceeded)
                        },
                        onRecheckBudgets = {
                            viewModel.checkAllBudgetAlerts()
                        }
                    )
                }
            }

            if (isAddSheetOpen) {
                AddTransactionSheet(
                    onDismiss = { isAddSheetOpen = false },
                    onSaveTransaction = { tx, syncCal ->
                        viewModel.addTransaction(tx, syncCal)
                    },
                    onSaveRecurring = { rec, syncCal ->
                        viewModel.addRecurringTransaction(rec, createFirstOccurrenceImmediately = true)
                    },
                    onInferCategory = { text ->
                        viewModel.inferCategoryAndTags(text)
                    },
                    hasCalendarPermission = hasCalendarPermission,
                    onRequestCalendarPermission = {
                        calendarPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR
                            )
                        )
                    },
                    currencySymbol = currentJurisdiction.currencySymbol
                )
            }

            // Export Data Dialog
            if (showExportDialog) {
                ExportDataDialog(
                    onDismiss = { showExportDialog = false },
                    onConfirmExport = { range, customStart, customEnd, format ->
                        val uri = viewModel.exportFinancialData(
                            context = context,
                            range = range,
                            customStartMillis = customStart,
                            customEndMillis = customEnd,
                            format = format
                        )
                        com.example.data.export.DataExportManager.shareExport(context, uri, format)
                        showExportDialog = false
                    },
                    matchingCount = allTransactions.size
                )
            }

            // Budget Management Dialog
            if (showBudgetDialog) {
                BudgetManageDialog(
                    budgets = allBudgets,
                    budgetProgress = budgetProgressList,
                    onDismiss = { showBudgetDialog = false },
                    onSaveBudget = { category, limit, notifyThreshold ->
                        viewModel.setCategoryBudget(category, limit, notifyThreshold)
                    },
                    onDeleteBudget = { budget ->
                        viewModel.deleteCategoryBudget(budget)
                    },
                    currencySymbol = currentJurisdiction.currencySymbol,
                    hasNotificationPermission = hasNotificationPermission,
                    onRequestNotificationPermission = requestNotificationPermission,
                    onSendTestAlert = { category, isExceeded ->
                        viewModel.sendTestNotification(category, isExceeded)
                    }
                )
            }

            // Recurring Schedules Management Dialog
            if (showRecurringDialog) {
                RecurringManageDialog(
                    recurringList = allRecurring,
                    onDismiss = { showRecurringDialog = false },
                    onToggleActive = { recurring ->
                        viewModel.toggleRecurringActive(recurring)
                    },
                    onDeleteRecurring = { recurring ->
                        viewModel.deleteRecurring(recurring)
                    },
                    onProcessNow = {
                        viewModel.processDueRecurringTransactionsNow()
                    }
                )
            }
        }
    }
}

private data class NavDestination(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
)
