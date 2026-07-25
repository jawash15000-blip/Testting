package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RootStatus
import com.example.ui.components.SparklineChart
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.KernelMonitorViewModel

@Composable
fun OverviewTab(
    viewModel: KernelMonitorViewModel,
    modifier: Modifier = Modifier
) {
    val systemInfo = viewModel.systemInfo.value
    val cpuData = viewModel.cpuData.value
    val gpuData = viewModel.gpuData.value
    val batteryData = viewModel.batteryData.value
    val thermals = viewModel.thermalZones.value

    val cpuHistory = viewModel.cpuFreqHistory.value
    val peakThermalHistory = viewModel.peakThermalHistory.value

    val rootStatus = viewModel.rootStatus.value

    val peakThermal = thermals.maxByOrNull { it.tempC }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("overview_tab_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. Device & Kernel Hardware Header Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("header_card"),
                color = DarkSurfaceCard
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = systemInfo?.deviceModel ?: "Redmi Note 10S (rosemary)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Chipset: MT6785 (Helio G95)",
                                    fontSize = 11.sp,
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Root Badge
                        val rootColor = when (rootStatus) {
                            RootStatus.GRANTED -> MatrixGreen
                            RootStatus.SIMULATION -> CyberCyan
                            RootStatus.DENIED -> AmberWarning
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(rootColor.copy(alpha = 0.15f))
                                .border(1.dp, rootColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = rootStatus.label,
                                color = rootColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("KERNEL VERSION", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = systemInfo?.kernelVersion ?: "Linux 4.14.186-kernel",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("SYSTEM UPTIME", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = systemInfo?.uptimeFormatted ?: "0h 0m 0s",
                                fontSize = 11.sp,
                                color = MatrixGreen,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Metrics Grid (CPU Load, GPU Load, Peak Temp, Charge Rate)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "CPU Total Usage",
                        value = "${cpuData?.totalUsagePercent?.toInt() ?: 0}%",
                        subtitle = "${cpuData?.cores?.count { it.isOnline } ?: 8} / 8 Cores Active",
                        accentColor = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "GPU Usage",
                        value = "${gpuData?.usagePercent?.toInt() ?: 0}%",
                        subtitle = "${gpuData?.currentFreqMhz ?: 300} MHz",
                        accentColor = MatrixGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val peakTemp = peakThermal?.tempC ?: 36.5f
                    val tempColor = if (peakTemp >= 60f) AlertRed else if (peakTemp >= 48f) AmberWarning else CyberCyan

                    StatMetricCard(
                        title = "Peak Thermal Zone",
                        value = "$peakTemp°C",
                        subtitle = peakThermal?.formattedName ?: "MTK Thermal",
                        accentColor = tempColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Battery Flow",
                        value = "${batteryData?.powerWatts ?: 0.0f} W",
                        subtitle = "${batteryData?.currentMa ?: 0} mA | ${batteryData?.status ?: "Normal"}",
                        accentColor = if ((batteryData?.currentMa ?: 0) > 0) MatrixGreen else AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Live CPU Telemetry Sparkline Graph
        item {
            SparklineChart(
                title = "CPU Usage Telemetry (%)",
                currentValueText = "${cpuData?.totalUsagePercent?.toInt() ?: 0}%",
                dataPoints = cpuHistory,
                lineColor = CyberCyan,
                minY = 0f,
                maxY = 100f
            )
        }

        // 4. Live Peak Temperature Sparkline Graph
        item {
            SparklineChart(
                title = "Peak Hardware Thermal (°C)",
                currentValueText = "${peakThermal?.tempC ?: 36.5f}°C",
                dataPoints = peakThermalHistory,
                lineColor = if ((peakThermal?.tempC ?: 35f) >= 55f) AlertRed else AmberWarning,
                minY = 25f,
                maxY = 80f
            )
        }
    }
}
