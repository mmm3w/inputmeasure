package com.mitsuki.armory.inputmeasure

import android.os.Build

/**
 * 难顶，其他机型日后再加吧
 */
internal sealed class Rom {
    abstract fun hit(): Boolean

    companion object {
        fun obtain(): Rom {
            return when {
                Xiaomi.hit() -> Xiaomi
                Huawei.hit() -> Huawei
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
            return Build.MANUFACTURER.contains("HUAWEI", true)
        }
    }

}