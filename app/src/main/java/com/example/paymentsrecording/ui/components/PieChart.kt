package com.example.paymentsrecording.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** 饼图数据项 */
data class PieSlice(val label: String, val value: Double, val color: Color)

/**
 * 简洁环形饼图 + 图例。Canvas 手绘，无第三方依赖。
 */
@Composable
fun PieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    centerLabel: String = "",
    centerValue: String = ""
) {
    val total = slices.sumOf { it.value }.coerceAtLeast(0.0001)

    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                val strokeWidth = 36f
                val diameter = minOf(size.width, size.height) - strokeWidth
                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = Size(diameter, diameter)

                drawArc(
                    color = Color(0xFFE0E0E0).copy(alpha = 0.3f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                var startAngle = -90f
                for (slice in slices) {
                    if (slice.value <= 0) continue
                    val sweep = (slice.value / total * 360f).toFloat()
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (centerLabel.isNotEmpty()) {
                    Text(
                        text = centerLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (centerValue.isNotEmpty()) {
                    Text(
                        text = centerValue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (slices.isEmpty()) {
                Text(
                    "暂无数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                slices.take(6).forEach { slice ->
                    LegendRow(slice, total)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun LegendRow(slice: PieSlice, total: Double) {
    val percent = (slice.value / total * 100).let { String.format("%.1f%%", it) }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(slice.color, CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = slice.label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = percent,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
