package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SparklineChart
import com.example.ui.components.ThermalZoneCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.KernelMonitorViewModel

@Composable
fun GpuThermalTab(
    viewModel: KernelMonitorViewModel,
    modifier: Modifier = Modifier
) {
    val gpuData = viewModel.gpuData.value
    val gpuLoadHistory = viewModel.gpuLoadHistory.value
    val thermalZones = viewModel.thermalZones.value

    var searchQuery by remember { mutableStateOf("") }

    val filteredZones = remember(thermalZones, searchQuery) {
        if (searchQuery.isBlank()) {
            thermalZones
        } else {
            thermalZones.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.formattedName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("gpu_thermal_tab_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. GPU Overview Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, MatrixGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("gpu_card"),
                color = DarkSurfaceCard
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mali-G76 MC4 GPU",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Governor: ${gpuData?.governor ?: "ged_dynamic"}",
                                fontSize = 11.sp,
                                color = MatrixGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${gpuData?.currentFreqMhz ?: 300} MHz",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MatrixGreen,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Max: ${gpuData?.maxFreqMhz ?: 900} MHz",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("GPU Load", fontSize = 11.sp, color = TextMuted)
                        Text("${gpuData?.usagePercent?.toInt() ?: 0}%", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { ((gpuData?.usagePercent ?: 0f) / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MatrixGreen,
                        trackColor = DarkSurfaceVariant
                    )
                }
            }
        }

        // 2. GPU Load History Sparkline Graph
        item {
            SparklineChart(
                title = "GPU Utilization History (%)",
                currentValueText = "${gpuData?.usagePercent?.toInt() ?: 0}%",
                dataPoints = gpuLoadHistory,
                lineColor = MatrixGreen,
                minY = 0f,
                maxY = 100f
            )
        }

        // 3. Thermal Zone Search & Filter Section Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THERMAL SENSORS (${filteredZones.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    val peakTemp = thermalZones.maxOfOrNull { it.tempC } ?: 0f
                    val headerColor = if (peakTemp >= 55f) AmberWarning else CyberCyan

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = headerColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Peak $peakTemp°C",
                            color = headerColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter thermal zones (e.g. cpu, battery, charger)...", fontSize = 11.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                )
            }
        }

        // 4. Thermal Zones Cards
        if (filteredZones.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching thermal zones found.", color = TextMuted, fontSize = 12.sp)
                }
            }
        } else {
            items(filteredZones.size) { index ->
                ThermalZoneCard(zone = filteredZones[index])
            }
        }
    }
}
