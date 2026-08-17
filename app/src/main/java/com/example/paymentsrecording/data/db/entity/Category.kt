package com.example.paymentsrecording.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 账单分类。type=0 支出, type=1 收入。
 * isDefault=true 的为系统内置分类，不可删除。
 */
@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "type"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: Int,          // 0 支出 1 收入
    val icon: String,       // 图标标识（Material icon name）
    val color: Long,        // ARGB 颜色
    val isDefault: Boolean = false
)
