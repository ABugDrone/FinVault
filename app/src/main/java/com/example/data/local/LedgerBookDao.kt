package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LedgerBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerBookDao {
    @Query("SELECT * FROM ledger_books ORDER BY createdAt ASC")
    fun getAllLedgers(): Flow<List<LedgerBookEntity>>

    @Query("SELECT * FROM ledger_books WHERE id = :id")
    suspend fun getLedgerById(id: String): LedgerBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedger(ledger: LedgerBookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgers(ledgers: List<LedgerBookEntity>)

    @Update
    suspend fun updateLedger(ledger: LedgerBookEntity)

    @Delete
    suspend fun deleteLedger(ledger: LedgerBookEntity)

    @Query("DELETE FROM ledger_books WHERE id = :id")
    suspend fun deleteLedgerById(id: String)

    @Query("SELECT COUNT(*) FROM ledger_books")
    suspend fun getLedgerCount(): Int
}
