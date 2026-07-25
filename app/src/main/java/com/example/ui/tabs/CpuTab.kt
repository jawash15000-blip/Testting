package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CoreGridCard
import com.example.ui.components.SparklineChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.KernelMonitorViewModel

@Composable
fun CpuTab(
    viewModel: KernelMonitorViewModel,
    modifier: Modifier = Modifier
) {
    val cpuData = viewModel.cpuData.value
    val cpuHistory = viewModel.cpuFreqHistory.value

    val cores = cpuData?.cores ?: emptyList()
    val littleCores = cores.filter { !it.isBigCore }
    val bigCores = cores.filter { it.isBigCore }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("cpu_tab_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. Cluster Governors Overview
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .testTag("cluster_governor_card"),
                color = DarkSurfaceCard
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CPU CLUSTERS & GOVERNORS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // LITTLE Cluster (Cortex-A55)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant),
                            color = DarkSurfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "LITTLE Cluster (6x A55)",
                                    fontWeight = FontWeight.Bold,
                                    color = CpuLittleCoreColor,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Governor: ${cpuData?.littleGovernor ?: "schedutil"}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Freq: ${cpuData?.littleMinFreqMhz ?: 300} - ${cpuData?.littleMaxFreqMhz ?: 2000} MHz",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            }
                        }

                        // BIG Cluster (Cortex-A76)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant),
                            color = DarkSurfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "BIG Cluster (2x A76)",
                                    fontWeight = FontWeight.Bold,
                                    color = CpuBigCoreColor,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Governor: ${cpuData?.bigGovernor ?: "schedutil"}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Freq: ${cpuData?.bigMinFreqMhz ?: 500} - ${cpuData?.bigMaxFreqMhz ?: 2050} MHz",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Real-time CPU Load Graph
        item {
            SparklineChart(
                title = "Total CPU Utilization (%)",
                currentValueText = "${cpuData?.totalUsagePercent?.toInt() ?: 0}%",
                dataPoints = cpuHistory,
                lineColor = CyberCyan,
                minY = 0f,
                maxY = 100f
            )
        }

        // 3. Core Status Section Header
        item {
            Text(
                text = "PER-CORE FREQUENCY & LOAD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
        }

        // 4. Core Grid Cards List
        items(cores.size) { index ->
            CoreGridCard(core = cores[index])
        }
    }
}
