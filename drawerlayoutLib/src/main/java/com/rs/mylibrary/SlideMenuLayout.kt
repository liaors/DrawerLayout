package com.rs.mylibrary

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Scroller

/**
 * @author liaorongsheng
 */
class SlideMenuLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private var menuView: View? = null
    private var mainView: View? = null
    private var menuWidth = 0
    private var scroller: Scroller? = null
    var isOpen = false
    private var downX = 0

    init {
        scroller = Scroller(context)
    }
    companion object{
        private const val ANIM_DURATION = 400
        private const val AUTO_OPEN_THRESHOLD = 0.2f
    }



    /**
     * 当1级的子view全部加载完调用，可以用初始化子view的引用
     * 注意，这里无法获取子view的高
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        menuView = getChildAt(0)
        mainView = getChildAt(1)
        menuWidth = menuView!!.layoutParams.width
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> downX = ev.x.toInt()
            MotionEvent.ACTION_MOVE -> {
                val deltaX = (ev.x - downX).toInt()
                if (Math.abs(deltaX) > 8) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * l: 当前子view的左边在父view的坐标系中的x坐标
     * t: 当前子view的顶边在父view的坐标系中的y坐标
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        menuView!!.layout(r, 0, r + menuWidth, menuView!!.measuredHeight)
        mainView!!.layout(0, 0, r, b)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> downX = event.x.toInt()
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x.toInt()
                val deltaX = moveX - downX
                var newScrollX = scrollX - deltaX
                if (newScrollX > menuWidth) newScrollX = menuWidth
                if (newScrollX < 0) newScrollX = 0
                scrollTo(newScrollX, 0)
                downX = moveX
            }

            MotionEvent.ACTION_UP -> if (isOpen && scrollX < menuWidth * (1 - AUTO_OPEN_THRESHOLD)) {
                closeMenu()
            } else if (scrollX > menuWidth * AUTO_OPEN_THRESHOLD) {
                openMenu()
            } else {
                closeMenu()
            }
        }
        return true
    }

    private fun closeMenu() {
        isOpen = false
        scroller!!.startScroll(scrollX, 0, -scrollX, 0, ANIM_DURATION)
        invalidate()
    }

    fun openMenu() {
        isOpen = true
        scroller!!.startScroll(scrollX, 0, menuWidth - scrollX, 0, ANIM_DURATION)
        invalidate()
    }

    /**
     * Scroller不主动去调用这个方法
     * invalidate->draw->computeScroll
     */
    override fun computeScroll() {
        super.computeScroll()
        if (scroller!!.computeScrollOffset()) { //返回true,表示动画没结束
            scrollTo(scroller!!.currX, 0)
            invalidate()
        }
    }

    /**
     * 切换菜单的开和关
     */
    fun switchMenu() {
        if (scrollX == 0) {
            openMenu()
        } else {
            closeMenu()
        }
    }

    fun clickClose() {
        if (scrollX != 0) {
            closeMenu()
        }
    }
}