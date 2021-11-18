package com.mitsuki.armory.inputmeasure

import android.content.Context
import android.provider.Settings
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.Surface
import android.view.ViewConfiguration

internal object NavigationBar {
    fun size(context: Context): Int {
        return when (Rom.obtain()) {
            Rom.Xiaomi -> {
                val isBarInvisible =
                    Settings.Global.getInt(context.contentResolver, "force_fsg_nav_bar", 0) != 0
                if (isBarInvisible) 0 else context.navigationBarHeight()
            }
            Rom.Huawei -> {
                val isBarInvisible =
                    Settings.Global.getInt(context.contentResolver, "navigationbar_is_min", 0) != 0
                if (isBarInvisible) 0 else context.navigationBarHeight()
            }
            else -> context.navigationBarHeight()
        }
    }

    fun withRotation(context: Context, rotation: Int): Int {
        return when (Rom.obtain()) {
            Rom.Xiaomi -> {
                val isBarInvisible =
                    Settings.Global.getInt(context.contentResolver, "force_fsg_nav_bar", 0) != 0
                if (isBarInvisible) 0 else (if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) context.navigationBarHeight() else 0)
            }
            Rom.Huawei -> {
                val isBarInvisible =
                    Settings.Global.getInt(context.contentResolver, "navigationbar_is_min", 0) != 0
                if (isBarInvisible) 0 else (if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) context.navigationBarHeight() else 0)
            }
            else -> if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) context.navigationBarHeight() else 0
        }
    }
}