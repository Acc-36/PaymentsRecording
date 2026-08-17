package com.example.paymentsrecording.util

import com.example.paymentsrecording.data.db.entity.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    /** 将账单列表导出为 CSV 字符串。 */
    fun export(transactions: List<Transaction>): String {
        val sb = StringBuilder()
        sb.append("日期,类型,分类,金额,商户,备注\n")
        for (t in transactions) {
            val type = if (t.type == 0) "支出" else "收入"
            sb.append(df.format(Date(t.date))).append(',')
            sb.append(type).append(',')
            sb.append(escape(t.categoryName)).append(',')
            sb.append(String.format(Locale.getDefault(), "%.2f", t.amount)).append(',')
            sb.append(escape(t.merchant)).append(',')
            sb.append(escape(t.note)).append('\n')
        }
        return sb.toString()
    }

    private fun escape(s: String): String {
        val v = s.replace("\"", "\"\"")
        return if (v.contains(',') || v.contains('"') || v.contains('\n')) "\"$v\"" else v
    }
}
