package com.example.paymentsrecording.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paymentsrecording.data.db.entity.Transaction
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.HomeViewModel
import com.example.paymentsrecording.ui.components.AddTransactionSheet
import com.example.paymentsrecording.ui.components.EmptyState
import com.example.paymentsrecording.ui.components.TransactionItem
import com.example.paymentsrecording.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(factory: AppViewModelFactory) {
    val vm: HomeViewModel = viewModel(factory = factory)
    val state by vm.uiState.collectAsState()
    val categories by vm.categoriesFlow.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Transaction?>(null) }
    var deleteTarget by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editing = null
                    showSheet = true
                },
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("记账") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item { MonthOverviewCard(state) }
            item { if (state.budget != null) BudgetProgressCard(state) }
            item {
                Text(
                    "最近账单",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
            }
            if (state.recent.isEmpty()) {
                item { EmptyState("还没有账单，点击右下角记一笔吧") }
            } else {
                items(state.recent, key = { it.id }) { tx ->
                    TransactionRow(
                        transaction = tx,
                        onEdit = {
                            editing = tx
                            showSheet = true
                        },
                        onDelete = { deleteTarget = tx }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    if (showSheet) {
        AddTransactionSheet(
            categories = categories,
            editing = editing,
            onDismiss = {
                showSheet = false
                editing = null
            },
            onSave = { tx ->
                if (editing == null) vm.addTransaction(tx) else vm.updateTransaction(tx)
                showSheet = false
                editing = null
            }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("删除账单") },
            text = { Text("确定删除这条 ${target.categoryName} 记录吗？") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteTransaction(target)
                    deleteTarget = null
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("取消") } }
        )
    }
}

@Composable
private fun MonthOverviewCard(state: com.example.paymentsrecording.ui.HomeUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                DateUtil.fmtMonth.format(java.util.Date()),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "本月结余",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                "¥ ${String.format("%.2f", state.balance)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OverviewColumn("收入", state.monthIncome, Color.White)
                OverviewColumn("支出", state.monthExpense, Color.White)
            }
        }
    }
}

@Composable
private fun OverviewColumn(label: String, value: Double, color: Color) {
    Column(modifier = Modifier.weight(1f)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = color.copy(alpha = 0.8f))
        Text(
            "¥ ${String.format("%.2f", value)}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun BudgetProgressCard(state: com.example.paymentsrecording.ui.HomeUiState) {
    val budget = state.budget!!
    val used = state.budgetUsed
    val ratio = if (budget.monthlyLimit > 0) (used / budget.monthlyLimit).coerceIn(0.0, 1.0) else 0.0
    val over = used > budget.monthlyLimit
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("月度预算", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(
                    "¥ ${String.format("%.2f", used)} / ${String.format("%.2f", budget.monthlyLimit)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (over) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { ratio.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (over) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            if (over) {
                Spacer(Modifier.height(4.dp))
                Text("已超出预算 ¥ ${String.format("%.2f", used - budget.monthlyLimit)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransactionItem(transaction, modifier = Modifier.weight(1f))
        IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, contentDescription = "编辑", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error) }
    }
}
