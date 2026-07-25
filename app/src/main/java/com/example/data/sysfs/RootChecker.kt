package com.example.data.sysfs

import java.io.File

object RootChecker {
    fun isRooted(): Boolean {
        return checkSuBinary() || checkSuExecution()
    }

    private fun checkSuBinary(): Boolean {
        val suPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in suPaths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkSuExecution(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val reader = process.inputStream.bufferedReader()
            val output = reader.readLine()
            process.destroy()
            output?.contains("uid=0") == true
        } catch (e: Exception) {
            false
        }
    }
}
