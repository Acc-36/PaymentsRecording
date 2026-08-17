package com.example.paymentsrecording.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.paymentsrecording.data.db.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories ORDER BY type ASC, id ASC")
    fun observeAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY id ASC")
    fun observeByType(type: Int): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type")
    suspend fun getByType(type: Int): List<Category>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
