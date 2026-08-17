package com.example.paymentsrecording.di

import android.content.Context
import androidx.room.Room
import com.example.paymentsrecording.data.db.AppDatabase
import com.example.paymentsrecording.data.db.dao.BudgetDao
import com.example.paymentsrecording.data.db.dao.CategoryDao
import com.example.paymentsrecording.data.db.dao.ReviewDao
import com.example.paymentsrecording.data.db.dao.TransactionDao
import com.example.paymentsrecording.data.db.entity.Category
import com.example.paymentsrecording.data.repository.BudgetRepository
import com.example.paymentsrecording.data.repository.CategoryRepository
import com.example.paymentsrecording.data.repository.ReviewRepository
import com.example.paymentsrecording.data.repository.TransactionRepository

/**
 * 轻量手动 DI 容器（避免引入 Hilt 加重构建）。
 * 单例，App 内持有。
 */
class AppContainer(private val context: Context) {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    val transactionDao: TransactionDao get() = database.transactionDao()
    val categoryDao: CategoryDao get() = database.categoryDao()
    val reviewDao: ReviewDao get() = database.reviewDao()
    val budgetDao: BudgetDao get() = database.budgetDao()

    val transactionRepository by lazy { TransactionRepository(transactionDao) }
    val categoryRepository by lazy { CategoryRepository(categoryDao) }
    val reviewRepository by lazy { ReviewRepository(reviewDao) }
    val budgetRepository by lazy { BudgetRepository(budgetDao) }

    /** 首次启动时填充内置分类。 */
    suspend fun seedIfEmpty() {
        if (categoryRepository.count() > 0) return
        SeedData.defaultCategories.forEach { categoryRepository.insert(it) }
    }

    object SeedData {
        // ARGB 颜色
        private val expenseCategories = listOf(
            Triple("餐饮", "Restaurant", 0xFFE57373),
            Triple("交通", "DirectionsBus", 0xFF64B5F6),
            Triple("购物", "ShoppingBag", 0xFFBA68C8),
            Triple("娱乐", "SportsEsports", 0xFFFF8A65),
            Triple("住房", "Home", 0xFF4DB6AC),
            Triple("医疗", "LocalHospital", 0xFFF06292),
            Triple("教育", "School", 0xFF9575CD),
            Triple("通讯", "PhoneIphone", 0xFF4DD0E1),
            Triple("旅行", "Flight", 0xFFAED581),
            Triple("数码", "Devices", 0xFF7986CB),
            Triple("日用", "ShoppingBasket", 0xFFFFD54F),
            Triple("其他支出", "MoreHoriz", 0xFFB0BEC5)
        )
        private val incomeCategories = listOf(
            Triple("工资", "Work", 0xFF66BB6A),
            Triple("奖金", "CardGiftcard", 0xFF26A69A),
            Triple("理财", "TrendingUp", 0xFF42A5F5),
            Triple("退款", "Replay", 0xFFAB47BC),
            Triple("其他收入", "MoreHoriz", 0xFFB0BEC5)
        )

        val defaultCategories: List<Category> = buildList {
            expenseCategories.forEach { (name, icon, color) ->
                add(Category(name = name, type = 0, icon = icon, color = color, isDefault = true))
            }
            incomeCategories.forEach { (name, icon, color) ->
                add(Category(name = name, type = 1, icon = icon, color = color, isDefault = true))
            }
        }
    }
}
