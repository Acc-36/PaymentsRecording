package com.example.paymentsrecording.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(navController: NavHostController) {
    var source by remember { mutableStateOf<String?>(null) } // "alipay" / "wechat" / "file"
    var fileName by remember { mutableStateOf<String?>(null) }
    var fileUri by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var importing by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }

    // 文件/图片选择
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fileUri = uri.toString()
            fileName = uri.lastPathSegment ?: "已选择截图"
            progress = 0f; done = false
        }
    }
    val pickFile = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fileUri = uri.toString()
            fileName = uri.lastPathSegment ?: "已选择文件"
            progress = 0f; done = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("导入账单") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            // 提示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.UploadFile, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("导入说明", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("支持支付宝/微信账单截图、流水 CSV/Excel 文件。\n导入逻辑后续版本实现，当前为界面预览。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("选择来源", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            // 来源选择
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SourceChip("支付宝", source == "alipay") { source = "alipay"; pickImage.launch("image/*") }
                SourceChip("微信", source == "wechat") { source = "wechat"; pickImage.launch("image/*") }
                SourceChip("流水文件", source == "file") {
                    source = "file"
                    pickFile.launch("*/*")
                }
            }

            // 文件预览
            fileName?.let { name ->
                Spacer(Modifier.height(20.dp))
                Text("已选文件", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        val isImage = source != "file"
                        if (isImage && fileUri != null) {
                            AsyncImage(
                                model = fileUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Icon(Icons.Outlined.InsertDriveFile, contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                            Text(
                                if (source == "file") "流水文件" else "账单截图",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        importing = true
                        progress = 0f
                        done = false
                    },
                    enabled = !importing && !done,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text(if (done) "导入完成" else "开始导入") }

                if (importing || done) {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (done) "导入完成（模拟）" else "正在识别并解析账单… ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // 模拟导入进度
    LaunchedEffect(importing) {
        if (importing) {
            while (progress < 1f) {
                progress = (progress + 0.05f).coerceAtMost(1f)
                delay(60)
            }
            importing = false
            done = true
        }
    }
}

@Composable
private fun SourceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    )
}
