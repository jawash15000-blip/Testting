package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Info
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
import com.example.ui.components.SparklineChart
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.KernelMonitorViewModel

@Composable
fun BatterySystemTab(
    viewModel: KernelMonitorViewModel,
    modifier: Modifier = Modifier
) {
    val battery = viewModel.batteryData.value
    val systemInfo = viewModel.systemInfo.value
    val batteryHistory = viewModel.batteryCurrentHistory.value

    val isCharging = (battery?.currentMa ?: 0) > 0
    val batteryAccent = if (isCharging) MatrixGreen else AmberWarning

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("battery_system_tab_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. Battery Power Meter Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, batteryAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("battery_meter_card"),
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
                                Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = batteryAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "BATTERY & POWER FLOW",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${battery?.status ?: "Discharging"} (${battery?.pluggedState ?: "Unplugged"})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Text(
                            text = "${battery?.levelPercent ?: 85}%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = batteryAccent,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { ((battery?.levelPercent ?: 85) / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = batteryAccent,
                        trackColor = DarkSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("CURRENT FLOW", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${battery?.currentMa ?: 0} mA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = batteryAccent
                            )
                        }
                        Column {
                            Text("POWER DYNAMICS", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${battery?.powerWatts ?: 0.0f} W",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("VOLTAGE / TEMP", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${battery?.voltageMv ?: 3980} mV | ${battery?.tempC ?: 34.5f}°C",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 2. Battery Current History Sparkline Graph
        item {
            SparklineChart(
                title = "Battery Current Dynamics (mA)",
                currentValueText = "${battery?.currentMa ?: 0} mA",
                dataPoints = batteryHistory,
                lineColor = batteryAccent,
                unitSuffix = " mA"
            )
        }

        // 3. System & Kernel Details Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceCard)
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .testTag("system_details_card"),
                color = DarkSurfaceCard
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KERNEL & HARDWARE SPECIFICATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val details = listOf(
                        "Device Hardware" to (systemInfo?.deviceHardware ?: "mt6785"),
                        "Device Board" to (systemInfo?.deviceBoard ?: "rosemary"),
                        "Android OS" to (systemInfo?.androidVersion ?: "Android 14"),
                        "Kernel Version" to (systemInfo?.kernelVersion ?: "Linux 4.14.186"),
                        "Active Vendor Profile" to (systemInfo?.activeVendor?.displayName ?: "MediaTek MT6785"),
                        "Uptime" to (systemInfo?.uptimeFormatted ?: "0h 0m 0s"),
                        "Battery Chemistry" to (battery?.technology ?: "Li-poly"),
                        "Battery Health" to (battery?.health ?: "Good")
                    )

                    details.forEachIndexed { index, (label, valStr) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label, fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = valStr,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        if (index < details.size - 1) {
                            HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}
