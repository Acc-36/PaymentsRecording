package com.example.paymentsrecording.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paymentsrecording.data.db.entity.Category
import com.example.paymentsrecording.data.db.entity.Transaction
import com.example.paymentsrecording.util.CategoryClassifier
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 底部弹出的记账 BottomSheet。
 * 输入金额 -> 选择收支类型 -> 分类(带自动建议) -> 日期 -> 备注。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    editing: Transaction? = null
) {
    val expenseCats = categories.filter { it.type == 0 }
    val incomeCats = categories.filter { it.type == 1 }

    var type by remember { mutableStateOf(editing?.type ?: 0) }
    var amountText by remember { mutableStateOf(editing?.let { String.format("%.2f", it.amount) } ?: "") }
    var merchant by remember { mutableStateOf(editing?.merchant ?: "") }
    var note by remember { mutableStateOf(editing?.note ?: "") }
    var selectedCategory by remember {
        mutableStateOf(
            editing?.let { cat -> categories.firstOrNull { it.id == cat.categoryId } }
                ?: expenseCats.firstOrNull()
        )
    }
    var date by remember { mutableStateOf(editing?.date ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var suggestion by remember { mutableStateOf<Category?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun reclassify() {
        if (type != 0) {
            suggestion = null
            return
        }
        val combined = "$merchant $note"
        val result = CategoryClassifier.classify(combined, expenseCats)
        suggestion = result
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("记一笔", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "关闭")
                }
            }

            // 金额输入
            Text("金额", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { v -> amountText = v.filter { it.isDigit() || it == '.' }.take(10) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                leadingIcon = { Text("¥", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
            )

            Spacer(Modifier.height(16.dp))

            // 收支切换
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = type == 0,
                    onClick = {
                        type = 0
                        selectedCategory = expenseCats.firstOrNull()
                        reclassify()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("支出") }
                SegmentedButton(
                    selected = type == 1,
                    onClick = {
                        type = 1
                        selectedCategory = incomeCats.firstOrNull()
                        reclassify()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("收入") }
            }

            // 自动分类建议
            if (suggestion != null && type == 0) {
                Spacer(Modifier.height(8.dp))
                SuggestionChip(
                    onClick = {
                        selectedCategory = suggestion
                        suggestion = null
                    },
                    label = { Text("建议分类：${suggestion!!.name}") },
                    leadingIcon = { Icon(Icons.Outlined.Bolt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("分类", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            val list = if (type == 0) expenseCats else incomeCats
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list, key = { it.id }) { cat ->
                    CategoryChip(
                        category = cat,
                        selected = selectedCategory?.id == cat.id,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 商户
            OutlinedTextField(
                value = merchant,
                onValueChange = {
                    merchant = it
                    reclassify()
                },
                label = { Text("商家/来源") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            // 日期
            OutlinedTextField(
                value = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(date)),
                onValueChange = {},
                label = { Text("日期") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(12.dp))

            // 备注
            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                    reclassify()
                },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: return@Button
                    val cat = selectedCategory ?: return@Button
                    onSave(
                        Transaction(
                            id = editing?.id ?: 0,
                            amount = amt,
                            type = type,
                            categoryId = cat.id,
                            categoryName = cat.name,
                            categoryIcon = cat.icon,
                            categoryColor = cat.color,
                            date = date,
                            note = note.trim(),
                            merchant = merchant.trim(),
                            createdAt = editing?.createdAt ?: System.currentTimeMillis()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = amountText.toDoubleOrNull() != null && selectedCategory != null
            ) {
                Text("保存", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showDatePicker) {
        val cal = Calendar.getInstance().apply { timeInMillis = date }
        val dp = DatePickerDialog(
            initialMillis = date
        )
        dp.show(
            onConfirm = {
                date = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun CategoryChip(category: Category, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (selected) category.color.toColor()
                    else category.color.toColor().copy(alpha = 0.12f)
                )
                .border(
                    width = if (selected) 2.dp else 0.dp,
                    color = category.color.toColor(),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = CategoryIcons.fromName(category.icon),
                contentDescription = category.name,
                tint = if (selected) Color.White else category.color.toColor(),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}
