package com.mitsuki.armory.inputmeasure

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * 仅对29及以下版本兼容
 * 在30+版本中存在较多有关window的api增减
 * 需要使用另外的实现方式来实现
 * */
class InputMeasurePopupWindow(private val activity: AppCompatActivity) : PopupWindow(),
    ViewTreeObserver.OnGlobalLayoutListener, LifecycleObserver {

    //用于缓存键盘高度
    private val mSharedPreferences by lazy {
        activity.getSharedPreferences("m_keyboard", Context.MODE_PRIVATE)
    }

    //sp缓存中的键盘高度
    private var cachedKeyboardHeight: Int
        get() = mSharedPreferences.getInt("m_keyboard_height", 0)
        set(value) = mSharedPreferences.edit().putInt("m_keyboard_height", value).apply()

    private val currentDisplayRect by lazy { Rect() }
    private val lastDisplayRect by lazy { Rect() }
    private val screenRect by lazy { Rect(0, 0, activity.screenWidth, activity.screenHeight) }
    private val displayRect by lazy { Rect(0, 0, activity.displayWidth, activity.displayHeight) }

//    private val mDeviationHeight by lazy {
//        activity.statusBarHeight().coerceAtMost(activity.navigationBarHeight())
//    }

    var onKeyBoardEvent: ((Boolean, Int) -> Unit)? = null

    var keyboardHeight: Int = cachedKeyboardHeight
        private set


    override fun onGlobalLayout() {
        contentView.getWindowVisibleDisplayFrame(currentDisplayRect)
        Log.d(
            "InputMeasure",
            "onGlobalLayout coming ------------------------------------> $currentDisplayRect"
        )
        //过滤重复事件
        Log.d(
            "InputMeasure",
            "currentDisplayRect : $currentDisplayRect | lastDisplayRect : $lastDisplayRect " +
                    "| ${currentDisplayRect == lastDisplayRect}"
        )
        if (currentDisplayRect == lastDisplayRect) return
        Log.d(
            "InputMeasure",
            "onGlobalLayout start =====================================> $currentDisplayRect"
        )
        /**
         * 1.横屏模式在切入后台，再切回前台，部分手机会产生视图旋转。触发布局调整。
         * 2.底部导航栏状态变更会触发布局调整。
         * 3.有些时候我们并不会把app设置为全屏，计算键盘高度并不总等于物理高减可视底。
         * 4.有些机型点开软键盘后会频繁触发onGlobalLayout。
         */

        /**
         * 总共三块内容
         * 一块是屏幕高度
         * 一块是当前显示区域
         * 一块是上次显示区域
         *
         */
//        //部分设备后台旋转事件过滤
//        val min = displayRect.bottom.coerceAtMost(displayRect.right)
//        val max = displayRect.bottom.coerceAtLeast(displayRect.right)
//        //正方形分辨率
//        if (max.toDouble() / min.toDouble() >= 1.2) {
//            when (activity.requestedOrientation) {
//                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
//                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> {
//                    if (displayRect.right > displayRect.bottom) return
//                }
//                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
//                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> {
//                    if (displayRect.bottom > displayRect.right) return
//                }
//            }
//        }
//

//
//        //横屏的时候导航栏在侧边
//        val isShowNavigation =
//            (0 == (activity.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)) ||
//                    displayRect.height() != screenRect.height()
//        val navigationBarHeight = when (activity.rotation) {
//            Surface.ROTATION_0, Surface.ROTATION_180 -> if (isShowNavigation) context.navigationBarHeight() else 0
//            else -> 0
//        }
//
//        // 先不管导航栏显示不显示，先减去导航栏的高度
//        // 这个高度是最小限度，在输入法没有显示的时候，当前显示矩阵的bottom无论如何都不会小于这个高度
//        // 这个其实是用来处理物理导航栏
//        val excludeNavigation = screenRect.bottom - navigationBarHeight
//        // 依据上个高度和当前矩阵的高度获取一个高度差，因为上面的条件的原因
//        // 当 currentHeightDiff >= 0 的时候软键盘可能隐藏，否则软键盘可能处于显示状态
//        // 还要综合判断是不是状态栏和导航栏改变导致的
//        // 如果存在虚拟导航栏的时候，不显示输入法是一般都是为0，但是是物理导航栏的话，存在导航栏高度，那么这个值会大于0
//        val currentHeightDiff = currentDisplayRect.bottom - excludeNavigation
//        //前后两次显示矩阵的高度差
//        val aroundHeightDiff = currentDisplayRect.bottom - lastDisplayRect.bottom
//
//        if (
//            (currentHeightDiff >= 0 && aroundHeightDiff <= mDeviationHeight) ||//两次高度变动属于状态栏或导航栏的变动
//            (currentHeightDiff >= 0 && currentDisplayRect.bottom < excludeNavigation) || //当前的高度在最低线内部
//            (currentHeightDiff < 0 && currentDisplayRect.bottom >= (screenRect.bottom - mDeviationHeight) && excludeNavigation != 0) //存在一个最低的显示高度
//        ) {
//            //这些都是状态栏或者导航栏发生变化的事件过滤
//            return
//        }
//
//        keyboardHeight =
//            if (currentHeightDiff == 0) screenRect.bottom - lastDisplayRect.bottom - navigationBarHeight
//            else screenRect.bottom - currentDisplayRect.bottom - navigationBarHeight
//
////        if (currentHeightDiff >= 0 && !isFirst) {
////            isFirst = true
////        } else {
////            onKeyBoardEvent?.invoke(currentHeightDiff < 0, keyboardHeight)
////        }
//
        lastDisplayRect.set(currentDisplayRect)
        Log.d(
            "InputMeasure",
            "onGlobalLayout end =======================================> $lastDisplayRect"
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onLifeCreate() {
        Log.d(
            "InputMeasure",
            "onLifeCreate"
        )
        //配置基础内容
        val contentView = View(activity)
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED
        contentView.viewTreeObserver.addOnGlobalLayoutListener(this)
        setContentView(contentView)

        activity.window.decorView.post {
            showAtLocation(
                activity.window.decorView,
                Gravity.NO_GRAVITY,
                0,
                0
            )
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifeResume() {
        Log.d(
            "InputMeasure",
            "onLifeResume"
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifeDestroy() {
        dismiss()
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }


}