package ir.vadomosan.security.emulator

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Log
import java.util.*

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class Emulator private constructor(@field:SuppressLint("StaticFieldLeak") private val mContext: Context) {
    interface OnEmulatorDetectorListener {
        fun onResult(isEmulator: Boolean)
    }

    var isDebug = false
        private set

    private val mListPackageName: MutableList<String> =
        ArrayList()

    fun detect(pOnEmulatorDetectorListener: OnEmulatorDetectorListener?) {
        Thread(Runnable {
            val isEmulator = detect()
            log("This System is Emulator: $isEmulator")
            pOnEmulatorDetectorListener?.onResult(isEmulator)
        }).start()
    }

    private fun detect(): Boolean {
        var result = false
        log(deviceInfo)

        // Check Basic
        if (!result) {
            result = checkBasic()
            log("Check basic $result")
        }

        return result
    }

    private fun checkBasic(): Boolean {
        var result = (Build.FINGERPRINT.startsWith("generic")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.toLowerCase().contains("droid4x")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE == "goldfish" || Build.HARDWARE == "vbox86" || Build.PRODUCT == "sdk" || Build.PRODUCT == "google_sdk" || Build.PRODUCT == "sdk_x86" || Build.PRODUCT == "vbox86p" || Build.BOARD.toLowerCase()
            .contains("nox")
                || Build.BOOTLOADER.toLowerCase().contains("nox")
                || Build.HARDWARE.toLowerCase().contains("nox")
                || Build.PRODUCT.toLowerCase().contains("nox")
                || Build.SERIAL.toLowerCase().contains("nox"))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == Build.PRODUCT)
        return result
    }

    private fun log(str: String) {
        if (isDebug) {
            Log.d(javaClass.name, str)
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var mEmulatorDetector: Emulator? = null
        fun with(pContext: Context): Emulator? {
            if (mEmulatorDetector == null) mEmulatorDetector =
                Emulator(pContext)
            return mEmulatorDetector
        }

        val deviceInfo: String
            get() = """
                Build.PRODUCT: ${Build.PRODUCT}
                Build.MANUFACTURER: ${Build.MANUFACTURER}
                Build.BRAND: ${Build.BRAND}
                Build.DEVICE: ${Build.DEVICE}
                Build.MODEL: ${Build.MODEL}
                Build.HARDWARE: ${Build.HARDWARE}
                Build.FINGERPRINT: ${Build.FINGERPRINT}
                """.trimIndent()
    }

    init {
        mListPackageName.add("com.google.android.launcher.layouts.genymotion")
        mListPackageName.add("com.bluestacks")
        mListPackageName.add("com.bignox.app")
    }
}
