package com.example.paymentsrecording.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentsrecording.data.db.entity.Budget
import com.example.paymentsrecording.data.repository.BudgetRepository
import com.example.paymentsrecording.data.repository.TransactionRepository
import com.example.paymentsrecording.util.CsvExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

data class ExportResult(val success: Boolean, val message: String)

class MineViewModel(
    private val txRepo: TransactionRepository,
    private val budgetRepo: BudgetRepository
) : ViewModel() {

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult = _exportResult.asStateFlow()

    fun clearExportResult() { _exportResult.value = null }

    /** 导出全部账单为 CSV 到指定 OutputStream。 */
    fun exportCsv(outputStream: OutputStream) = viewModelScope.launch {
        try {
            val all = withContext(Dispatchers.IO) {
                // 取全部：用 observeAll 的快照不便，直接走 getByRange 全量
                txRepo.getByRange(0, System.currentTimeMillis() + 1)
            }
            val csv = CsvExporter.export(all)
            withContext(Dispatchers.IO) {
                outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(csv) }
            }
            _exportResult.value = ExportResult(true, "已导出 ${all.size} 条记录")
        } catch (e: Exception) {
            _exportResult.value = ExportResult(false, "导出失败：${e.message}")
        }
    }
}
