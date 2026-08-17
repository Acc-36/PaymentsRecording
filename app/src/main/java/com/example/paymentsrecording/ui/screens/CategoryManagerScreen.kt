package com.example.paymentsrecording.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentsrecording.data.db.entity.Category
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.CategoryViewModel
import com.example.paymentsrecording.ui.components.CategoryIcons
import com.example.paymentsrecording.ui.components.toColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerScreen(factory: AppViewModelFactory, navController: NavHostController) {
    val vm: CategoryViewModel = viewModel(factory = factory)
    val categories by vm.categories.collectAsState()
    var editing by remember { mutableStateOf<Category?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分类管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editing = null
                showEditor = true
            }) { Icon(Icons.Outlined.Add, contentDescription = "新增分类") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            val expense = categories.filter { it.type == 0 }
            val income = categories.filter { it.type == 1 }
            item { SectionHeader("支出分类") }
            items(expense, key = { it.id }) { cat ->
                CategoryRow(cat,
                    onEdit = { editing = cat; showEditor = true },
                    onDelete = if (cat.isDefault) null else { { vm.delete(cat) } }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item { SectionHeader("收入分类") }
            items(income, key = { it.id }) { cat ->
                CategoryRow(cat,
                    onEdit = { editing = cat; showEditor = true },
                    onDelete = if (cat.isDefault) null else { { vm.delete(cat) } }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showEditor) {
        CategoryEditorSheet(
            editing = editing,
            onDismiss = { showEditor = false; editing = null },
            onSave = { cat ->
                if (editing == null) vm.add(cat) else vm.update(cat)
                showEditor = false
                editing = null
            }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun CategoryRow(cat: Category, onEdit: () -> Unit, onDelete: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(cat.color.toColor().copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(CategoryIcons.fromName(cat.icon), cat.name, tint = cat.color.toColor(), modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(cat.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        if (cat.isDefault) {
            Text("内置", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
        }
        IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, "编辑", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (onDelete != null) {
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, "删除", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryEditorSheet(
    editing: Category?,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(editing?.name ?: "") }
    var type by remember { mutableIntStateOf(editing?.type ?: 0) }
    val iconOptions = listOf("Restaurant","DirectionsBus","ShoppingBag","SportsEsports","Home","LocalHospital","School","PhoneIphone","Flight","Devices","ShoppingBasket","Work","CardGiftcard","TrendingUp","Replay","MoreHoriz")
    var icon by remember { mutableStateOf(editing?.icon ?: "Restaurant") }
    val colorOptions = listOf(0xFFE57373,0xFF64B5F6,0xFFBA68C8,0xFFFF8A65,0xFF4DB6AC,0xFFF06292,0xFF9575CD,0xFF4DD0E1,0xFFAED581,0xFF7986CB,0xFFFFD54F,0xFF66BB6A,0xFF26A69A,0xFF42A5F5,0xFFAB47BC,0xFFB0BEC5)
    var color by remember { mutableLongStateOf(editing?.color ?: colorOptions.first()) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(20.dp).padding(bottom = 24.dp)) {
            Text(if (editing == null) "新增分类" else "编辑分类",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("分类名称") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text("类型", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(selected = type == 0, onClick = { type = 0 },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("支出") }
                SegmentedButton(selected = type == 1, onClick = { type = 1 },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("收入") }
            }
            Spacer(Modifier.height(12.dp))
            Text("图标", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            IconPicker(iconOptions, icon) { icon = it }
            Spacer(Modifier.height(12.dp))
            Text("颜色", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            ColorPicker(colorOptions, color) { color = it }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    onSave(editing?.copy(name = name, type = type, icon = icon, color = color)
                        ?: Category(name = name, type = type, icon = icon, color = color, isDefault = false))
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotBlank()
            ) { Text("保存") }
        }
    }
}

@Composable
private fun IconPicker(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.take(8).forEach { ic ->
            val isSel = ic == selected
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .clickable { onSelect(ic) },
                contentAlignment = Alignment.Center
            ) {
                Icon(CategoryIcons.fromName(ic), ic,
                    tint = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ColorPicker(options: List<Long>, selected: Long, onSelect: (Long) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.take(8).forEach { c ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(c.toColor(), CircleShape)
                    .clickable { onSelect(c) }
                    .then(
                        if (c == selected) Modifier.padding(0.dp)
                        else Modifier
                    )
            ) {
                if (c == selected) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Edit, "选中", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
