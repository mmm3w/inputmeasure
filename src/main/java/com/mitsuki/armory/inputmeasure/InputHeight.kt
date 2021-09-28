package com.mitsuki.armory.inputmeasure

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

object InputHeight {
    //用于缓存键盘高度
    private lateinit var mSharedPreferences: SharedPreferences

    var call: ((Int) -> Unit)? = null

    //sp缓存中的键盘高度
    var storeKeyboardHeight: Int
        get() = mSharedPreferences.getInt("m_keyboard_height", 0)
        internal set(value) = mSharedPreferences.edit().putInt("m_keyboard_height", value).apply()

    private var cachedKeyboardHeight: Int = 0

    //当前界面的键盘高度，在键盘隐藏的时候高度为0
    var keyboardHeight: Int = 0
        internal set

    fun init(context: Context) {
        mSharedPreferences = context.getSharedPreferences("m_keyboard", Context.MODE_PRIVATE)
        cachedKeyboardHeight = storeKeyboardHeight
        keyboardHeight = storeKeyboardHeight
    }

    fun bindMeasure(activity: AppCompatActivity) {
        activity.lifecycle.addObserver(InputMeasurePopupWindow(activity))
    }

    internal fun intercept(height: Int) {
        if (keyboardHeight == height) return
        keyboardHeight = height
        if (keyboardHeight > 0 && height != cachedKeyboardHeight) {
            cachedKeyboardHeight = height
            storeKeyboardHeight = height
        }
        //然后对外分发高度
        Log.d("InputHeight", "--------> $keyboardHeight <--------")
        call?.invoke(keyboardHeight)
    }
}