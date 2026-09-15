package com.example.data.calendar

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import java.util.TimeZone

class CalendarSyncManager(private val context: Context) {

    fun hasCalendarPermission(): Boolean {
        val read = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
        val write = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
        return read && write
    }

    private fun getPrimaryCalendarId(): Long? {
        if (!hasCalendarPermission()) return null

        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        val uri: Uri = CalendarContract.Calendars.CONTENT_URI
        return try {
            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )
            cursor?.use {
                var firstId: Long? = null
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                    val isPrimaryIndex = it.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY)
                    val isPrimary = if (isPrimaryIndex >= 0) it.getInt(isPrimaryIndex) == 1 else false
                    if (firstId == null) firstId = id
                    if (isPrimary) {
                        return id
                    }
                }
                firstId
            }
        } catch (e: Exception) {
            null
        }
    }

    fun syncTransactionToCalendar(transaction: TransactionEntity): Long? {
        if (!hasCalendarPermission()) return null
        val calendarId = getPrimaryCalendarId() ?: return null

        val prefix = if (transaction.type == TransactionType.INCOME) "💰 FinVault Income: +$" else "💸 FinVault Expense: -$"
        val title = "$prefix${String.format("%.2f", transaction.amount)} (${transaction.category})"
        val description = buildString {
            append("Transaction: ${transaction.title}\n")
            append("Category: ${transaction.category}\n")
            if (transaction.tags.isNotEmpty()) {
                append("Tags: #${transaction.tags.joinToString(" #")}\n")
            }
            append("Managed offline & encrypted via FinVault.")
        }

        val startTime = transaction.timestamp
        val endTime = startTime + (30 * 60 * 1000L) // 30 minutes duration

        return try {
            if (transaction.calendarEventId != null && transaction.calendarEventId > 0) {
                // Update existing event
                val updateUri = ContentUris.withAppendedId(
                    CalendarContract.Events.CONTENT_URI,
                    transaction.calendarEventId
                )
                val values = ContentValues().apply {
                    put(CalendarContract.Events.TITLE, title)
                    put(CalendarContract.Events.DESCRIPTION, description)
                    put(CalendarContract.Events.DTSTART, startTime)
                    put(CalendarContract.Events.DTEND, endTime)
                }
                val rows = context.contentResolver.update(updateUri, values, null, null)
                if (rows > 0) transaction.calendarEventId else insertNewEvent(calendarId, title, description, startTime, endTime)
            } else {
                insertNewEvent(calendarId, title, description, startTime, endTime)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun insertNewEvent(
        calendarId: Long,
        title: String,
        description: String,
        startTime: Long,
        endTime: Long
    ): Long? {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED)
            put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE)
        }

        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri?.lastPathSegment?.toLongOrNull()
    }

    fun removeCalendarEvent(eventId: Long?) {
        if (!hasCalendarPermission() || eventId == null) return
        try {
            val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
            context.contentResolver.delete(deleteUri, null, null)
        } catch (e: Exception) {
            // Ignore error
        }
    }
}
