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

    private val currentDisplayRect by lazy { Rect() }

    //界面会被销毁，所以lastDisplayRect不一定是最后一次的，如果在销毁之前
    private val lastDisplayRect by lazy { Rect() }
//    private val screenRect by lazy { Rect(0, 0, activity.screenWidth, activity.screenHeight) }
//    private val displayRect by lazy { Rect(0, 0, activity.displayWidth, activity.displayHeight) }

    private val navigationBarHeight = activity.navigationBarHeight()


    /**
     * 注：在activity旋转的时候，在上一个activity销毁之前会概率调用该方法，并且显示的区域会是旋转之后的区域，会和界面重建后调用的该方法重复
     *
     *
     */
    override fun onGlobalLayout() {
        contentView.getWindowVisibleDisplayFrame(currentDisplayRect)
        Log.d(
            "InputMeasure",
            "onGlobalLayout coming ------------------------------------> $currentDisplayRect"
        )
        Log.d(
            "InputMeasure",
            "currentDisplayRect : $currentDisplayRect | lastDisplayRect : $lastDisplayRect " +
                    "| ${currentDisplayRect == lastDisplayRect}"
        )
        //过滤重复事件，但是当出现备注中旋转的情况时，该重复事件需要其他方法来过滤
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

        //键盘的高度不能够通过前后两次显示矩阵的差来直接计算
        //因为在部分手机中该方法会有奇怪的调用，会有奇怪的矩阵导致该方法获得的高度并不可信
        //首先要确定一条bottom基线，以这条基线为基准计算通过当前矩阵的bottom来计算高度

        //首先影响基线的位置的有：
        //1、导航栏的状态，在隐藏导航栏的情况下，布局是延伸至底部的，此时的基线应为屏幕的最地下
        //2、横屏情况下，导航栏在侧面，应也不在计算范围内
        //3、部分拥有物理导航栏的设备存在activity.navigationBarHeight的值，但是实际并不占用屏幕
        //4、不同机型手机在启用全屏手势的情况下导航栏的高度并不一样(注：原生全屏手势是存在导航栏高度的，而部分华为手机则没有)

        //目前已知screenRect为全屏幕高度
        //displayRect与navigationBarHeight在不同设备上表现并不一样

        //比如在一台小米8手机中，displayRect表现出的navigationBarHeight值为132
        //而实际navigationBarHeight的值为44，在应用中观察也应该是44
        //但是在弹出输入法后表现出的navigationBarHeight值应该为132
        //注；这台手机的displayRect减去了状态栏的高度

        //再比如在一台使用全屏手势的华为平板中，displayRect表现出的navigationBarHeight值为0
        //在应用中观察应该也为0，但是取出的navigationBarHeight的值为106
        //在弹出输入法后navigationBarHeight表现出来的值为0

        //在小米8lite这台拥有三个虚拟案件导航栏中，displayRect的表现与navigationBarHeight的取值一致
        //在Xperia5使用全屏手势的情况下，displayRect的表现与navigationBarHeight的取值一致
        //然后在这台手机开启手势后一切都变了，所有值都不准了

        //还有存在实体按键的手机设备没有尝试过。

        //有些设备厂偷懒在导航方式改变时不会去变更navigationBarHeight
        //有些设备厂在displayRect中会减去刘海的高度（小部分）

        //先尝试计算一个正确的导航栏高度
        //直接根据手机型号适配，因为单纯通过值的计算无法考虑到部分情况
        //将屏幕的方向也考虑进去
        val sourceNavigationBarHeight: Int = NavigationBar.withRotation(activity, activity.rotation)

        val isShowNavigation =
            (0 == (activity.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION))
        //最后实际会参与计算的高度
        val realCalcNavigationBarHeight = if (isShowNavigation) sourceNavigationBarHeight else 0
        Log.d("asdf", "========> $realCalcNavigationBarHeight <========")

        //找到基线
        val baseHeight = activity.screenHeight - realCalcNavigationBarHeight


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
//        val aroundHeightDiff = lastDisplayRect.bottom - currentDisplayRect.bottom

//        if (
//            (currentHeightDiff >= 0 && aroundHeightDiff <= currentNavigationBarHeight) ||//两次高度变动属于状态栏或导航栏的变动
//            (currentHeightDiff >= 0 && currentDisplayRect.bottom < excludeNavigation) || //当前的高度在最低线内部
//            (currentHeightDiff < 0 && currentDisplayRect.bottom >= (screenRect.bottom - currentNavigationBarHeight) && excludeNavigation != 0) //存在一个最低的显示高度
//        ) {
//            //这些都是状态栏或者导航栏发生变化的事件过滤
//            return
//        }

//        val keyboardHeight =
//            if (currentHeightDiff == 0) screenRect.bottom - lastDisplayRect.bottom - navigationBarHeight
//            else screenRect.bottom - currentDisplayRect.bottom - navigationBarHeight

        //直接使用基线计算高度
        val keyboardHeight = baseHeight - currentDisplayRect.bottom
        InputHeight.intercept(if (keyboardHeight < 0) 0 else keyboardHeight)

        lastDisplayRect.set(currentDisplayRect)
        Log.d(
            "InputMeasure",
            "onGlobalLayout end =======================================> $lastDisplayRect"
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onLifeCreate() {
        //配置基础内容
        val contentView = View(activity)
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        @Suppress("DEPRECATION")
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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifeDestroy() {
        dismiss()
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }


}