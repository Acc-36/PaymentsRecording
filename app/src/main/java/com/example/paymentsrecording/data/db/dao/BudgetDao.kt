package com.example.paymentsrecording.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.paymentsrecording.data.db.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: Budget)

    @Query("SELECT * FROM budget WHERE id = 0")
    fun observe(): Flow<Budget?>

    @Query("SELECT * FROM budget WHERE id = 0")
    suspend fun get(): Budget?
}
