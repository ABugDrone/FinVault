package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.RoseExpense
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DayData(
    val dayNumber: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val hasIncome: Boolean,
    val hasExpense: Boolean
)

@Composable
fun FinVaultCalendarGrid(
    currentMonthCalendar: Calendar,
    selectedDateMillis: Long,
    transactions: List<TransactionEntity>,
    onDateSelected: (Long) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthTitle = remember(currentMonthCalendar.timeInMillis) {
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        format.format(currentMonthCalendar.time)
    }

    val daysInGrid = remember(currentMonthCalendar.timeInMillis, transactions) {
        computeMonthDays(currentMonthCalendar, transactions)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("calendar_month_grid")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Month Header with Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = monthTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyanAccent.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clickable(onClick = onTodayClick)
                            .testTag("calendar_today_button")
                    ) {
                        Text(
                            text = "Today",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onPreviousMonth,
                        modifier = Modifier.size(36.dp).testTag("cal_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier.size(36.dp).testTag("cal_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day of week headers
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days Grid (6 rows of 7 days)
            val rows = daysInGrid.chunked(7)
            val selCal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }

            rows.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { day ->
                        val dayCal = Calendar.getInstance().apply { timeInMillis = day.dateMillis }
                        val isSelected = selCal.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR) &&
                                selCal.get(Calendar.DAY_OF_YEAR) == dayCal.get(Calendar.DAY_OF_YEAR)

                        DayCell(
                            day = day,
                            isSelected = isSelected,
                            onClick = { onDateSelected(day.dateMillis) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: DayData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> CyanAccent.copy(alpha = 0.25f)
                    day.isToday -> MaterialTheme.colorScheme.surfaceVariant
                    else -> Color.Transparent
                }
            )
            .then(
                if (isSelected) Modifier.border(1.5.dp, CyanAccent, RoundedCornerShape(10.dp))
                else if (day.isToday) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${day.dayNumber}",
                fontSize = 13.sp,
                fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    isSelected -> CyanAccent
                    day.isToday -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Indicators for income and expense dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                if (day.hasIncome) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(EmeraldIncome)
                    )
                }
                if (day.hasIncome && day.hasExpense) {
                    Spacer(modifier = Modifier.width(2.dp))
                }
                if (day.hasExpense) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(RoseExpense)
                    )
                }
            }
        }
    }
}

private fun computeMonthDays(
    currentMonthCal: Calendar,
    transactions: List<TransactionEntity>
): List<DayData> {
    val cal = currentMonthCal.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday
    val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val todayCal = Calendar.getInstance()
    val todayYear = todayCal.get(Calendar.YEAR)
    val todayDayOfYear = todayCal.get(Calendar.DAY_OF_YEAR)

    val days = mutableListOf<DayData>()

    // Leading days from previous month
    val prevMonthCal = cal.clone() as Calendar
    prevMonthCal.add(Calendar.MONTH, -1)
    val prevMonthMaxDays = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingDaysCount = firstDayOfWeek - 1

    for (i in (prevMonthMaxDays - leadingDaysCount + 1)..prevMonthMaxDays) {
        prevMonthCal.set(Calendar.DAY_OF_MONTH, i)
        val time = prevMonthCal.timeInMillis
        days.add(
            DayData(
                dayNumber = i,
                dateMillis = time,
                isCurrentMonth = false,
                isToday = false,
                hasIncome = false,
                hasExpense = false
            )
        )
    }

    // Days in current month
    for (i in 1..maxDaysInMonth) {
        cal.set(Calendar.DAY_OF_MONTH, i)
        val time = cal.timeInMillis
        val isToday = (cal.get(Calendar.YEAR) == todayYear && cal.get(Calendar.DAY_OF_YEAR) == todayDayOfYear)

        // Check transactions on this day
        val hasIncome = transactions.any { tx ->
            tx.type == TransactionType.INCOME && isSameDay(tx.timestamp, time)
        }
        val hasExpense = transactions.any { tx ->
            tx.type == TransactionType.EXPENSE && isSameDay(tx.timestamp, time)
        }

        days.add(
            DayData(
                dayNumber = i,
                dateMillis = time,
                isCurrentMonth = true,
                isToday = isToday,
                hasIncome = hasIncome,
                hasExpense = hasExpense
            )
        )
    }

    // Trailing days from next month to fill 42 cells (6 full weeks) or 35
    val trailingDaysCount = (7 - (days.size % 7)) % 7
    val totalCells = if (days.size + trailingDaysCount < 35) 35 else if (days.size + trailingDaysCount <= 42) 42 else 42
    val extraNeeded = totalCells - days.size

    val nextMonthCal = currentMonthCal.clone() as Calendar
    nextMonthCal.add(Calendar.MONTH, 1)
    for (i in 1..extraNeeded) {
        nextMonthCal.set(Calendar.DAY_OF_MONTH, i)
        val time = nextMonthCal.timeInMillis
        days.add(
            DayData(
                dayNumber = i,
                dateMillis = time,
                isCurrentMonth = false,
                isToday = false,
                hasIncome = false,
                hasExpense = false
            )
        )
    }

    return days
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
