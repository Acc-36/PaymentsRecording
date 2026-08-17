package com.example.paymentsrecording.data.repository

import com.example.paymentsrecording.data.db.dao.ReviewDao
import com.example.paymentsrecording.data.db.entity.Review
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val dao: ReviewDao) {

    fun observeAll(): Flow<List<Review>> = dao.observeAll()
    suspend fun getById(id: Long): Review? = dao.getById(id)
    suspend fun insert(review: Review): Long = dao.insert(review)
    suspend fun update(review: Review) = dao.update(review)
    suspend fun delete(review: Review) = dao.delete(review)
}
