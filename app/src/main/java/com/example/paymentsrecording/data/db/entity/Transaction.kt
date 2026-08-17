package com.example.paymentsrecording.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 账单记录。type=0 支出, type=1 收入。 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,        // 金额（元）
    val type: Int,             // 0 支出 1 收入
    val categoryId: Long,      // 分类 id
    val categoryName: String,  // 冗余分类名，便于展示与统计
    val categoryIcon: String,
    val categoryColor: Long,
    val date: Long,            // 时间戳(ms)
    val note: String,          // 备注
    val merchant: String = "", // 商户/来源
    val createdAt: Long = System.currentTimeMillis()
)
