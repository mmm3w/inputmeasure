package com.mitsuki.armory.inputmeasure

import android.view.View

interface ReferValue {

    fun translationYTarget(): View

    fun updateTargetHeight(tH: Int)

    fun updateTargetTranslationY(tY: Float)

    fun referTranslationY(): Float

    fun referHeight(): Float

    val currentDisplayHeight get() = referHeight() - referTranslationY()
}