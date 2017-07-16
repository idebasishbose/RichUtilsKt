package pyxis.uzuki.live.richutilskt.module.crash

import android.content.Context
import android.os.Build
import android.text.TextUtils
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * RichUtilsKt
 * CrashModel
 * Created by winds on 2017-07-16.
 */
data class CrashConfig(var displayDeviceInfo: Boolean, var logLevel: LogLevel, var timeFormat: String, var versionStr: String,
                       var packageName: String, var logLocation: String, var documentType: DocumentType) {
    class Builder {
        private var displayDeviceInfo = true
        private var logLevel = LogLevel.MESSAGE
        private var timeFormat = "yyyy-MM-dd (E) a hh:mm:ss.SSS"
        private var packageName = "pyxis.uzuki.live.richutilskt"
        private var versionStr = "1.0.0(1)"
        private var logLocation: String = ""
        private var documentType = DocumentType.MARKDOWN

        fun setDisplayDeviceInfo(displayDeviceInfo: Boolean): Builder {
            this.displayDeviceInfo = displayDeviceInfo
            return this
        }

        fun setLogLevel(logLevel: LogLevel): Builder {
            this.logLevel = logLevel
            return this
        }

        fun setTimeFormat(timeFormat: String): Builder {
            this.timeFormat = timeFormat
            return this
        }

        fun setPackageName(packageName: String): Builder {
            this.packageName = packageName
            return this
        }

        fun setVersionStr(versionStr: String): Builder {
            this.versionStr = versionStr
            return this
        }

        fun setLogLocation(logLocation: String): Builder {
            this.logLocation = logLocation
            return this
        }

        fun setDocumentType(documentType: DocumentType): Builder {
            this.documentType = documentType
            return this
        }

        fun build(context: Context): CrashConfig {
            if (TextUtils.isEmpty(logLocation))
                logLocation = context.getExternalFilesDir("crash").toString()

            return CrashConfig(displayDeviceInfo, logLevel, timeFormat, versionStr, packageName, logLocation, documentType)
        }
    }
}

class CrashModel() {
    var packageName: String = ""
    var versionStr: String = ""
    var modelStr: String = ""
    var productStr: String = ""
    var deviceStr: String = ""
    var sdkStr: String = ""
    var sdkNum: Int = 0
    var manufacturerStr: String  = ""
    var timeStr: String = ""
    var message: String  = ""
    var localizedMessage: String  = ""
    var stackTrace: String  = ""

    fun setThrowable(ex: Throwable) {
        val result = StringWriter()
        val printWriter = PrintWriter(result)
        ex.printStackTrace(printWriter)

        printWriter.use {
            val cause = ex.cause
            cause?.printStackTrace(printWriter)
        }

        this.message = ex.message as String
        this.localizedMessage = ex.localizedMessage
        this.stackTrace = result.toString()
    }

    fun setDeviceInfo() {
        this.modelStr = Build.MODEL
        this.productStr = Build.PRODUCT
        this.deviceStr = Build.DEVICE
        this.sdkStr = Build.VERSION.SDK
        this.sdkNum = Build.VERSION.SDK_INT
        this.manufacturerStr = Build.MANUFACTURER
    }

    fun setConfig(config: CrashConfig) {
        this.packageName = config.packageName
        this.versionStr = config.versionStr
    }

    fun setTime(date: Date, config: CrashConfig) {
        val dateFormat = SimpleDateFormat(config.timeFormat, Locale.US)
        this.timeStr = dateFormat.format(date)
    }

    override fun toString(): String {
        return "CrashModel(packageName='$packageName', versionStr='$versionStr', modelStr='$modelStr', productStr='$productStr', deviceStr='$deviceStr', " +
                "sdkStr='$sdkStr', sdkNum=$sdkNum, manufacturerStr='$manufacturerStr', timeStr='$timeStr', message='$message', localizedMessage='$localizedMessage', stackTrace='$stackTrace')"
    }
}

enum class LogLevel {
    MESSAGE, STACKTRACE
}

enum class DocumentType {
    MARKDOWN
}