package com.example.data.provider

import com.example.data.model.VendorProfile

object VendorManager {
    fun getProvider(profile: VendorProfile): SysfsPathProvider {
        return when (profile) {
            VendorProfile.MTK_MT6785 -> MtkMT6785SysfsProvider()
            VendorProfile.GENERIC_ANDROID -> GenericSysfsProvider()
        }
    }
}
