package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.RootStatus
import com.example.data.model.VendorProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.KernelMonitorViewModel

@Composable
fun SettingsDialog(
    viewModel: KernelMonitorViewModel,
    onDismiss: () -> Unit
) {
    val pollingInterval by viewModel.pollingIntervalMs.collectAsState()
    val activeVendor by viewModel.activeVendor.collectAsState()
    val isSimulation by viewModel.isSimulationMode.collectAsState()
    val rootStatus by viewModel.rootStatus.collectAsState()
    val diagnosticResult by viewModel.diagnosticResult.collectAsState()

    var customPathToTest by remember { mutableStateOf("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceCard,
        titleContentColor = TextPrimary,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kernel Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                // 1. Root Status Card
                Text(
                    text = "SYSTEM ACCESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val rootColor = when (rootStatus) {
                    RootStatus.GRANTED -> MatrixGreen
                    RootStatus.SIMULATION -> CyberCyan
                    RootStatus.DENIED -> AmberWarning
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, rootColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    color = DarkSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = rootStatus.label,
                                color = rootColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = rootStatus.description,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.checkRootAccess() },
                            modifier = Modifier.testTag("recheck_root_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recheck Root", tint = CyberCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Simulation Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simulation / Demo Mode",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Emulate MTK sysfs data on non-rooted devices",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = isSimulation,
                        onCheckedChange = { viewModel.toggleSimulationMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = CyberCyan
                        ),
                        modifier = Modifier.testTag("simulation_mode_switch")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Polling Interval Picker
                Text(
                    text = "POLLING REFRESH RATE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val intervals = listOf(
                    500L to "500 ms (Real-time)",
                    1000L to "1 sec (Balanced)",
                    2000L to "2 sec (Eco)",
                    5000L to "5 sec (Low Battery)"
                )

                intervals.forEach { (ms, label) ->
                    val isSelected = pollingInterval == ms
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else DarkSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) CyberCyan else DarkBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setPollingInterval(ms) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("polling_option_$ms"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) CyberCyan else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = CyberCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Vendor Profile Selection
                Text(
                    text = "HARDWARE VENDOR PROFILE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                VendorProfile.entries.forEach { profile ->
                    val isSelected = activeVendor == profile
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MatrixGreen.copy(alpha = 0.15f) else DarkSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) MatrixGreen else DarkBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setVendorProfile(profile) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("vendor_profile_${profile.id}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = profile.displayName,
                                color = if (isSelected) MatrixGreen else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = profile.chipsetName,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = MatrixGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Sysfs Path Inspector Diagnostic Tool
                Text(
                    text = "SYSFS PATH INSPECTOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customPathToTest,
                    onValueChange = { customPathToTest = it },
                    label = { Text("Enter sysfs path", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { viewModel.diagnoseSysfsPath(customPathToTest) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("diagnose_path_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Inspect Path", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                diagnosticResult?.let { diag ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkKernelBg)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp)),
                        color = DarkKernelBg
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Path: ${diag.path}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary
                            )
                            Text(
                                text = "Exists: ${diag.exists} | CanRead: ${diag.isReadable}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (diag.exists) MatrixGreen else AlertRed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Raw Output:\n${diag.rawValue}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Done", fontSize = 12.sp)
            }
        }
    )
}
