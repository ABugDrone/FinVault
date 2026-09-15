package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.security.CryptoManager
import com.example.ui.components.FinVaultCalendarGrid
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseExpense
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    currentMonthCalendar: Calendar,
    selectedDateMillis: Long,
    allTransactions: List<TransactionEntity>,
    dayTransactions: List<TransactionEntity>,
    hasCalendarPermission: Boolean,
    isPrivacyMode: Boolean,
    onDateSelected: (Long) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit,
    onSyncAllClick: () -> Unit,
    onRequestCalendarPermission: () -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onTogglePrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDateTitle = remember(selectedDateMillis) {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        sdf.format(Date(selectedDateMillis))
    }

    val dayIncome = remember(dayTransactions) {
        dayTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val dayExpense = remember(dayTransactions) {
        dayTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val dayNet = dayIncome - dayExpense

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header Bar
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
                        text = "Calendar Sync",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Daily, Weekly, Monthly Records",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onTogglePrivacy,
                    modifier = Modifier.size(40.dp).testTag("cal_toggle_privacy")
                ) {
                    Icon(
                        imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Privacy",
                        tint = if (isPrivacyMode) GoldAccent else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Calendar Sync Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasCalendarPermission) EmeraldIncome.copy(alpha = 0.4f) else CyanAccent.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .testTag("calendar_sync_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasCalendarPermission) EmeraldIncome.copy(alpha = 0.15f)
                                    else CyanAccent.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasCalendarPermission) Icons.Default.CheckCircle else Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = if (hasCalendarPermission) EmeraldIncome else CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (hasCalendarPermission) "Default Calendar Linked" else "Sync to Device Calendar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (hasCalendarPermission) "Automatic 2-way offline event logging" else "Grant access to post entries to default calendar",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (hasCalendarPermission) {
                        Button(
                            onClick = onSyncAllClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("button_sync_now")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync All", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onRequestCalendarPermission,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("button_grant_calendar")
                        ) {
                            Text("Enable", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Month Grid Component
        item {
            FinVaultCalendarGrid(
                currentMonthCalendar = currentMonthCalendar,
                selectedDateMillis = selectedDateMillis,
                transactions = allTransactions,
                onDateSelected = onDateSelected,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onTodayClick = onTodayClick,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
        }

        // Selected Day Summary Card
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .testTag("selected_day_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedDateTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${dayTransactions.size} entries",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Income
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Earned", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = CryptoManager.formatMaskedAmount(dayIncome, isPrivacyMode),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldIncome
                            )
                        }
                        // Expense
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Spent", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = CryptoManager.formatMaskedAmount(dayExpense, isPrivacyMode),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoseExpense
                            )
                        }
                        // Net
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Day Net", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = CryptoManager.formatMaskedAmount(dayNet, isPrivacyMode),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dayNet >= 0) EmeraldIncome else RoseExpense
                            )
                        }
                    }
                }
            }
        }

        // Day Transactions List Header
        item {
            Text(
                text = "Day Activity Logs",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
        }

        if (dayTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No financial activity logged for this day",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(dayTransactions, key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    isPrivacyMode = isPrivacyMode,
                    onDeleteClick = { onDeleteTransaction(tx) },
                    onClick = { },
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
                )
            }
        }
    }
}
