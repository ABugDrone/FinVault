package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.RecurrenceFrequency
import com.example.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTagsList(tags: List<String>?): String {
        return tags?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toTagsList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return try {
            TransactionType.valueOf(value)
        } catch (e: Exception) {
            TransactionType.EXPENSE
        }
    }

    @TypeConverter
    fun fromRecurrenceFrequency(freq: RecurrenceFrequency): String {
        return freq.name
    }

    @TypeConverter
    fun toRecurrenceFrequency(value: String): RecurrenceFrequency {
        return try {
            RecurrenceFrequency.valueOf(value)
        } catch (e: Exception) {
            RecurrenceFrequency.MONTHLY
        }
    }
}
