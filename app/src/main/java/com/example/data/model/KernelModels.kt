package com.example.data.model

data class CoreInfo(
    val id: Int,
    val currentFreqMhz: Int,
    val minFreqMhz: Int,
    val maxFreqMhz: Int,
    val isOnline: Boolean,
    val isBigCore: Boolean, // True for Cortex-A76, False for Cortex-A55
    val governor: String,
    val usagePercent: Float
)

data class CpuData(
    val cores: List<CoreInfo>,
    val totalUsagePercent: Float,
    val littleGovernor: String,
    val bigGovernor: String,
    val littleMinFreqMhz: Int,
    val littleMaxFreqMhz: Int,
    val bigMinFreqMhz: Int,
    val bigMaxFreqMhz: Int,
    val scalingGovernorsAvailable: List<String>
)

data class GpuData(
    val currentFreqMhz: Int,
    val maxFreqMhz: Int,
    val usagePercent: Float,
    val governor: String,
    val powerProfile: String
)

data class ThermalZone(
    val id: Int,
    val name: String,
    val formattedName: String,
    val tempC: Float,
    val sysfsPath: String
)

data class BatteryData(
    val levelPercent: Int,
    val status: String,
    val health: String,
    val voltageMv: Int,
    val currentMa: Int, // Positive = Charging, Negative = Discharging
    val powerWatts: Float,
    val tempC: Float,
    val technology: String,
    val pluggedState: String
)

data class SystemInfo(
    val deviceModel: String,
    val deviceBoard: String,
    val deviceHardware: String,
    val kernelVersion: String,
    val androidVersion: String,
    val uptimeFormatted: String,
    val rootStatus: RootStatus,
    val activeVendor: VendorProfile,
    val pollingIntervalMs: Long
)

enum class RootStatus(val label: String, val description: String) {
    GRANTED("ROOT GRANTED", "Access via Magisk / KernelSU active"),
    DENIED("NON-ROOT", "Reading standard sysfs / Android APIs"),
    SIMULATION("SIMULATION MODE", "Interactive simulated kernel sysfs data")
}

enum class VendorProfile(val id: String, val displayName: String, val chipsetName: String) {
    MTK_MT6785("mtk_mt6785", "MediaTek MT6785 (Rosemary)", "Helio G95 / Mali-G76"),
    GENERIC_ANDROID("generic_android", "Generic Android Sysfs", "Linux Kernel standard")
}

data class SysfsDiagnostic(
    val path: String,
    val exists: Boolean,
    val isReadable: Boolean,
    val rawValue: String
)
