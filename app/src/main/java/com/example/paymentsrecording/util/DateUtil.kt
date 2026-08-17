package com.example.paymentsrecording.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {

    val fmtYMD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fmtYMDHm = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val fmtMD = SimpleDateFormat("MM月dd日", Locale.getDefault())
    val fmtMonth = SimpleDateFormat("yyyy年MM月", Locale.getDefault())

    fun monthStart(ts: Long = System.currentTimeMillis()): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    fun monthEnd(ts: Long = System.currentTimeMillis()): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
        return c.timeInMillis
    }

    fun dayStart(ts: Long): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    fun dayKey(ts: Long): String = fmtYMD.format(Date(ts))

    /** 计算两个时间戳之间相差的整天数（含首尾）。 */
    fun daysBetween(start: Long, end: Long): Int {
        val ms = dayStart(end) - dayStart(start)
        return (ms / (24 * 60 * 60 * 1000)).toInt() + 1
    }
}
