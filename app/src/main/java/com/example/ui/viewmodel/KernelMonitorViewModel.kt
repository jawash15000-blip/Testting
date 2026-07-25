package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.provider.VendorManager
import com.example.data.sysfs.RootChecker
import com.example.data.sysfs.SysfsReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KernelMonitorViewModel(application: Application) : AndroidViewModel(application) {

    private val sysfsReader = SysfsReader(application)

    private val _cpuData = MutableStateFlow<CpuData?>(null)
    val cpuData: StateFlow<CpuData?> = _cpuData.asStateFlow()

    private val _gpuData = MutableStateFlow<GpuData?>(null)
    val gpuData: StateFlow<GpuData?> = _gpuData.asStateFlow()

    private val _thermalZones = MutableStateFlow<List<ThermalZone>>(emptyList())
    val thermalZones: StateFlow<List<ThermalZone>> = _thermalZones.asStateFlow()

    private val _batteryData = MutableStateFlow<BatteryData?>(null)
    val batteryData: StateFlow<BatteryData?> = _batteryData.asStateFlow()

    private val _systemInfo = MutableStateFlow<SystemInfo?>(null)
    val systemInfo: StateFlow<SystemInfo?> = _systemInfo.asStateFlow()

    private val _pollingIntervalMs = MutableStateFlow(1000L)
    val pollingIntervalMs: StateFlow<Long> = _pollingIntervalMs.asStateFlow()

    private val _activeVendor = MutableStateFlow(VendorProfile.MTK_MT6785)
    val activeVendor: StateFlow<VendorProfile> = _activeVendor.asStateFlow()

    private val _isSimulationMode = MutableStateFlow(false)
    val isSimulationMode: StateFlow<Boolean> = _isSimulationMode.asStateFlow()

    private val _rootStatus = MutableStateFlow(RootStatus.DENIED)
    val rootStatus: StateFlow<RootStatus> = _rootStatus.asStateFlow()

    // Real-time chart history buffers (Max 30 entries)
    private val _cpuFreqHistory = MutableStateFlow<List<Float>>(emptyList())
    val cpuFreqHistory: StateFlow<List<Float>> = _cpuFreqHistory.asStateFlow()

    private val _gpuLoadHistory = MutableStateFlow<List<Float>>(emptyList())
    val gpuLoadHistory: StateFlow<List<Float>> = _gpuLoadHistory.asStateFlow()

    private val _batteryCurrentHistory = MutableStateFlow<List<Float>>(emptyList())
    val batteryCurrentHistory: StateFlow<List<Float>> = _batteryCurrentHistory.asStateFlow()

    private val _peakThermalHistory = MutableStateFlow<List<Float>>(emptyList())
    val peakThermalHistory: StateFlow<List<Float>> = _peakThermalHistory.asStateFlow()

    // Diagnostic result
    private val _diagnosticResult = MutableStateFlow<SysfsDiagnostic?>(null)
    val diagnosticResult: StateFlow<SysfsDiagnostic?> = _diagnosticResult.asStateFlow()

    init {
        checkRootAccess()
        startMonitoringLoop()
    }

    fun checkRootAccess() {
        viewModelScope.launch(Dispatchers.IO) {
            val isRooted = RootChecker.isRooted()
            _rootStatus.value = if (_isSimulationMode.value) {
                RootStatus.SIMULATION
            } else if (isRooted) {
                RootStatus.GRANTED
            } else {
                RootStatus.DENIED
            }
        }
    }

    fun setPollingInterval(intervalMs: Long) {
        _pollingIntervalMs.value = intervalMs
    }

    fun setVendorProfile(profile: VendorProfile) {
        _activeVendor.value = profile
        sysfsReader.activeProvider = VendorManager.getProvider(profile)
    }

    fun toggleSimulationMode(enabled: Boolean) {
        _isSimulationMode.value = enabled
        sysfsReader.isSimulationMode = enabled
        checkRootAccess()
    }

    fun diagnoseSysfsPath(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _diagnosticResult.value = sysfsReader.diagnosePath(path)
        }
    }

    private fun startMonitoringLoop() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val cpu = sysfsReader.fetchCpuData()
                    val gpu = sysfsReader.fetchGpuData()
                    val thermals = sysfsReader.fetchThermalZones()
                    val battery = sysfsReader.fetchBatteryData()
                    val sysInfo = sysfsReader.fetchSystemInfo(_rootStatus.value, _pollingIntervalMs.value)

                    _cpuData.value = cpu
                    _gpuData.value = gpu
                    _thermalZones.value = thermals
                    _batteryData.value = battery
                    _systemInfo.value = sysInfo

                    // Update sparkline chart buffer histories
                    _cpuFreqHistory.value = appendHistory(_cpuFreqHistory.value, cpu.totalUsagePercent)
                    _gpuLoadHistory.value = appendHistory(_gpuLoadHistory.value, gpu.usagePercent)
                    _batteryCurrentHistory.value = appendHistory(_batteryCurrentHistory.value, battery.currentMa.toFloat())

                    val maxTemp = thermals.maxOfOrNull { it.tempC } ?: 35f
                    _peakThermalHistory.value = appendHistory(_peakThermalHistory.value, maxTemp)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(_pollingIntervalMs.value)
            }
        }
    }

    private fun appendHistory(list: List<Float>, newValue: Float): List<Float> {
        val maxPoints = 30
        val updated = list.toMutableList()
        updated.add(newValue)
        if (updated.size > maxPoints) {
            updated.removeAt(0)
        }
        return updated
    }
}
