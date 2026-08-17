package com.example.paymentsrecording.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 预算设置。单条记录代表当前生效的月度预算。 */
@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey val id: Int = 0,
    val monthlyLimit: Double,  // 月度预算上限（元）
    val reminderEnabled: Boolean = false,  // 超支提醒
    val dailyReminderEnabled: Boolean = false,  // 每日记账提醒
    val dailyReminderHour: Int = 20,      // 提醒小时
    val dailyReminderMinute: Int = 0      // 提醒分钟
)
