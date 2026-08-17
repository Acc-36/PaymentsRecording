package com.example.paymentsrecording.data.repository

import com.example.paymentsrecording.data.db.dao.CategoryDao
import com.example.paymentsrecording.data.db.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {

    fun observeAll(): Flow<List<Category>> = dao.observeAll()
    fun observeByType(type: Int): Flow<List<Category>> = dao.observeByType(type)
    suspend fun getByType(type: Int): List<Category> = dao.getByType(type)
    suspend fun count(): Int = dao.count()

    suspend fun insert(category: Category): Long = dao.insert(category)
    suspend fun update(category: Category) = dao.update(category)
    suspend fun delete(category: Category) = dao.delete(category)
}
