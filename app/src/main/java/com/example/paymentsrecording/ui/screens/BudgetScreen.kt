package com.example.paymentsrecording.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentsrecording.data.db.entity.Budget
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.BudgetViewModel
import com.example.paymentsrecording.util.ReminderWorker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(factory: AppViewModelFactory, navController: NavHostController) {
    val vm: BudgetViewModel = viewModel(factory = factory)
    val budget by vm.budget.collectAsState()
    val context = LocalContext.current

    var limitText by remember(budget?.monthlyLimit) {
        mutableStateOf(budget?.monthlyLimit?.let { String.format("%.2f", it) } ?: "")
    }
    var overReminder by remember(budget?.reminderEnabled) { mutableStateOf(budget?.reminderEnabled ?: false) }
    var dailyReminder by remember(budget?.dailyReminderEnabled) { mutableStateOf(budget?.dailyReminderEnabled ?: false) }
    var hour by remember(budget?.dailyReminderHour) { mutableIntStateOf(budget?.dailyReminderHour ?: 20) }
    var minute by remember(budget?.dailyReminderMinute) { mutableIntStateOf(budget?.dailyReminderMinute ?: 0) }
    var showTimePicker by remember { mutableStateOf(false) }
    var savedMsg by remember { mutableStateOf(false) }

    // 首次进入确保通知渠道存在
    LaunchedEffect(Unit) { ReminderWorker.ensureChannel(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预算管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("月度预算", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { v -> limitText = v.filter { it.isDigit() || it == '.' }.take(10) },
                        label = { Text("每月预算上限（元）") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Text("¥") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = overReminder, onCheckedChange = { overReminder = it })
                        Spacer(Modifier.width(8.dp))
                        Text("超出预算提醒")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.NotificationsActive, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("每日记账提醒", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("到点提醒你记一笔",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = dailyReminder, onCheckedChange = { dailyReminder = it })
                    }
                    if (dailyReminder) {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text("提醒时间", modifier = Modifier.weight(1f))
                            TextButton(onClick = { showTimePicker = true }) {
                                Text(String.format("%02d:%02d", hour, minute), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    vm.save(
                        Budget(
                            id = 0,
                            monthlyLimit = limit,
                            reminderEnabled = overReminder,
                            dailyReminderEnabled = dailyReminder,
                            dailyReminderHour = hour,
                            dailyReminderMinute = minute
                        ),
                        context
                    )
                    savedMsg = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("保存设置", fontWeight = FontWeight.SemiBold) }

            if (savedMsg) {
                Spacer(Modifier.height(8.dp))
                Text("已保存", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onConfirm = { h, m -> hour = h; minute = m; showTimePicker = false },
            onDismiss = { showTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = true)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("确定") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        text = { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { TimePicker(state = state) } }
    )
}
