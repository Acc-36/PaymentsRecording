package com.example.paymentsrecording.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentsrecording.data.db.entity.Review
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.ReviewViewModel
import com.example.paymentsrecording.ui.navigation.Screen
import com.example.paymentsrecording.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(factory: AppViewModelFactory, navController: NavHostController) {
    val vm: ReviewViewModel = viewModel(factory = factory)
    val reviews by vm.reviews.collectAsState()

    var showCreate by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Review?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("新建回顾") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("回顾", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("选择时间段，生成专属账单分析报告",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (reviews.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Assessment, contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(12.dp))
                        Text("还没有回顾，点击右下角创建", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(reviews, key = { it.id }) { review ->
                    ReviewCard(
                        review = review,
                        onClick = {
                            navController.navigate(Screen.ReviewDetail.create(review.id))
                        },
                        onDelete = { deleteTarget = review }
                    )
                }
            }
        }
    }

    if (showCreate) {
        CreateReviewSheet(
            onDismiss = { showCreate = false },
            onCreate = { name, start, end ->
                vm.createReview(name, start, end)
                showCreate = false
            }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("删除回顾") },
            text = { Text("确定删除「${target.name}」吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteReview(target)
                    deleteTarget = null
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("取消") } }
        )
    }
}

@Composable
private fun ReviewCard(review: Review, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Assessment, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(review.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${DateUtil.fmtYMD.format(java.util.Date(review.startDate))}  ~  ${DateUtil.fmtYMD.format(java.util.Date(review.endDate))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateReviewSheet(onDismiss: () -> Unit, onCreate: (String, Long, Long) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var start by remember { mutableStateOf(DateUtil.monthStart()) }
    var end by remember { mutableStateOf(DateUtil.monthEnd()) }
    var showStart by remember { mutableStateOf(false) }
    var showEnd by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(20.dp).padding(bottom = 24.dp)) {
            Text("新建回顾", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("回顾名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = DateUtil.fmtYMD.format(java.util.Date(start)),
                onValueChange = {},
                label = { Text("开始日期") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = outlinedFieldColors()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = DateUtil.fmtYMD.format(java.util.Date(end)),
                onValueChange = {},
                label = { Text("结束日期") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = outlinedFieldColors()
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = { showStart = true }, label = { Text("选开始") })
                AssistChip(onClick = { showEnd = true }, label = { Text("选结束") })
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (name.isBlank()) name = "${DateUtil.fmtYMD.format(java.util.Date(start))} 回顾"
                    if (start <= end) onCreate(name.trim(), start, end)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = start <= end
            ) { Text("创建", fontWeight = FontWeight.SemiBold) }
        }
    }

    if (showStart) {
        com.example.paymentsrecording.ui.components.DatePickerDialogHolder(
            initialMillis = start,
            onConfirm = { start = it; showStart = false },
            onDismiss = { showStart = false }
        )
    }
    if (showEnd) {
        com.example.paymentsrecording.ui.components.DatePickerDialogHolder(
            initialMillis = end,
            onConfirm = { end = it; showEnd = false },
            onDismiss = { showEnd = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledBorderColor = MaterialTheme.colorScheme.outline,
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
)
