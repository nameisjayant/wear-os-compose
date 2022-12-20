package com.example.focusworkwearapp.utils

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
class Utils(private val context: Context) {
    var usageStatsManager: UsageStatsManager? = null
    private val EXTRA_LAST_APP = "EXTRA_LAST_APP"
    private val LOCKED_APPS = "LOCKED_APPS"

    val launcherTopApp: String

        get() {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                usageStatsManager =
                    context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                val taskInfoList = manager.getRunningTasks(1)
                if (null != taskInfoList && !taskInfoList.isEmpty()) {
                    return taskInfoList[0].topActivity!!.packageName
                }
            } else {
                val endTime = System.currentTimeMillis()
                val beginTime = endTime - 10000
                var result = ""
                val event = UsageEvents.Event()
                val usageEvents = usageStatsManager!!.queryEvents(beginTime, endTime)
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event)
                    if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        result = event.packageName
                    }
                }
                if (!TextUtils.isEmpty(result)) Log.d("RESULT", result)
                return result
            }
            return ""
        }

    companion object {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun checkPermission(ctx: Context): Boolean {
            var appOpsManager: AppOpsManager? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                appOpsManager = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            }
            val mode = appOpsManager!!.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                ctx.packageName
            )
            return mode == AppOpsManager.MODE_ALLOWED
        }
    }
}

fun formatTime(seconds: String, minutes: String, hours: String): String {
    return "$hours:$minutes:$seconds"
}

fun Int.pad(): String {
    return this.toString().padStart(2, '0')
}