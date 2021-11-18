package com.mitsuki.armory.inputmeasure

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.Surface
import android.view.ViewConfiguration

fun Context.navigationBarHeight(): Int {
    //实体按键规避
    val hasHardwareButton = ViewConfiguration.get(this)
        .hasPermanentMenuKey() || KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    if (hasHardwareButton) return 0

    val id = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (id > 0) return resources.getDimensionPixelSize(id)

    return 0
}

@Suppress("DEPRECATION")
//整个屏幕的高度，横竖屏切换时与screenWidth对调
val Activity.screenHeight: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        DisplayMetrics().apply { display?.getRealMetrics(this) }.heightPixels
    } else {
        DisplayMetrics().apply { windowManager.defaultDisplay.getRealMetrics(this) }.heightPixels
    }


@Suppress("DEPRECATION")
val Activity.rotation: Int
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            display?.rotation ?: Surface.ROTATION_0
        else
            windowManager.defaultDisplay.rotation

