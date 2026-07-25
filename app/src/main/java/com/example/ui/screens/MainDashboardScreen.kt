package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
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
import com.example.data.model.RootStatus
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.tabs.BatterySystemTab
import com.example.ui.tabs.CpuTab
import com.example.ui.tabs.GpuThermalTab
import com.example.ui.tabs.OverviewTab
import com.example.ui.theme.*
import com.example.ui.viewmodel.KernelMonitorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    viewModel: KernelMonitorViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val rootStatus by viewModel.rootStatus.collectAsState()

    val tabs = listOf(
        TabItem("Overview", Icons.Default.Dashboard, "tab_overview"),
        TabItem("CPU Cores", Icons.Default.Memory, "tab_cpu"),
        TabItem("GPU & Thermal", Icons.Default.Thermostat, "tab_gpu_thermal"),
        TabItem("Power & Sys", Icons.Default.BatteryChargingFull, "tab_battery_sys")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Kernel Monitor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )

                        // MTK badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MT6785",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                actions = {
                    // Root status indicator
                    val rootColor = when (rootStatus) {
                        RootStatus.GRANTED -> MatrixGreen
                        RootStatus.SIMULATION -> CyberCyan
                        RootStatus.DENIED -> AmberWarning
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(rootColor.copy(alpha = 0.15f))
                            .border(1.dp, rootColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = rootStatus.label,
                            color = rootColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Kernel Settings",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkKernelBg,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurfaceCard,
                contentColor = TextSecondary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberCyan,
                            indicatorColor = CyberCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        containerColor = DarkKernelBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> OverviewTab(viewModel = viewModel)
                1 -> CpuTab(viewModel = viewModel)
                2 -> GpuThermalTab(viewModel = viewModel)
                3 -> BatterySystemTab(viewModel = viewModel)
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }
}

private data class TabItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
