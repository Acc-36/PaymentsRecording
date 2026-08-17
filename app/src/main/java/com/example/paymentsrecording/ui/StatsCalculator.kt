package com.example.paymentsrecording.ui

import com.example.paymentsrecording.data.db.entity.Transaction
import com.example.paymentsrecording.util.DateUtil

/** 回顾统计结果 */
data class ReviewStats(
    val totalExpense: Double,
    val totalIncome: Double,
    val expenseByCategory: List<CategoryAmount>,
    val incomeByDay: List<DayAmount>,
    val expenseByDay: List<DayAmount>,
    val dailyBars: List<DailyBar>,
    val transactionCount: Int
)

data class CategoryAmount(val name: String, val amount: Double, val color: Long)
data class DayAmount(val dayLabel: String, val amount: Double)
data class DailyBar(val label: String, val expense: Double, val income: Double)

object StatsCalculator {

    fun compute(transactions: List<Transaction>, start: Long, end: Long): ReviewStats {
        val inRange = transactions.filter { it.date in start..end }

        val totalExpense = inRange.filter { it.type == 0 }.sumOf { it.amount }
        val totalIncome = inRange.filter { it.type == 1 }.sumOf { it.amount }

        // 分类支出
        val expenseByCat = inRange.filter { it.type == 0 }
            .groupBy { it.categoryName }
            .map { (name, list) ->
                CategoryAmount(name, list.sumOf { it.amount }, list.first().categoryColor)
            }
            .sortedByDescending { it.amount }

        // 按日
        val days = DateUtil.daysBetween(start, end).coerceIn(1, 400)
        val expenseByDay = mutableListOf<DayAmount>()
        val incomeByDay = mutableListOf<DayAmount>()
        val dailyBars = mutableListOf<DailyBar>()

        val cal = java.util.Calendar.getInstance().apply {
            timeInMillis = DateUtil.dayStart(start)
        }
        for (i in 0 until days) {
            val dayStart = cal.timeInMillis
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            val dayEnd = cal.timeInMillis - 1
            val dayTx = inRange.filter { it.date in dayStart..dayEnd }
            val exp = dayTx.filter { it.type == 0 }.sumOf { it.amount }
            val inc = dayTx.filter { it.type == 1 }.sumOf { it.amount }
            val label = DateUtil.fmtMD.format(java.util.Date(dayStart))
            expenseByDay.add(DayAmount(label, exp))
            incomeByDay.add(DayAmount(label, inc))
            dailyBars.add(DailyBar(label, exp, inc))
        }

        return ReviewStats(
            totalExpense = totalExpense,
            totalIncome = totalIncome,
            expenseByCategory = expenseByCat,
            incomeByDay = incomeByDay,
            expenseByDay = expenseByDay,
            dailyBars = dailyBars,
            transactionCount = inRange.size
        )
    }
}
