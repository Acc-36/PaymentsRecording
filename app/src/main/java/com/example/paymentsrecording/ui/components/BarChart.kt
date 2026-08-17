package com.example.paymentsrecording.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

/** 柱状图条目 */
data class BarEntry(val label: String, val expense: Double, val income: Double)

/**
 * 收支对比柱状图。每组双柱：支出(红) + 收入(绿)。
 */
@Composable
fun BarChart(
    entries: List<BarEntry>,
    modifier: Modifier = Modifier
) {
    val maxValue = max(
        entries.maxOfOrNull { max(it.expense, it.income) } ?: 0.0,
        1.0
    )
    val expenseColor = MaterialTheme.colorScheme.error
    val incomeColor = Color(0xFF30A46C)
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        if (entries.isEmpty()) {
            Text("暂无数据", color = MaterialTheme.colorScheme.onSurfaceVariant)
            return@Column
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 8.dp)
        ) {
            val labelAreaHeight = 28f
            val chartHeight = size.height - labelAreaHeight
            val groupWidth = size.width / entries.size
            val barWidth = (groupWidth * 0.28f).coerceAtMost(40f)
            val gap = barWidth * 0.25f

            // 基线
            drawLine(
                color = axisColor,
                start = Offset(0f, chartHeight),
                end = Offset(size.width, chartHeight),
                strokeWidth = 1f
            )

            entries.forEachIndexed { index, entry ->
                val groupCenter = groupWidth * index + groupWidth / 2f
                val baseY = chartHeight

                val expenseH = (entry.expense / maxValue * chartHeight).toFloat()
                val incomeH = (entry.income / maxValue * chartHeight).toFloat()

                drawRect(
                    color = expenseColor,
                    topLeft = Offset(groupCenter - barWidth - gap / 2, baseY - expenseH),
                    size = Size(barWidth, expenseH)
                )
                drawRect(
                    color = incomeColor,
                    topLeft = Offset(groupCenter + gap / 2, baseY - incomeH),
                    size = Size(barWidth, incomeH)
                )
            }
        }
        // x 轴标签
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            entries.forEach {
                Text(
                    it.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
