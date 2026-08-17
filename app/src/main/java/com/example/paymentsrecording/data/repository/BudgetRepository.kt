package com.example.paymentsrecording.data.repository

import com.example.paymentsrecording.data.db.dao.BudgetDao
import com.example.paymentsrecording.data.db.entity.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetDao) {

    fun observe(): Flow<Budget?> = dao.observe()
    suspend fun get(): Budget? = dao.get()
    suspend fun upsert(budget: Budget) = dao.upsert(budget)
}
