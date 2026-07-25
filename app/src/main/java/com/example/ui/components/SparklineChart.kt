package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant

@Composable
fun SparklineChart(
    title: String,
    currentValueText: String,
    dataPoints: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    minY: Float? = null,
    maxY: Float? = null,
    unitSuffix: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("sparkline_chart_${title.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Text(
                text = currentValueText,
                style = MaterialTheme.typography.titleLarge,
                color = lineColor,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
        ) {
            if (dataPoints.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Awaiting telemetry...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val computedMin = minY ?: (dataPoints.minOrNull() ?: 0f)
                    val computedMax = maxY ?: (dataPoints.maxOrNull() ?: 100f).coerceAtLeast(computedMin + 1f)
                    val range = (computedMax - computedMin).coerceAtLeast(1f)

                    val strokePath = Path()
                    val fillPath = Path()

                    val stepX = if (dataPoints.size > 1) width / (dataPoints.size - 1) else width

                    dataPoints.forEachIndexed { index, value ->
                        val normalizedY = ((value - computedMin) / range).coerceIn(0f, 1f)
                        val x = index * stepX
                        val y = height - (normalizedY * height)

                        if (index == 0) {
                            strokePath.moveTo(x, y)
                            fillPath.moveTo(x, height)
                            fillPath.lineTo(x, y)
                        } else {
                            strokePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }

                        if (index == dataPoints.size - 1) {
                            fillPath.lineTo(x, height)
                            fillPath.close()
                        }
                    }

                    // Draw subtle area fill below graph
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )

                    // Draw main sparkline
                    drawPath(
                        path = strokePath,
                        color = lineColor,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }
        }
    }
}
