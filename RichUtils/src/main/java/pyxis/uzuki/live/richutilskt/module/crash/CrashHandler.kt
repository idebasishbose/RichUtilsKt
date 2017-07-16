package pyxis.uzuki.live.richutilskt.module.crash

import pyxis.uzuki.live.richutilskt.utils.runAsync
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * RichUtilsKt
 * CrashHandler
 * Created by winds on 2017-07-16.
 */
class CrashHandler private constructor(var config: CrashConfig) : Thread.UncaughtExceptionHandler {
    var crashModel: CrashModel? = null

    override fun uncaughtException(thread: Thread?, e: Throwable?) {
        handleException(e)
    }

    private fun handleException(ex: Throwable?) {
        if (ex == null)
            return

        crashModel = generateRichCrashModel(ex)
        runAsync {
            saveLocal(writeLogIntoMarkdown())
        }
    }

    private fun saveLocal(message: String) {
        val fileName = getFileName()
        val file = File(config.logLocation, fileName)

        if (!file.exists())
            file.createNewFile()

        val messageBytes = message.toByteArray()
        val fos: FileOutputStream? = FileOutputStream(file, true)

        fos?.use {
            fos.write(messageBytes)
            fos.flush()
        }

        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(10)
    }

    private fun generateRichCrashModel(ex: Throwable): CrashModel {
        val now = Calendar.getInstance()

        val richCrashModel = CrashModel()
        richCrashModel.setConfig(config)
        richCrashModel.setDeviceInfo()
        richCrashModel.setThrowable(ex)
        richCrashModel.setTime(now.time, config)
        return richCrashModel
    }

    private fun writeLogIntoMarkdown(): String {
        val model = crashModel as CrashModel
        var content = ""

        val basic = "## Crash Log in \n${model.packageName}\n### Application Info\n* Version: **${model.packageName}**\n* Version: **${model.versionStr}**\n\n"
        val crash = "### Crash Info\n* When: **${model.timeStr}**\n* Message: **${model.message}**\n* Localized Message: **${model.localizedMessage}**\n\n"
        val deviceInfo = "### Device Info\n* Device: **${model.modelStr} (a.k.a ${model.productStr} or ${model.deviceStr})**\n* Version: **${model.sdkStr} (${model.sdkNum})**\n* Manufacturer: **${model.manufacturerStr}**\n\n"
        val stackTrace = "#### Stack Trace\n````\n${model.stackTrace}\n````\n\n"

        content += basic
        if (config.displayDeviceInfo)
            content += deviceInfo
        content += crash
        content += stackTrace

        return content
    }

    private fun getFileName(): String {
        return "crash_${SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(System.currentTimeMillis())}${getExtension()}"
    }

    private fun getExtension(): String {
        when (config.documentType) {
            DocumentType.MARKDOWN -> return ".md"
        }
    }

    companion object {
        var instance: CrashHandler? = null

        fun getInstance(config: CrashConfig): CrashHandler {
            if (instance == null)
                instance = CrashHandler(config)

            return instance as CrashHandler
        }
    }
}