package com.logan.stater.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Process
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_ACCESS_NETWORK_STATE
import com.mozhimen.kotlin.utilk.android.net.UtilKActiveNetworkInfo
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

object StaterUtils {
    private var sCurProcessName: String? = null
    fun isMainProcess(context: Context): Boolean {
        val processName = getCurProcessName(context)
        return if (processName != null && processName.contains(":")) {
            false
        } else processName != null && processName == context.packageName
    }

    @OPermission_ACCESS_NETWORK_STATE
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            return UtilKActiveNetworkInfo.isAvailable(context)
        }
        return false
    }

    fun getCurProcessName(context: Context): String? {
        val procName = sCurProcessName
        if (!TextUtils.isEmpty(procName)) {
            return procName
        }
        try {
            val pid = Process.myPid()
            val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in mActivityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    sCurProcessName = appProcess.processName
                    return sCurProcessName
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        sCurProcessName = curProcessNameFromProc
        return sCurProcessName
    }// ignore

    // ignore
    private val curProcessNameFromProc: String?
        private get() {
            var cmdlineReader: BufferedReader? = null
            try {
                cmdlineReader = BufferedReader(
                    InputStreamReader(
                        FileInputStream(
                            "/proc/${Process.myPid()}/cmdline"
                        ),
                        "iso-8859-1"
                    )
                )
                var c: Int
                val processName = StringBuilder()
                while (cmdlineReader.read().also { c = it } > 0) {
                    processName.append(c.toChar())
                }
                return processName.toString()
            } catch (e: Throwable) {
                // ignore
            } finally {
                if (cmdlineReader != null) {
                    try {
                        cmdlineReader.close()
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
            return null
        }
}