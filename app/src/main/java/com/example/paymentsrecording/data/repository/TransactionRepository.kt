package com.example.paymentsrecording.data.repository

import com.example.paymentsrecording.data.db.dao.TransactionDao
import com.example.paymentsrecording.data.db.entity.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    fun observeAll(): Flow<List<Transaction>> = dao.observeAll()
    fun observeRecent(limit: Int): Flow<List<Transaction>> = dao.observeRecent(limit)
    fun observeMonthRange(start: Long, end: Long): Flow<List<Transaction>> = dao.observeMonthRange(start, end)
    fun observeRange(start: Long, end: Long): Flow<List<Transaction>> = dao.observeByRange(start, end)
    fun observeSumByType(start: Long, end: Long, type: Int): Flow<Double> =
        dao.observeSumByType(start, end, type)

    suspend fun getById(id: Long): Transaction? = dao.getById(id)
    suspend fun getByRange(start: Long, end: Long): List<Transaction> = dao.getByRange(start, end)

    suspend fun insert(t: Transaction): Long = dao.insert(t)
    suspend fun update(t: Transaction) = dao.update(t)
    suspend fun delete(t: Transaction) = dao.delete(t)
}
