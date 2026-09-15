package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import com.example.data.model.CategoryBudgetProgress
import com.example.data.model.FinancialSummary
import com.example.data.model.TimeframeFilter
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.security.CryptoManager
import com.example.ui.components.BarGroup
import com.example.ui.components.CategoryPalette
import com.example.ui.components.FinVaultBarChart
import com.example.ui.components.FinVaultDonutChart
import com.example.ui.components.FinVaultLineChart
import com.example.ui.components.LinePoint
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseExpense

enum class ChartType {
    PIE,
    BAR,
    LINE
}

@Composable
fun AnalyticsScreen(
    summary: FinancialSummary,
    transactions: List<TransactionEntity>,
    barGroups: List<BarGroup>,
    linePoints: List<LinePoint>,
    timeframe: TimeframeFilter,
    budgetProgress: List<CategoryBudgetProgress> = emptyList(),
    isPrivacyMode: Boolean,
    onTimeframeChange: (TimeframeFilter) -> Unit,
    onTogglePrivacy: () -> Unit,
    onOpenBudgetDialog: () -> Unit = {},
    onOpenExportDialog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedChart by remember { mutableStateOf(ChartType.PIE) }
    var pieTypeFilter by remember { mutableStateOf(TransactionType.EXPENSE) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Visual Analytics",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Interactive Financial Intelligence",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenExportDialog,
                        modifier = Modifier.size(38.dp).testTag("analytics_button_export")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export Data",
                            tint = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onTogglePrivacy,
                        modifier = Modifier.size(38.dp).testTag("analytics_toggle_privacy")
                    ) {
                        Icon(
                            imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Privacy",
                            tint = if (isPrivacyMode) GoldAccent else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Timeframe Selector
        item {
            TimeframeSelectorRow(
                selectedTimeframe = timeframe,
                onSelect = onTimeframeChange,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
            )
        }

        // Chart Type Switcher (Pie / Bar / Line)
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val charts = listOf(
                        ChartType.PIE to "Pie / Donut",
                        ChartType.BAR to "Bar Comparison",
                        ChartType.LINE to "Line Trajectory"
                    )

                    charts.forEach { (type, label) ->
                        val isSelected = type == selectedChart
                        val icon = when (type) {
                            ChartType.PIE -> Icons.Default.PieChart
                            ChartType.BAR -> Icons.Default.BarChart
                            ChartType.LINE -> Icons.Default.ShowChart
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyanAccent else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedChart = type }
                                .testTag("chart_tab_${type.name}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (type == ChartType.PIE) "Pie" else if (type == ChartType.BAR) "Bar" else "Line",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Selected Chart Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedChart) {
                        ChartType.PIE -> {
                            // Sub-toggle for Pie: Expense vs Income breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Category Distribution",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(2.dp)
                                ) {
                                    val isExp = pieTypeFilter == TransactionType.EXPENSE
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isExp) RoseExpense else Color.Transparent)
                                            .clickable { pieTypeFilter = TransactionType.EXPENSE }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .testTag("pie_toggle_expense")
                                    ) {
                                        Text(
                                            text = "Expenses",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExp) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    val isInc = pieTypeFilter == TransactionType.INCOME
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isInc) EmeraldIncome else Color.Transparent)
                                            .clickable { pieTypeFilter = TransactionType.INCOME }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .testTag("pie_toggle_income")
                                    ) {
                                        Text(
                                            text = "Earnings",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isInc) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            FinVaultDonutChart(
                                transactions = transactions,
                                typeFilter = pieTypeFilter,
                                isPrivacyMode = isPrivacyMode
                            )
                        }
                        ChartType.BAR -> {
                            Text(
                                text = "Earnings vs Expenses by Period",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            FinVaultBarChart(
                                barGroups = barGroups,
                                isPrivacyMode = isPrivacyMode
                            )
                        }
                        ChartType.LINE -> {
                            Text(
                                text = "Net Financial Growth Progression",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            FinVaultLineChart(
                                points = linePoints,
                                isPrivacyMode = isPrivacyMode
                            )
                        }
                    }
                }
            }
        }

        // Monthly Category Budgets Progress Section
        if (budgetProgress.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .testTag("budget_progress_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyanAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Monthly Budget Limits",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val warningsCount = budgetProgress.count { it.isWarning || it.isExceeded }
                                    Text(
                                        text = if (warningsCount > 0) "$warningsCount categories near/over limit" else "All category budgets healthy",
                                        fontSize = 11.sp,
                                        color = if (warningsCount > 0) GoldAccent else EmeraldIncome
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyanAccent.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .clickable { onOpenBudgetDialog() }
                                    .testTag("button_manage_budgets_analytics")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Manage",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Budget Mini Bars
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            budgetProgress.forEach { bp ->
                                val barColor = when {
                                    bp.isExceeded -> RoseExpense
                                    bp.isWarning -> GoldAccent
                                    else -> EmeraldIncome
                                }
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = bp.category,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (bp.isExceeded) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = RoseExpense.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "EXCEEDED",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = RoseExpense,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            } else if (bp.isWarning) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = GoldAccent.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "NEAR LIMIT",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GoldAccent,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = "${CryptoManager.formatMaskedAmount(bp.spent, isPrivacyMode)} / ${CryptoManager.formatMaskedAmount(bp.limit, isPrivacyMode)} (${(bp.percent * 100).toInt()}%)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = barColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(5.dp))

                                    LinearProgressIndicator(
                                        progress = { bp.percent.coerceIn(0f, 1f) },
                                        color = barColor,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Detailed Category Breakdown Table
        item {
            Text(
                text = "Detailed Breakdown",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
            )
        }

        val relevantTransactions = transactions.filter { it.type == pieTypeFilter }
        val categoryTotals = relevantTransactions.groupBy { it.category }
            .mapValues { it.value.sumOf { tx -> tx.amount } }
            .toList()
            .sortedByDescending { it.second }
        val totalAmount = relevantTransactions.sumOf { it.amount }

        if (categoryTotals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records for ${pieTypeFilter.name.lowercase()}s in this timeframe",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(categoryTotals.size) { idx ->
                val (category, amount) = categoryTotals[idx]
                val percentage = if (totalAmount > 0.0) (amount / totalAmount).toFloat() else 0f
                val color = CategoryPalette[idx % CategoryPalette.size]
                val matchedBudget = if (pieTypeFilter == TransactionType.EXPENSE) {
                    budgetProgress.find { it.category.equals(category, ignoreCase = true) }
                } else null

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                        .testTag("category_breakdown_$category")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = category,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = CryptoManager.formatMaskedAmount(amount, isPrivacyMode),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${String.format("%.1f", percentage * 100)}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = color
                                )
                            }
                        }

                        if (matchedBudget != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val statusText = when {
                                    matchedBudget.isExceeded -> "Exceeded \$${String.format("%.0f", matchedBudget.limit)} limit"
                                    matchedBudget.isWarning -> "Warning: \$${String.format("%.0f", matchedBudget.remaining)} left"
                                    else -> "\$${String.format("%.0f", matchedBudget.remaining)} left of \$${String.format("%.0f", matchedBudget.limit)}"
                                }
                                val statusColor = when {
                                    matchedBudget.isExceeded -> RoseExpense
                                    matchedBudget.isWarning -> GoldAccent
                                    else -> EmeraldIncome
                                }
                                Text(
                                    text = statusText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = statusColor
                                )
                                Text(
                                    text = "${(matchedBudget.percent * 100).toInt()}% budget used",
                                    fontSize = 11.sp,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { percentage },
                            color = color,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }
    }
}
