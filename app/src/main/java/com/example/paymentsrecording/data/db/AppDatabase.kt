package com.example.paymentsrecording.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paymentsrecording.data.db.dao.BudgetDao
import com.example.paymentsrecording.data.db.dao.CategoryDao
import com.example.paymentsrecording.data.db.dao.ReviewDao
import com.example.paymentsrecording.data.db.dao.TransactionDao
import com.example.paymentsrecording.data.db.entity.Budget
import com.example.paymentsrecording.data.db.entity.Category
import com.example.paymentsrecording.data.db.entity.Review
import com.example.paymentsrecording.data.db.entity.Transaction

@Database(
    entities = [Transaction::class, Category::class, Review::class, Budget::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reviewDao(): ReviewDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        const val DB_NAME = "payments.db"
    }
}
