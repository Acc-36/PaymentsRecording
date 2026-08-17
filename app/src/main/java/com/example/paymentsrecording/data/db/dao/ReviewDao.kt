package com.example.paymentsrecording.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.paymentsrecording.data.db.entity.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review): Long

    @Update
    suspend fun update(review: Review)

    @Delete
    suspend fun delete(review: Review)

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun getById(id: Long): Review?
}
