package com.mitsuki.armory.inputmeasure

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.Surface

fun Context.navigationBarHeight(): Int {
    val id = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (id > 0) return resources.getDimensionPixelSize(id)
    return 0
}

fun Context.statusBarHeight(): Int {
    val id = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (id > 0)
        return resources.getDimensionPixelSize(id)
    return 0
}

/** 29 以下使用 包含过时API **********************************************************************/
@Suppress("DEPRECATION")
//整个屏幕的宽度，横竖屏切换时与screenHeight对调
val Activity.screenWidth: Int
    get() = DisplayMetrics().apply { windowManager.defaultDisplay.getRealMetrics(this) }.widthPixels

@Suppress("DEPRECATION")
//整个屏幕的高度，横竖屏切换时与screenWidth对调
val Activity.screenHeight: Int
    get() = DisplayMetrics().apply { windowManager.defaultDisplay.getRealMetrics(this) }.heightPixels

@Suppress("DEPRECATION")
//显示区域的宽度，横竖屏切换时与displayHeight对调
val Activity.displayWidth: Int
    get() = DisplayMetrics().apply { windowManager.defaultDisplay.getMetrics(this) }.widthPixels

@Suppress("DEPRECATION")
//显示区域的高度，横竖屏切换时与displayWidth对调，固定减去导航栏的高度，无论导航栏是否显示
//使用手势导航的时候也会减去导航栏的高度
//不过部分手机的在手势导航的时候导航栏的高度是0，而原生的手势存在一个较小的高度底栏
val Activity.displayHeight: Int
    get() = DisplayMetrics().apply { windowManager.defaultDisplay.getMetrics(this) }.heightPixels

@Suppress("DEPRECATION")
val Activity.rotation: Int
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            display?.rotation ?: Surface.ROTATION_0
        else
            windowManager.defaultDisplay.rotation

