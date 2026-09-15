package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetProgress
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseExpense
import java.util.Locale

@Composable
fun BudgetManageDialog(
    budgets: List<BudgetEntity>,
    budgetProgress: List<CategoryBudgetProgress>,
    onDismiss: () -> Unit,
    onSaveBudget: (category: String, limit: Double, notifyThresholdPercent: Double) -> Unit,
    onDeleteBudget: (BudgetEntity) -> Unit,
    currencySymbol: String = "$",
    hasNotificationPermission: Boolean = true,
    onRequestNotificationPermission: () -> Unit = {},
    onSendTestAlert: (category: String, isExceeded: Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showAddSection by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Food & Dining") }
    var limitInput by remember { mutableStateOf("") }
    var selectedThreshold by remember { mutableDoubleStateOf(85.0) }

    val predefinedCategories = listOf(
        "Food & Dining", "Groceries", "Transport", "Shopping",
        "Entertainment", "Housing", "Utilities", "Health & Fitness", "General"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("dialog_manage_budgets")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Monthly Budgets",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Category Limits & Smart Notifications",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notification permission status banner
                if (!hasNotificationPermission) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = RoseExpense.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoseExpense.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("banner_notification_permission_required")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = RoseExpense,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Alerts Disabled (Android 13+)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoseExpense
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Grant POST_NOTIFICATIONS permission to receive heads-up system alerts when you reach warning thresholds or exceed category caps.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onRequestNotificationPermission,
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .testTag("btn_grant_notifications_budget_dialog")
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enable Notification Alerts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldAccent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "High-priority system alerts will pop up when spending crosses your threshold or breaches category caps.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            TextButton(
                                onClick = { onSendTestAlert(selectedCategory, false) },
                                modifier = Modifier.height(28.dp).testTag("btn_test_notification_budget_dialog")
                            ) {
                                Text("Test", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Add / Edit Budget Toggle button
                Button(
                    onClick = { showAddSection = !showAddSection },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showAddSection) MaterialTheme.colorScheme.surfaceVariant else CyanAccent,
                        contentColor = if (showAddSection) MaterialTheme.colorScheme.onSurface else Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_toggle_add_budget")
                ) {
                    Icon(
                        imageVector = if (showAddSection) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showAddSection) "Cancel New Target" else "+ Set New Category Target",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Add Budget Form
                AnimatedVisibility(visible = showAddSection) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Target Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Category Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            predefinedCategories.take(4).forEach { cat ->
                                val isSel = cat == selectedCategory
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) CyanAccent else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedCategory = cat }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                        Text(
                                            text = cat.split(" ").first(),
                                            fontSize = 10.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            predefinedCategories.drop(4).take(4).forEach { cat ->
                                val isSel = cat == selectedCategory
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) CyanAccent else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedCategory = cat }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                        Text(
                                            text = cat.split(" ").first(),
                                            fontSize = 10.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Monthly limit input
                        OutlinedTextField(
                            value = limitInput,
                            onValueChange = { limitInput = it },
                            label = { Text("Monthly Limit ($currencySymbol)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_budget_limit"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick presets: 150, 300, 500, 1000
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(150, 300, 500, 1000).forEach { amt ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { limitInput = amt.toString() }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(
                                            text = "$currencySymbol$amt",
                                            fontSize = 11.sp,
                                            color = CyanAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Warning Threshold Selector
                        Text(
                            text = "Notification Warning Threshold",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(70.0, 80.0, 85.0, 90.0).forEach { th ->
                                val isSel = selectedThreshold == th
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) GoldAccent else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedThreshold = th }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 5.dp)) {
                                        Text(
                                            text = "${th.toInt()}%",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val limitVal = limitInput.toDoubleOrNull() ?: 0.0
                        Button(
                            onClick = {
                                if (limitVal > 0) {
                                    onSaveBudget(selectedCategory, limitVal, selectedThreshold)
                                    limitInput = ""
                                    showAddSection = false
                                }
                            },
                            enabled = limitVal > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("button_save_budget"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
                        ) {
                            Text("Save Target for $selectedCategory", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of active budgets
                Text(
                    text = "Active Limits (${budgets.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (budgets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No category budgets set yet. Tap above to add one!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(budgets, key = { it.id }) { b ->
                            val progress = budgetProgress.find { it.category.equals(b.category, ignoreCase = true) }
                            val spent = progress?.spent ?: 0.0
                            val ratio = if (b.monthlyLimit > 0) (spent / b.monthlyLimit).toFloat().coerceIn(0f, 1f) else 0f
                            val isExceeded = spent >= b.monthlyLimit
                            val isWarning = spent >= (b.monthlyLimit * (b.notifyThresholdPercent / 100.0)) && !isExceeded

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("budget_item_${b.category}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = b.category,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = GoldAccent.copy(alpha = 0.15f)
                                                ) {
                                                    Text(
                                                        text = "Alert @ ${b.notifyThresholdPercent.toInt()}%",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GoldAccent,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            val statusText = when {
                                                isExceeded -> "Exceeded by $currencySymbol${String.format(Locale.US, "%.2f", spent - b.monthlyLimit)}"
                                                isWarning -> "Warning: $currencySymbol${String.format(Locale.US, "%.2f", b.monthlyLimit - spent)} remaining"
                                                else -> "$currencySymbol${String.format(Locale.US, "%.2f", b.monthlyLimit - spent)} remaining"
                                            }
                                            val statusColor = when {
                                                isExceeded -> RoseExpense
                                                isWarning -> GoldAccent
                                                else -> EmeraldIncome
                                            }
                                            Text(
                                                text = statusText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = statusColor
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "$currencySymbol${String.format(Locale.US, "%.0f", spent)} / $currencySymbol${String.format(Locale.US, "%.0f", b.monthlyLimit)}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            IconButton(
                                                onClick = { onSendTestAlert(b.category, isExceeded) },
                                                modifier = Modifier.size(32.dp).testTag("btn_test_alert_${b.category}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Notifications,
                                                    contentDescription = "Test Notification Alert",
                                                    tint = GoldAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { onDeleteBudget(b) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Budget",
                                                    tint = RoseExpense.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Progress bar
                                    val barColor = when {
                                        isExceeded -> RoseExpense
                                        isWarning -> GoldAccent
                                        else -> EmeraldIncome
                                    }
                                    LinearProgressIndicator(
                                        progress = { ratio },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = barColor,
                                        trackColor = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

