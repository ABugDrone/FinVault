package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.material.icons.filled.Repeat
import com.example.data.model.RecurrenceFrequency
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.security.CryptoManager
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseExpense
import kotlinx.coroutines.launch

val PredefinedExpenseCategories = listOf(
    "Food & Dining", "Groceries", "Transport", "Shopping",
    "Entertainment", "Housing", "Utilities", "Health & Fitness", "General"
)

val PredefinedIncomeCategories = listOf(
    "Salary", "Freelance", "Investments", "Bonus", "Consulting", "Dividends", "Other Income"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionSheet(
    onDismiss: () -> Unit,
    onSaveTransaction: (TransactionEntity, Boolean) -> Unit,
    onSaveRecurring: (RecurringTransactionEntity, Boolean) -> Unit,
    onInferCategory: suspend (String) -> Triple<String?, TransactionType?, List<String>>,
    hasCalendarPermission: Boolean,
    onRequestCalendarPermission: () -> Unit,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    val tags = remember { mutableStateListOf<String>() }
    var newTagInput by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var syncCalendar by remember { mutableStateOf(hasCalendarPermission) }
    var autoDetectedLabel by remember { mutableStateOf<String?>(null) }

    // Recurring Transaction Options
    var isRecurring by remember { mutableStateOf(false) }
    var recurrenceFrequency by remember { mutableStateOf(RecurrenceFrequency.MONTHLY) }
    var hasEndDate by remember { mutableStateOf(false) }
    var durationMonths by remember { mutableStateOf(12) } // default 1 year if end date enabled

    // Run smart auto-categorization when title changes
    LaunchedEffect(title) {
        if (title.length >= 3) {
            val inference = onInferCategory(title)
            val inferredCategory = inference.first
            val inferredType = inference.second
            val inferredTags = inference.third

            if (inferredCategory != null) {
                selectedCategory = inferredCategory
                autoDetectedLabel = "Auto-Categorized: $inferredCategory"
            }
            if (inferredType != null) {
                type = inferredType
            }
            if (inferredTags.isNotEmpty()) {
                inferredTags.forEach { tag ->
                    if (!tags.contains(tag)) {
                        tags.add(tag)
                    }
                }
            }
        } else {
            autoDetectedLabel = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("add_transaction_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.testTag("close_add_sheet")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Income / Expense Selector Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                val isExpense = type == TransactionType.EXPENSE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isExpense) RoseExpense else Color.Transparent)
                        .clickable {
                            type = TransactionType.EXPENSE
                            if (!PredefinedExpenseCategories.contains(selectedCategory)) {
                                selectedCategory = "Food & Dining"
                            }
                        }
                        .padding(vertical = 10.dp)
                        .testTag("select_expense_type"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Expense",
                        fontWeight = FontWeight.Bold,
                        color = if (isExpense) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val isIncome = type == TransactionType.INCOME
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isIncome) EmeraldIncome else Color.Transparent)
                        .clickable {
                            type = TransactionType.INCOME
                            if (!PredefinedIncomeCategories.contains(selectedCategory)) {
                                selectedCategory = "Salary"
                            }
                        }
                        .padding(vertical = 10.dp)
                        .testTag("select_income_type"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Income / Earning",
                        fontWeight = FontWeight.Bold,
                        color = if (isIncome) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title or Merchant (e.g. Starbucks, Salary, Gas)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_tx_title"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Auto-categorized badge banner
            AnimatedVisibility(visible = autoDetectedLabel != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanAccent.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = autoDetectedLabel ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount input
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                        amountText = input
                    }
                },
                label = { Text("Amount ($currencySymbol)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_tx_amount"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (type == TransactionType.INCOME) EmeraldIncome else RoseExpense,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Chips Selection
            Text(
                text = "Category",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            val currentCategoryList = if (type == TransactionType.EXPENSE) PredefinedExpenseCategories else PredefinedIncomeCategories
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                currentCategoryList.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { selectedCategory = cat }
                            .testTag("cat_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Tags Section
            Text(
                text = "Custom Tags",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 12.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Medium
                            )
                            IconButton(
                                onClick = { tags.remove(tag) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove tag",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Add new custom tag input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTagInput,
                    onValueChange = { newTagInput = it },
                    placeholder = { Text("Add custom tag (e.g. TaxDeductible, Trip)") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_new_tag"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val clean = newTagInput.trim().replace("#", "")
                        if (clean.isNotEmpty() && !tags.contains(clean)) {
                            tags.add(clean)
                            newTagInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("button_add_tag")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add tag", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Encrypted Note field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = EmeraldIncome)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Private Note (AES-256 GCM Encrypted)")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_tx_note"),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldIncome,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Recurring Transaction Section
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isRecurring,
                            onCheckedChange = { isRecurring = it },
                            colors = CheckboxDefaults.colors(checkedColor = CyanAccent),
                            modifier = Modifier.testTag("checkbox_recurring")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Repeat / Recurring Transaction",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Automatically logs transaction and syncs on calendar",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    AnimatedVisibility(visible = isRecurring) {
                        Column(modifier = Modifier.padding(top = 10.dp, start = 8.dp, end = 8.dp)) {
                            // Frequency Chips
                            Text(
                                text = "Recurrence Interval:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                RecurrenceFrequency.entries.forEach { freq ->
                                    val isSelected = freq == recurrenceFrequency
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { recurrenceFrequency = freq }
                                            .testTag("frequency_${freq.name}")
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = freq.name.lowercase().replaceFirstChar { it.uppercase() },
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // End Date or Indefinite
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (!hasEndDate) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (!hasEndDate) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { hasEndDate = false }
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "Indefinite Recurrence",
                                        fontSize = 11.sp,
                                        fontWeight = if (!hasEndDate) FontWeight.Bold else FontWeight.Normal,
                                        color = if (!hasEndDate) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hasEndDate) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (hasEndDate) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { hasEndDate = true }
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "Specify End Date",
                                        fontSize = 11.sp,
                                        fontWeight = if (hasEndDate) FontWeight.Bold else FontWeight.Normal,
                                        color = if (hasEndDate) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }

                            if (hasEndDate) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "End After Duration:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(3, 6, 12, 24).forEach { months ->
                                        val isSel = durationMonths == months
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSel) CyanAccent else MaterialTheme.colorScheme.surface,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { durationMonths = months }
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = if (months < 12) "$months mo" else "${months / 12} yr",
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
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

            Spacer(modifier = Modifier.height(14.dp))

            // Default Calendar Sync Checkbox
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = syncCalendar,
                        onCheckedChange = { checked ->
                            if (checked && !hasCalendarPermission) {
                                onRequestCalendarPermission()
                            }
                            syncCalendar = checked
                        },
                        colors = CheckboxDefaults.colors(checkedColor = CyanAccent),
                        modifier = Modifier.testTag("checkbox_calendar_sync")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sync with Default Calendar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (hasCalendarPermission) "Will post event to device calendar" else "Tap to grant calendar permissions",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Record Button
            val amountValue = amountText.toDoubleOrNull() ?: 0.0
            val canSave = title.isNotBlank() && amountValue > 0.0

            Button(
                onClick = {
                    if (canSave) {
                        val encryptedNoteString = if (note.isNotBlank()) {
                            CryptoManager.encrypt(note.trim())
                        } else {
                            ""
                        }

                        if (isRecurring) {
                            val endMillis = if (hasEndDate) {
                                val cal = java.util.Calendar.getInstance()
                                cal.add(java.util.Calendar.MONTH, durationMonths)
                                cal.timeInMillis
                            } else null

                            val recurring = RecurringTransactionEntity(
                                title = title.trim(),
                                amount = amountValue,
                                type = type,
                                category = selectedCategory,
                                tags = tags.toList(),
                                frequency = recurrenceFrequency,
                                startDate = System.currentTimeMillis(),
                                lastGeneratedDate = System.currentTimeMillis(),
                                hasEndDate = hasEndDate,
                                endDateMillis = endMillis,
                                encryptedNote = encryptedNoteString,
                                syncWithCalendar = syncCalendar,
                                isActive = true
                            )
                            onSaveRecurring(recurring, syncCalendar)
                        } else {
                            val newTx = TransactionEntity(
                                title = title.trim(),
                                amount = amountValue,
                                type = type,
                                category = selectedCategory,
                                tags = tags.toList(),
                                timestamp = System.currentTimeMillis(),
                                encryptedNote = encryptedNoteString
                            )
                            onSaveTransaction(newTx, syncCalendar)
                        }

                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("button_save_transaction"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == TransactionType.INCOME) EmeraldIncome else RoseExpense,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isRecurring) "Save Recurring Schedule" else "Securely Save Record",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
