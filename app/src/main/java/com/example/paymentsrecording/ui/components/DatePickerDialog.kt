package com.example.paymentsrecording.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

/**
 * Material3 DatePicker 弹窗包装。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogHolder(
    initialMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.selectedDateMillis ?: System.currentTimeMillis()) }) {
                Text("确定")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        text = { DatePicker(state = state) }
    )
}

/** 供 AddTransactionSheet 使用的轻量调用句柄 */
class DatePickerDialog(private val initialMillis: Long) {
    @Composable
    fun show(onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
        DatePickerDialogHolder(initialMillis, onConfirm, onDismiss)
    }
}
