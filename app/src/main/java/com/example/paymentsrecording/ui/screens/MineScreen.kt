package com.example.paymentsrecording.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.MineViewModel
import com.example.paymentsrecording.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(
    factory: AppViewModelFactory,
    themeMode: Int,
    onThemeChange: (Int) -> Unit,
    navController: NavHostController
) {
    val vm: MineViewModel = viewModel(factory = factory)
    val exportResult by vm.exportResult.collectAsState()
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("我的", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 主题设置
            SettingsCard(
                icon = Icons.Outlined.Brightness6,
                title = "深浅色模式",
                subtitle = when (themeMode) { 0 -> "跟随系统"; 1 -> "浅色"; 2 -> "深色"; else -> "" }
            ) { showThemeDialog = true }

            Spacer(Modifier.height(12.dp))
            // 分类管理
            SettingsCard(
                icon = Icons.Outlined.Category,
                title = "账单分类管理",
                subtitle = "新增、编辑、删除分类"
            ) { navController.navigate(Screen.CategoryManager.route) }

            Spacer(Modifier.height(12.dp))
            // 预算管理
            SettingsCard(
                icon = Icons.Outlined.Savings,
                title = "预算管理",
                subtitle = "设置月度预算与记账提醒"
            ) { navController.navigate(Screen.Budget.route) }

            Spacer(Modifier.height(12.dp))
            // 导入管理
            SettingsCard(
                icon = Icons.Outlined.CloudUpload,
                title = "导入账单",
                subtitle = "支付宝 / 微信截图或流水文件"
            ) { navController.navigate(Screen.Import.route) }

            Spacer(Modifier.height(12.dp))
            // 数据导出
            SettingsCard(
                icon = Icons.Outlined.Download,
                title = "导出数据",
                subtitle = "导出为 CSV 格式"
            ) {
                val fileName = "payments_${System.currentTimeMillis()}.csv"
                val resolver = context.contentResolver
                val values = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Download/PaymentsRecording")
                    }
                }
                val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
                else
                    android.provider.MediaStore.Files.getContentUri("external")
                val uri = resolver.insert(collection, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.let { out -> vm.exportCsv(out) }
                } else {
                    Toast.makeText(context, "无法创建文件", Toast.LENGTH_SHORT).show()
                }
            }

            // 导出结果提示
            exportResult?.let { res ->
                Spacer(Modifier.height(8.dp))
                Text(
                    res.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (res.success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                LaunchedEffect(res) { vm.clearExportResult() }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "记账本 v1.0  ·  Material Design 3",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("深浅色模式") },
            text = {
                Column {
                    ThemeOption("跟随系统", themeMode == 0) { onThemeChange(0); showThemeDialog = false }
                    ThemeOption("浅色模式", themeMode == 1) { onThemeChange(1); showThemeDialog = false }
                    ThemeOption("深色模式", themeMode == 2) { onThemeChange(2); showThemeDialog = false }
                }
            },
            confirmButton = { TextButton(onClick = { showThemeDialog = false }) { Text("关闭") } }
        )
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
private fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp).size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}
