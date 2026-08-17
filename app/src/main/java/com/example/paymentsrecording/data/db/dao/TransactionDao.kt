package com.example.paymentsrecording.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.paymentsrecording.data.db.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun observeAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeByRange(start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    suspend fun getByRange(start: Long, end: Long): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeMonthRange(start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = :type AND date BETWEEN :start AND :end")
    fun observeSumByType(start: Long, end: Long, type: Int): Flow<Double>
}
