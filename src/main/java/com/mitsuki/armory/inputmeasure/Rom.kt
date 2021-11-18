package com.mitsuki.armory.inputmeasure

import android.os.Build
import android.util.Log
import java.io.BufferedInputStream
import java.io.InputStream
import java.lang.Exception
import java.util.*

/**
 * 难顶，其他机型日后再加吧
 */
sealed class Rom {
    abstract fun hit(): Boolean

    companion object {
        fun obtain(): Rom {
            return when {
                Xiaomi.hit() -> Xiaomi
                Huawei.hit() -> Huawei
                Oppo.hit() -> Oppo
                Vivo.hit() -> Vivo
                Flyme.hit() -> Flyme
                else -> Plain
            }
        }
    }

    object Plain : Rom() {
        override fun hit(): Boolean {
            return true
        }
    }

    object Xiaomi : Rom() {
        override fun hit(): Boolean {
            return Build.MANUFACTURER.contains("Xiaomi", true)
        }
    }

    object Huawei : Rom() {
        override fun hit(): Boolean {
            if (Build.MANUFACTURER.contains("HUAWEI", true)) return true
            if (getProp("ro.build.version.emui").trim().isNotEmpty()) return true
            return false
        }
    }

    object Oppo : Rom() {
        override fun hit(): Boolean {
            return getProp("ro.build.version.opporom").trim().isNotEmpty()
        }
    }

    object Vivo : Rom() {
        override fun hit(): Boolean {
            return getProp("ro.vivo.os.version").trim().isNotEmpty()
        }
    }

    object Flyme : Rom() {
        override fun hit(): Boolean {
            return Build.DISPLAY.toUpperCase(Locale.getDefault()) == "FLYME" ||
                    Build.MANUFACTURER.contains("Meizu", true)
        }
    }

    internal fun getProp(name: String): String {
        try {
            Runtime.getRuntime().exec("getprop $name").inputStream.use { inputStream ->
                inputStream.buffered().use { bufferedInputStream ->
                    bufferedInputStream.reader().use { reader ->
                        return reader.readText()
                    }
                }
            }
        } catch (inn: Exception) {
            return ""
        }
    }

}