package com.rs.mylibrary

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Scroller
import com.rs.drawerlayoutLib.R
import kotlin.math.abs

/**
 * description: 搜索的view
 * @author liaorongsheng
 * @2023/11/07
 */
class HalfOpenDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private var rightView: View? = null
    private var leftView:View? = null
    // 右侧布局的宽
    private var rightWidth = 0
    // 左侧布局的宽
    private var leftWidth = 0
    private var scroller: Scroller = Scroller(getContext())
    private var downX = 0
    private var downY = 0
    private var mOpen = false
    // 打开与关闭的动画时长
    private var duration = DEFAULT_DURATION
    // 左侧布局对应百分比
    private var leftPercent = 0.4f
    private var listener: ((isOpen: Boolean) -> Unit)? = null
    // 右侧布局中有一条分割线，滚动的距离需要加上这条分割线的宽度
    private var lineWidth = 0

    companion object{
        // 默认动画时间1s
        private const val DEFAULT_DURATION = 1000
        private const val TAG = "XLSearchView"
    }
    init {
        val obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.HalfOpenDrawerLayout)
        duration = obtainStyledAttributes.getInteger(R.styleable.HalfOpenDrawerLayout_animationDuration,DEFAULT_DURATION)
        leftPercent =  obtainStyledAttributes.getFloat(R.styleable.HalfOpenDrawerLayout_leftPercent,0.4f)
        val screenWidth: Int = getScreenWidth()
        rightWidth = screenWidth
        leftWidth = (screenWidth * leftPercent).toInt()
        lineWidth = 0
    }


    /**
     * 当1级的子view全部加载完调用，可以用初始化子view的引用
     * 注意，这里无法获取子view的高
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        leftView = getChildAt(0)
        rightView = getChildAt(1)
        val layoutParams = leftView?.layoutParams
        layoutParams?.width = leftWidth
        leftView?.layoutParams = layoutParams

        val rightLayoutParams = rightView?.layoutParams
        rightLayoutParams?.width = rightWidth
        rightView?.layoutParams = rightLayoutParams
    }

    fun setLeftWidth(){
//        val rightLayoutParams = rightView?.layoutParams
//        if (rightWidth != rightLayoutParams?.width) {
//            rightLayoutParams?.width = rightWidth
//            rightView?.layoutParams = rightLayoutParams
//        }
    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        // 有点性能问题，待后续有时间优化，先用onFinishInflate中的逻辑替代
////        measureChildren(widthMeasureSpec,heightMeasureSpec)
//    }
//    override fun measureChildren(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val child = getChildAt(0)
//        measureChild(child, widthMeasureSpec, heightMeasureSpec)
//    }
//
//    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
//        val lp: LayoutParams = child.layoutParams as LayoutParams
//        val childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, 0, leftWidth)
//        // 子控件的高度 = 父控件的高度 - 父控件的上下padding
//        val childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, 0, lp.height - paddingTop - paddingBottom)
//        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
//    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x.toInt()
                downY = ev.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX: Int = (ev.x - downX).toInt()
                if (abs(deltaX) > 8 && abs(deltaX) > abs(ev.y - downY)) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    // 大于1/5打开， 小于4/5关闭，后续根据产品可以要求去调整开关的阈值
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> downX = event.x.toInt()
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x.toInt()
                val deltaX = moveX - downX
                var newScrollX = scrollX - deltaX
                if (newScrollX > leftWidth) {
                    newScrollX = leftWidth
                }
                if (newScrollX < 0) {
                    newScrollX = 0
                }
                scrollTo(newScrollX, 0)
                downX = moveX
            }
            MotionEvent.ACTION_UP -> if (isOpen() && scrollX < leftWidth * 4 / 5) {
                close()
            } else if (scrollX > leftWidth / 5) {
                open()
            } else {
                close()
            }
        }
        return true
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        rightView?.layout(leftWidth, paddingTop, leftWidth + rightWidth, paddingTop + rightView!!.measuredHeight)
        leftView?.layout(0, paddingTop, leftWidth, paddingTop + b)
    }


    /**
     * Scroller不主动去调用这个方法
     * invalidate->draw->computeScroll
     */
    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) { // 返回true,表示动画没结束
            scrollTo(scroller.currX, 0)
            invalidate()
        }
    }

    fun isOpen(): Boolean {
        return mOpen
    }

    fun close() {
        mOpen = false
        if (scrollX != 0) {
            // 滑动的时间 = 全过程动画时间 * 当前需要滚动的距离/ 全过程动画的的距离
            val time = duration * scrollX / (leftWidth + lineWidth)
            scroller.startScroll(scrollX, 0, -scrollX, 0, abs(time))
            invalidate()
        }
        listener?.invoke(mOpen)
    }

    fun open() {
        mOpen = true
        // 滑动的时间 = 全过程动画时间 * 当前需要滚动的距离/ 全过程动画的的距离
        val time = duration * (leftWidth + lineWidth - scrollX) / (leftWidth + lineWidth)
        scroller.startScroll(scrollX, 0, leftWidth + lineWidth - scrollX, 0, abs(time))
        invalidate()
        listener?.invoke(mOpen)
    }

    fun switch() {
        if (scrollX == 0) {
            open()
        } else {
            close()
        }
    }

    fun setStatusChangeListener(statusChangeListener: (isOpen: Boolean) -> Unit) {
        this.listener = statusChangeListener
    }

    private fun getScreenWidth():Int{
        val dm = DisplayMetrics()
        val wm = context.getSystemService("window") as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }
}