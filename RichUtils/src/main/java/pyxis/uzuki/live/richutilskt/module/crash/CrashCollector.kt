package pyxis.uzuki.live.richutilskt.module.crash

import android.app.ActivityManager
import android.app.Application
import android.content.Context

object CrashCollector {

    @JvmStatic @JvmOverloads fun initCrashCollector(app: Application, config: CrashConfig = CrashConfig.Builder().build(app.applicationContext)) {
        val context = app.applicationContext
        val pid = android.os.Process.myPid()

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.runningAppProcesses
                .filter { it.pid == pid && it.processName.equals(context.packageName, true) }
                .forEach { Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance(config)) }
    }

}
