package com.example.data.provider

import com.example.data.model.VendorProfile

interface SysfsPathProvider {
    val vendorProfile: VendorProfile

    fun getCpuFreqPath(coreId: Int): String
    fun getCpuMinFreqPath(coreId: Int): String
    fun getCpuMaxFreqPath(coreId: Int): String
    fun getCpuOnlinePath(coreId: Int): String
    fun getCpuGovernorPath(clusterIndex: Int): String

    fun getGpuFreqPath(): String
    fun getGpuMaxFreqPath(): String
    fun getGpuUsagePath(): String
    fun getGpuGovernorPath(): String

    fun getThermalZoneDir(): String
    fun getThermalZoneTempPath(zoneId: Int): String
    fun getThermalZoneTypePath(zoneId: Int): String

    fun getBatteryCurrentPath(): String
    fun getBatteryVoltagePath(): String
    fun getBatteryTempPath(): String
    fun getBatteryStatusPath(): String
    fun getBatteryHealthPath(): String
}

class MtkMT6785SysfsProvider : SysfsPathProvider {
    override val vendorProfile: VendorProfile = VendorProfile.MTK_MT6785

    override fun getCpuFreqPath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/cpufreq/scaling_cur_freq"

    override fun getCpuMinFreqPath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/cpufreq/scaling_min_freq"

    override fun getCpuMaxFreqPath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/cpufreq/scaling_max_freq"

    override fun getCpuOnlinePath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/online"

    override fun getCpuGovernorPath(clusterIndex: Int): String {
        // MT6785 has policy0 (LITTLE cores 0-5) and policy6 (BIG cores 6-7)
        val policyId = if (clusterIndex == 0) 0 else 6
        return "/sys/devices/system/cpu/cpufreq/policy$policyId/scaling_governor"
    }

    override fun getGpuFreqPath(): String =
        "/sys/kernel/ged/hal/custom_upbound_gpu_freq" // Or /sys/class/misc/mali0/device/cur_freq

    override fun getGpuMaxFreqPath(): String =
        "/sys/class/misc/mali0/device/max_freq"

    override fun getGpuUsagePath(): String =
        "/sys/module/ged/parameters/gpu_loading" // MTK GED GPU loading or /sys/class/misc/mali0/device/utilization

    override fun getGpuGovernorPath(): String =
        "/sys/class/misc/mali0/device/power_policy"

    override fun getThermalZoneDir(): String =
        "/sys/class/thermal"

    override fun getThermalZoneTempPath(zoneId: Int): String =
        "/sys/class/thermal/thermal_zone$zoneId/temp"

    override fun getThermalZoneTypePath(zoneId: Int): String =
        "/sys/class/thermal/thermal_zone$zoneId/type"

    override fun getBatteryCurrentPath(): String =
        "/sys/class/power_supply/battery/current_now"

    override fun getBatteryVoltagePath(): String =
        "/sys/class/power_supply/battery/voltage_now"

    override fun getBatteryTempPath(): String =
        "/sys/class/power_supply/battery/temp"

    override fun getBatteryStatusPath(): String =
        "/sys/class/power_supply/battery/status"

    override fun getBatteryHealthPath(): String =
        "/sys/class/power_supply/battery/health"
}

class GenericSysfsProvider : SysfsPathProvider {
    override val vendorProfile: VendorProfile = VendorProfile.GENERIC_ANDROID

    override fun getCpuFreqPath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/cpufreq/scaling_cur_freq"

    override fun getCpuMinFreqPath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/cpufreq/scaling_min_freq"

    override fun getCpuMaxFreqPath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/cpufreq/scaling_max_freq"

    override fun getCpuOnlinePath(coreId: Int): String =
        "/sys/devices/system/cpu/cpu$coreId/online"

    override fun getCpuGovernorPath(clusterIndex: Int): String =
        "/sys/devices/system/cpu/cpu${clusterIndex * 4}/cpufreq/scaling_governor"

    override fun getGpuFreqPath(): String =
        "/sys/class/kgsl/kgsl-3d0/devfreq/cur_freq"

    override fun getGpuMaxFreqPath(): String =
        "/sys/class/kgsl/kgsl-3d0/devfreq/max_freq"

    override fun getGpuUsagePath(): String =
        "/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage"

    override fun getGpuGovernorPath(): String =
        "/sys/class/kgsl/kgsl-3d0/devfreq/governor"

    override fun getThermalZoneDir(): String =
        "/sys/class/thermal"

    override fun getThermalZoneTempPath(zoneId: Int): String =
        "/sys/class/thermal/thermal_zone$zoneId/temp"

    override fun getThermalZoneTypePath(zoneId: Int): String =
        "/sys/class/thermal/thermal_zone$zoneId/type"

    override fun getBatteryCurrentPath(): String =
        "/sys/class/power_supply/battery/current_now"

    override fun getBatteryVoltagePath(): String =
        "/sys/class/power_supply/battery/voltage_now"

    override fun getBatteryTempPath(): String =
        "/sys/class/power_supply/battery/temp"

    override fun getBatteryStatusPath(): String =
        "/sys/class/power_supply/battery/status"

    override fun getBatteryHealthPath(): String =
        "/sys/class/power_supply/battery/health"
}
