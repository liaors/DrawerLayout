package com.rs.mylibrary

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper


class DoubleDrawerLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs) {
    private var contentView: View? = null
    private var firstMenuView: View? = null
    private var secondMenuView: View? = null
    private var viewDragHelper: ViewDragHelper? = null
    private var firstMenuStateCallBack: FirstMenuStateCallBack? = null
    private var secondMenuStateCallBack: SecondMenuStateCallBack? = null
    private var firstMenuLock = false
    private var secondMenuLock = false

    /**
     * drawer显示出来的占自身的百分比
     */
    private var firstMenuOnScreen = 1.0f
    private var secondMenuOnScreen = 1.0f

    init {
        init()
    }

    private fun init() {
        val density = resources.displayMetrics.density
        val minVel = MIN_FLING_VELOCITY * density
        viewDragHelper = ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                log("tryCaptureView")
                return child === firstMenuView && !firstMenuLock || child === secondMenuView && !secondMenuLock
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                log("clampViewPositionHorizontal")
                val leftBound = width - child.width
                val rightBound = width
                return left.coerceAtLeast(leftBound).coerceAtMost(rightBound)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                log("onViewReleased")
                val childWidth = releasedChild.width
                val offset = (childWidth - releasedChild.left) * 1.0f / childWidth
                val width = width
                viewDragHelper!!.settleCapturedViewAt(
                    if (xvel < 0 || xvel == 0f && offset > 0.5f) width - childWidth else width,
                    releasedChild.top
                )
                invalidate()
            }

            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                val childWidth = changedView.width
                val offset = (width - left).toFloat() / childWidth
                if (changedView === firstMenuView) {
                    firstMenuOnScreen = offset
                    if (firstMenuStateCallBack != null) {
                        if (firstMenuOnScreen == 0.0f) {
                            firstMenuStateCallBack!!.firstMenuStateCallBack(false)
                            firstMenuOnScreen = 1.0f
                        } else if (firstMenuOnScreen == 1.0f) {
                            firstMenuStateCallBack!!.firstMenuStateCallBack(true)
                            firstMenuOnScreen = 0.0f
                        }
                    }
                } else if (changedView === secondMenuView) {
                    secondMenuOnScreen = offset
                    if (secondMenuStateCallBack != null) {
                        if (secondMenuOnScreen == 0.0f) {
                            secondMenuStateCallBack!!.secondMenuStateCallBack(false)
                            secondMenuOnScreen = 1.0f
                        } else if (secondMenuOnScreen == 1.0f) {
                            secondMenuStateCallBack!!.secondMenuStateCallBack(true)
                            secondMenuOnScreen = 0.0f
                        }
                    }
                }
                changedView.visibility = if (offset == 0f) INVISIBLE else VISIBLE
                invalidate()
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                log("getViewHorizontalDragRange")
                var result = 0
                if (child === firstMenuView || child === secondMenuView) {
                    result = child.width
                }
                return result
            }
        })
        viewDragHelper?.minVelocity = minVel
    }

    fun openFirstDrawer() {
        if (firstMenuView == null) {
            return
        }
        val menuView: View = firstMenuView!!
        //  firstMenuOnScreen = 1.0f;
        firstMenuOnScreen = 0f
        val flag = viewDragHelper!!.smoothSlideViewTo(menuView, width - menuView.width, menuView.top)
        postInvalidate()
    }

    fun closeFirstDrawer() {
        if (firstMenuView == null) {
            return
        }
        val menuView: View = firstMenuView!!
        //      firstMenuOnScreen = 0f;
        firstMenuOnScreen = 1f
        viewDragHelper!!.smoothSlideViewTo(menuView, width, menuView.top)
        postInvalidate()
    }

    fun openSecondDrawer() {
        if (secondMenuView == null) {
            return
        }
        val menuView: View = secondMenuView!!
        //    secondMenuOnScreen = 1.0f;
        secondMenuOnScreen = 0f
        viewDragHelper!!.smoothSlideViewTo(menuView, width - menuView.width, menuView.top)
        postInvalidate()
        if (firstMenuStateCallBack != null) {
            firstMenuStateCallBack!!.firstMenuStateCallBack(false)
        }
    }

    fun closeSecondDrawer() {
        if (secondMenuView == null) {
            return
        }
        val menuView: View = secondMenuView!!
        //  secondMenuOnScreen = 0f;
        secondMenuOnScreen = 1f
        viewDragHelper!!.smoothSlideViewTo(menuView, width, menuView.top)
        postInvalidate()
        if (secondMenuStateCallBack != null) {
            secondMenuStateCallBack!!.secondMenuStateCallBack(false)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        log("onMeasure")
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
        if (childCount >= 2 && getChildAt(1) != null) {
            val firstView = getChildAt(1)
            val lp = firstView.layoutParams as MarginLayoutParams
            val drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width)
            val drawerHeightSpec =
                getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height)
            firstView.measure(drawerWidthSpec, drawerHeightSpec)
            firstMenuView = firstView
        }
        if (childCount >= 3 && getChildAt(2) != null) {
            val secondView = getChildAt(2)
            val lp = secondView.layoutParams as MarginLayoutParams
            val drawerWidthSpec =
                getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width)
            val drawerHeightSpec =
                getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height)
            secondView.measure(drawerWidthSpec, drawerHeightSpec)
            secondMenuView = secondView
        }
        val mainView = getChildAt(0)
        val lp = mainView.layoutParams as MarginLayoutParams
        val contentWidthSpec = MeasureSpec.makeMeasureSpec(
            widthSize - lp.leftMargin - lp.rightMargin,
            MeasureSpec.EXACTLY
        )
        val contentHeightSpec = MeasureSpec.makeMeasureSpec(
            heightSize - lp.topMargin - lp.bottomMargin,
            MeasureSpec.EXACTLY
        )
        mainView.measure(contentWidthSpec, contentHeightSpec)
        contentView = mainView
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        log("onLayout firstMenuOnScreen : $firstMenuOnScreen  secondMenuOnScreen : $secondMenuOnScreen")
        val firstView = firstMenuView
        val secondView = secondMenuView
        val mainView = contentView
        var lp = mainView!!.layoutParams as MarginLayoutParams
        mainView.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + mainView.measuredWidth, lp.topMargin + mainView.measuredHeight)
        val width = width
        if (firstView != null) {
            lp = firstView.layoutParams as MarginLayoutParams
            val menuWidth = firstView.measuredWidth
            val childLeft = width - menuWidth + (menuWidth * firstMenuOnScreen).toInt()
            firstView.layout(childLeft, lp.topMargin, childLeft + menuWidth, lp.topMargin + firstView.measuredHeight)
        }
        if (secondView != null) {
            lp = secondView.layoutParams as MarginLayoutParams
            val menuWidth = secondView.measuredWidth
            val childLeft = width - menuWidth + (menuWidth * secondMenuOnScreen).toInt()
            secondView.layout(childLeft, lp.topMargin, childLeft + menuWidth, lp.topMargin + secondView.measuredHeight)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper!!.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper!!.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (viewDragHelper!!.continueSettling(true)) {
            invalidate()
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    public override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    fun setFirstMenuStateCallBack(firstMenuStateCallBack: FirstMenuStateCallBack?) {
        this.firstMenuStateCallBack = firstMenuStateCallBack
    }

    fun setSecondMenuStateCallBack(secondMenuStateCallBack: SecondMenuStateCallBack?) {
        this.secondMenuStateCallBack = secondMenuStateCallBack
    }

    fun setFirstMenuLock(firstMenuLock: Boolean) {
        this.firstMenuLock = firstMenuLock
    }

    fun setSecondMenuLock(secondMenuLock: Boolean) {
        this.secondMenuLock = secondMenuLock
    }

    interface FirstMenuStateCallBack {
        fun firstMenuStateCallBack(isOpen: Boolean)
    }

    interface SecondMenuStateCallBack {
        fun secondMenuStateCallBack(isOpen: Boolean)
    }

    private fun log(msg: String) {}

    companion object {
        private const val MIN_FLING_VELOCITY = 400
    }
}