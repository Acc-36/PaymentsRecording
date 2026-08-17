package com.example.paymentsrecording.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 回顾：自定义时间段 + 名称。 */
@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)
