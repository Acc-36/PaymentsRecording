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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.max

/** 折线图点 */
data class LinePoint(val label: String, val value: Double)

/**
 * 每日支出趋势折线图。
 */
@Composable
fun LineChart(
    points: List<LinePoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF00696D)
) {
    val maxValue = max(points.maxOfOrNull { it.value } ?: 0.0, 1.0)

    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        if (points.isEmpty()) {
            Text("暂无数据", color = MaterialTheme.colorScheme.onSurfaceVariant)
            return@Column
        }
        val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val labelAreaHeight = 28f
            val chartHeight = size.height - labelAreaHeight
            val stepX = if (points.size > 1) size.width / (points.size - 1) else size.width

            // 基线
            drawLine(
                color = axisColor,
                start = Offset(0f, chartHeight),
                end = Offset(size.width, chartHeight),
                strokeWidth = 1f
            )

            if (points.size == 1) {
                val y = chartHeight - (points[0].value / maxValue * chartHeight).toFloat()
                drawCircle(color = lineColor, radius = 6f, center = Offset(size.width / 2, y))
                return@Canvas
            }

            val path = Path()
            points.forEachIndexed { i, p ->
                val x = stepX * i
                val y = chartHeight - (p.value / maxValue * chartHeight).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path = path, color = lineColor, style = Stroke(width = 4f))

            points.forEachIndexed { i, p ->
                val x = stepX * i
                val y = chartHeight - (p.value / maxValue * chartHeight).toFloat()
                drawCircle(color = lineColor, radius = 4f, center = Offset(x, y))
            }
        }
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            points.take(8).forEach {
                Text(
                    it.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
