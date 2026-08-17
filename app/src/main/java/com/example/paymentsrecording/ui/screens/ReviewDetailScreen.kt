package com.example.paymentsrecording.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.ReviewViewModel
import com.example.paymentsrecording.ui.components.BarChart
import com.example.paymentsrecording.ui.components.LineChart
import com.example.paymentsrecording.ui.components.PieChart
import com.example.paymentsrecording.ui.components.PieSlice
import com.example.paymentsrecording.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    reviewId: Long,
    factory: AppViewModelFactory,
    navController: NavHostController
) {
    val vm: ReviewViewModel = viewModel(factory = factory)
    val stats by vm.currentStats.collectAsState()
    val review by vm.currentReview.collectAsState()

    LaunchedEffect(reviewId) { vm.loadById(reviewId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(review?.name ?: "回顾详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        val s = stats
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (review == null || s == null) {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }
            val r = review!!

            // 时间范围
            Text(
                "${DateUtil.fmtYMD.format(java.util.Date(r.startDate))}  ~  ${DateUtil.fmtYMD.format(java.util.Date(r.endDate))}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 概览卡
            Spacer(Modifier.height(12.dp))
            SummaryCard(s.totalIncome, s.totalExpense)

            // 饼图：分类支出占比
            Spacer(Modifier.height(20.dp))
            SectionTitle("分类支出占比")
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                PieChart(
                    slices = s.expenseByCategory.map {
                        PieSlice(it.name, it.amount, Color(it.color))
                    },
                    centerLabel = "总支出",
                    centerValue = "¥${String.format("%.0f", s.totalExpense)}"
                )
            }

            // 柱状图：收支对比（按天，过多则按天聚合为最多 30 柱）
            Spacer(Modifier.height(20.dp))
            SectionTitle("收支对比（每日）")
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                BarChart(entries = s.dailyBars.map { com.example.paymentsrecording.ui.components.BarEntry(it.label, it.expense, it.income) })
            }

            // 折线图：每日支出趋势
            Spacer(Modifier.height(20.dp))
            SectionTitle("每日支出趋势")
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                LineChart(points = s.expenseByDay.map { com.example.paymentsrecording.ui.components.LinePoint(it.dayLabel, it.amount) })
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "共 ${s.transactionCount} 笔记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SummaryCard(income: Double, expense: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(20.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("总支出", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("¥ ${String.format("%.2f", expense)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("总收入", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("¥ ${String.format("%.2f", income)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF30A46C))
            }
        }
    }
}
