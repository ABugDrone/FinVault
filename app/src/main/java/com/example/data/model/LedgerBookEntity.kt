package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LedgerType {
    INDIVIDUAL,
    BUSINESS
}

@Entity(tableName = "ledger_books")
data class LedgerBookEntity(
    @PrimaryKey
    val id: String, // e.g. "ledger_primary", "ledger_spouse", "ledger_kids", "ledger_business_1"
    val name: String, // e.g. "My Personal Vault", "Spouse", "Kids", "Provisions Store"
    val type: LedgerType = LedgerType.INDIVIDUAL,
    val businessSubtype: String = "", // e.g. "Micro Retail & Stock", "Wholesale", "Services", "Family", "Personal"
    val currencySymbol: String = "$",
    val currencyCode: String = "USD",
    val colorHex: Long = 0xFF00E5FF,
    val iconName: String = "person", // "person", "store", "child", "favorite", "business", "briefcase"
    val isDefault: Boolean = false,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class BusinessBookkeepingSummary(
    val totalSalesRevenue: Double = 0.0,
    val totalStockPurchases: Double = 0.0,
    val stockGrossMargin: Double = 0.0,
    val totalDebtCollected: Double = 0.0,
    val totalPersonalWithdrawals: Double = 0.0,
    val totalOperatingExpenses: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBusinessProfit: Double = 0.0,
    val transactionCount: Int = 0
)
