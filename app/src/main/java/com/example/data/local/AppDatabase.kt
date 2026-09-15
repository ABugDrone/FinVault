package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryRule
import com.example.data.model.RecurrenceFrequency
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.security.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        TransactionEntity::class,
        CategoryRule::class,
        RecurringTransactionEntity::class,
        BudgetEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryRuleDao(): CategoryRuleDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finvault_encrypted_ledger.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val ruleDao = database.categoryRuleDao()
            val txDao = database.transactionDao()

            val defaultRules = listOf(
                CategoryRule(keyword = "salary", targetCategory = "Salary", defaultType = TransactionType.INCOME, defaultTags = listOf("Payroll", "Primary")),
                CategoryRule(keyword = "payroll", targetCategory = "Salary", defaultType = TransactionType.INCOME, defaultTags = listOf("Work")),
                CategoryRule(keyword = "freelance", targetCategory = "Freelance", defaultType = TransactionType.INCOME, defaultTags = listOf("SideHustle", "Client")),
                CategoryRule(keyword = "dividend", targetCategory = "Investments", defaultType = TransactionType.INCOME, defaultTags = listOf("Stocks", "Passive")),
                CategoryRule(keyword = "bonus", targetCategory = "Bonus", defaultType = TransactionType.INCOME, defaultTags = listOf("Performance")),
                CategoryRule(keyword = "consulting", targetCategory = "Consulting", defaultType = TransactionType.INCOME, defaultTags = listOf("Contract")),
                CategoryRule(keyword = "coffee", targetCategory = "Food & Dining", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Cafe", "Daily")),
                CategoryRule(keyword = "starbucks", targetCategory = "Food & Dining", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Coffee")),
                CategoryRule(keyword = "lunch", targetCategory = "Food & Dining", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Dining")),
                CategoryRule(keyword = "dinner", targetCategory = "Food & Dining", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Restaurant")),
                CategoryRule(keyword = "groceries", targetCategory = "Groceries", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Essentials", "Market")),
                CategoryRule(keyword = "trader", targetCategory = "Groceries", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Food")),
                CategoryRule(keyword = "whole foods", targetCategory = "Groceries", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Organic")),
                CategoryRule(keyword = "uber", targetCategory = "Transport", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Ride", "City")),
                CategoryRule(keyword = "lyft", targetCategory = "Transport", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Ride")),
                CategoryRule(keyword = "gas", targetCategory = "Transport", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Fuel", "Car")),
                CategoryRule(keyword = "netflix", targetCategory = "Entertainment", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Streaming", "Subscription")),
                CategoryRule(keyword = "spotify", targetCategory = "Entertainment", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Music", "Subscription")),
                CategoryRule(keyword = "gym", targetCategory = "Health & Fitness", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Fitness", "Wellness")),
                CategoryRule(keyword = "pharmacy", targetCategory = "Healthcare", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Medical")),
                CategoryRule(keyword = "rent", targetCategory = "Housing", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Fixed", "Home")),
                CategoryRule(keyword = "electric", targetCategory = "Utilities", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Power", "Bills")),
                CategoryRule(keyword = "wifi", targetCategory = "Utilities", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Internet")),
                CategoryRule(keyword = "amazon", targetCategory = "Shopping", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Online", "Retail")),
                CategoryRule(keyword = "apple", targetCategory = "Technology", defaultType = TransactionType.EXPENSE, defaultTags = listOf("Tech", "Hardware"))
            )
            ruleDao.insertRules(defaultRules)

            // Populate sample data spanning current day, past week, past month
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance()

            val sampleTransactions = listOf(
                TransactionEntity(
                    title = "Senior Tech Lead Salary",
                    amount = 5400.00,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    tags = listOf("Payroll", "Primary"),
                    timestamp = now - 2 * 3600 * 1000L, // 2 hours ago today
                    encryptedNote = CryptoManager.encrypt("Bi-weekly direct deposit credited to secure vault account.")
                ),
                TransactionEntity(
                    title = "Artisan Roast Coffee & Pastry",
                    amount = 8.75,
                    type = TransactionType.EXPENSE,
                    category = "Food & Dining",
                    tags = listOf("Cafe", "Daily"),
                    timestamp = now - 4 * 3600 * 1000L, // 4 hours ago
                    encryptedNote = CryptoManager.encrypt("Morning oat milk flat white with almond croissant.")
                ),
                TransactionEntity(
                    title = "Whole Foods Organic Market",
                    amount = 142.30,
                    type = TransactionType.EXPENSE,
                    category = "Groceries",
                    tags = listOf("Essentials", "Market"),
                    timestamp = now - 1 * 86400 * 1000L, // 1 day ago
                    encryptedNote = CryptoManager.encrypt("Fresh produce, salmon fillets, sourdough bread.")
                ),
                TransactionEntity(
                    title = "Fintech Consulting Project",
                    amount = 1850.00,
                    type = TransactionType.INCOME,
                    category = "Freelance",
                    tags = listOf("SideHustle", "Client"),
                    timestamp = now - 2 * 86400 * 1000L, // 2 days ago
                    encryptedNote = CryptoManager.encrypt("Milestone 2 API architectural review payment.")
                ),
                TransactionEntity(
                    title = "Downtown Uber Premier Ride",
                    amount = 34.50,
                    type = TransactionType.EXPENSE,
                    category = "Transport",
                    tags = listOf("Ride", "City"),
                    timestamp = now - 3 * 86400 * 1000L, // 3 days ago
                    encryptedNote = CryptoManager.encrypt("Trip from client headquarters to train station.")
                ),
                TransactionEntity(
                    title = "High-Yield Dividend Payout",
                    amount = 320.45,
                    type = TransactionType.INCOME,
                    category = "Investments",
                    tags = listOf("Stocks", "Passive"),
                    timestamp = now - 5 * 86400 * 1000L, // 5 days ago
                    encryptedNote = CryptoManager.encrypt("Quarterly reinvested dividend portfolio distribution.")
                ),
                TransactionEntity(
                    title = "Luxury Apartment Lease",
                    amount = 1950.00,
                    type = TransactionType.EXPENSE,
                    category = "Housing",
                    tags = listOf("Fixed", "Home"),
                    timestamp = now - 8 * 86400 * 1000L, // 8 days ago
                    encryptedNote = CryptoManager.encrypt("Monthly residency lease payment.")
                ),
                TransactionEntity(
                    title = "Premium Equinox Gym Membership",
                    amount = 180.00,
                    type = TransactionType.EXPENSE,
                    category = "Health & Fitness",
                    tags = listOf("Fitness", "Wellness"),
                    timestamp = now - 12 * 86400 * 1000L, // 12 days ago
                    encryptedNote = CryptoManager.encrypt("Monthly fitness, sauna, and pool club access.")
                ),
                TransactionEntity(
                    title = "Fiber Optic Gigabit Internet",
                    amount = 75.00,
                    type = TransactionType.EXPENSE,
                    category = "Utilities",
                    tags = listOf("Internet", "Bills"),
                    timestamp = now - 15 * 86400 * 1000L, // 15 days ago
                    encryptedNote = CryptoManager.encrypt("Dedicated 1Gbps secure symmetrical connection.")
                ),
                TransactionEntity(
                    title = "Keychron Mechanical Keyboard",
                    amount = 169.00,
                    type = TransactionType.EXPENSE,
                    category = "Technology",
                    tags = listOf("Tech", "Hardware"),
                    timestamp = now - 20 * 86400 * 1000L, // 20 days ago
                    encryptedNote = CryptoManager.encrypt("Wireless custom Q3 Max mechanical keyboard.")
                )
            )

            for (tx in sampleTransactions) {
                txDao.insertTransaction(tx)
            }

            val budgetDao = database.budgetDao()
            val initialBudgets = listOf(
                BudgetEntity(category = "Food & Dining", monthlyLimit = 350.0),
                BudgetEntity(category = "Groceries", monthlyLimit = 400.0),
                BudgetEntity(category = "Transport", monthlyLimit = 150.0),
                BudgetEntity(category = "Entertainment", monthlyLimit = 100.0),
                BudgetEntity(category = "Housing", monthlyLimit = 2000.0)
            )
            for (b in initialBudgets) {
                budgetDao.insertBudget(b)
            }

            val recurringDao = database.recurringTransactionDao()
            val initialRecurring = listOf(
                RecurringTransactionEntity(
                    title = "Senior Tech Lead Salary",
                    amount = 5400.00,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    tags = listOf("Payroll", "Primary"),
                    frequency = RecurrenceFrequency.MONTHLY,
                    startDate = now - 30 * 86400 * 1000L,
                    lastGeneratedDate = now,
                    hasEndDate = false,
                    encryptedNote = CryptoManager.encrypt("Recurring monthly executive payroll deposit.")
                ),
                RecurringTransactionEntity(
                    title = "Netflix & Streaming Bundle",
                    amount = 22.99,
                    type = TransactionType.EXPENSE,
                    category = "Entertainment",
                    tags = listOf("Streaming", "Subscription"),
                    frequency = RecurrenceFrequency.MONTHLY,
                    startDate = now - 15 * 86400 * 1000L,
                    lastGeneratedDate = now,
                    hasEndDate = false,
                    encryptedNote = CryptoManager.encrypt("Automated monthly 4K streaming plan.")
                )
            )
            for (r in initialRecurring) {
                recurringDao.insertRecurring(r)
            }
        }
    }
}
