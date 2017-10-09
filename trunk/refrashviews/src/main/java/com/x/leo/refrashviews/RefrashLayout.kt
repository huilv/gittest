package com.x.leo.refrashviews

import android.content.Context
import android.support.v4.widget.ViewDragHelper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import java.io.Serializable

/**
 * @作者:XLEO
 * @创建日期: 2017/8/25 10:51
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */

class RefrashLayout(ctx: Context, attr: AttributeSet?) : ViewGroup(ctx, attr) {
    constructor(ctx: Context) : this(ctx, null)

    private var topRefrashViewId: Int = -1
    private var bottomRefrashViewId: Int = -1
    private var mainViewId: Int = -1
    private var topRefrashView: View? = null

    private var bottomView: View? = null

    private var mainView: View? = null

    private var isTopRefrash: Boolean = false
    private var isBottomRefrash: Boolean = false
    var onRefrashListener: OnRefrashListener? = null
    val STATEDIRECTION = 0x0012.shl(3)
    val STATEIDLE = 0x0011.shl(3)
    val STATEDRAGING = 0x0013.shl(3)
    val STATENOTDRAG = 0x0014.shl(3)
    private var currentState = STATEIDLE

    private val DIR_NONE: Int = 0x1122
    private val DIR_DOWN: Int = 0x1123

    private val DIR_UP: Int = 0x1124
    private var direction: Int = DIR_NONE
    val DO_LOG = false


    val dragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View?, pointerId: Int): Boolean {
                if (child!! == mainView) {
                    return true
                }
                return false
            }

            override fun clampViewPositionVertical(child: View?, top: Int, dy: Int): Int {
                logd("dy:" + dy)
                return top
            }

            override fun clampViewPositionHorizontal(child: View?, left: Int, dx: Int): Int {
                return child!!.left
            }

            override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
                resetViews()
            }

            override fun onViewPositionChanged(changedView: View?, left: Int, top: Int, dx: Int, dy: Int) {
                logd("newtop:" + top + ";dy:" + dy)
                locateViews(top)
            }
        })

    }

    private fun locateViews(dy: Int) {
        when (dy > 0) {
            true -> {
                if (isBottomRefrash) {
                    bottomMoveBy(dy)
                } else {
                    if (topRefrashView != null) {
                        topMoveBy(dy)
                        isTopRefrash = true
                    }
                }
            }
            else -> {
                if (isTopRefrash) {
                    topMoveBy(dy)
                } else {
                    if (bottomView != null) {
                        bottomMoveBy(dy)
                        isBottomRefrash = true
                    }
                }
            }
        }
    }

    private var isReleaseing: Boolean = false

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate()
        } else {
            if (isReleaseing) {
                isBottomRefrash = false
                isTopRefrash = false
                isReleaseing = false
            }
        }
    }

    private fun resetViews() {
        logd("reset")
        isReleaseing = true
        if (mainView != null) {
            dragHelper.settleCapturedViewAt(0, 0)
        }
        invalidate()
        if (isTopRefrash) {
            onRefrashListener?.onTopRefrash()
        }
        if (isBottomRefrash) {
            onRefrashListener?.onBottomRefrash()
        }
    }

    private fun topMoveBy(dy: Int) {
        topRefrashView!!.translationY = dy.toFloat()
        logd("topview:transY:" + topRefrashView!!.translationY)
        onRefrashListener?.onTopViewMove(dy)
    }

    private fun bottomMoveBy(dy: Int) {
        bottomView!!.translationY = dy.toFloat()
        onRefrashListener?.onBottomViewMove(dy)
    }


    init {
        if (attr != null) {
            val attrs = ctx.obtainStyledAttributes(attr!!, R.styleable.RefrashLayout)
            topRefrashViewId = attrs.getResourceId(R.styleable.RefrashLayout_topView, -1)
            bottomRefrashViewId = attrs.getResourceId(R.styleable.RefrashLayout_bottomView, -1)
            mainViewId = attrs.getResourceId(R.styleable.RefrashLayout_mainView, -1)
        }
    }


    fun logd(s: String) {
        if (DO_LOG) {
            Log.d("RefrashLayout", s)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        dragHelper.shouldInterceptTouchEvent(ev)
        return true
    }


    private fun isDirectionPermited(): Boolean {
        when (mainView) {
            is AdapterView<*> -> {
                val listView = mainView as ListView
                if (listView.adapter == null && direction != DIR_NONE) {
                    currentState = STATEDRAGING
                    return true
                }
                if (listView.firstVisiblePosition == 0 && direction == DIR_DOWN) {
                    currentState = STATEDRAGING
                    return true

                }
                if (listView.lastVisiblePosition == listView.adapter.count - 1 && direction == DIR_UP) {
                    currentState = STATEDRAGING
                    return true
                }
            }
            is RecyclerView -> {
                val recycler = mainView as RecyclerView

                if (recycler.layoutManager is LinearLayoutManager) {
                    if (recycler.adapter == null && direction != DIR_NONE) {
                        currentState = STATEDRAGING
                        return true
                    }
                    val linearManager = recycler.layoutManager as LinearLayoutManager
                    //获取第一个可见view的位置
                    val firstItemPosition = linearManager.findFirstVisibleItemPosition()
                    if (firstItemPosition == 0 && direction == DIR_DOWN) {
                        currentState = STATEDRAGING
                        return true
                    }
                    //获取最后一个可见view的位置
                    val lastItemPosition = linearManager.findLastVisibleItemPosition()
                    if (lastItemPosition == recycler.adapter.itemCount - 1 && direction == DIR_UP) {
                        currentState = STATEDRAGING
                        return true
                    }

                } else if (recycler.layoutManager is StaggeredGridLayoutManager) {
                    val stagger = recycler.layoutManager as StaggeredGridLayoutManager
                    if (stagger.findViewByPosition(0).windowVisibility != View.GONE && direction == DIR_DOWN) {
                        currentState = STATEDRAGING
                        return true
                    } else if (stagger.findViewByPosition(recycler.adapter.itemCount - 1).windowVisibility != View.GONE && direction == DIR_UP) {
                        currentState = STATEDRAGING
                        return true
                    }
                }
            }
            is ViewGroup -> {
                val view = mainView as ViewGroup
                if (view.childCount <= 0) {
                    if (direction != DIR_NONE) {
                        currentState = STATEDRAGING
                        return true
                    } else {
                        currentState = STATENOTDRAG
                        return false
                    }
                }
                if (view.getChildAt(0).windowVisibility != View.GONE && direction == DIR_DOWN) {
                    currentState = STATEDRAGING
                    return true
                } else if (view.getChildAt(view.childCount - 1).windowVisibility != View.GONE && direction == DIR_UP) {
                    currentState = STATEDRAGING
                    return true
                }
            }
            else -> {
                currentState = STATEDRAGING
                return true
            }
        }
        currentState = STATENOTDRAG
        return false
    }

    private val eventLog: EventLogHolder by lazy {
        EventLogHolder()
    }

    class EventLogHolder {
        var last: MotionEventBean? = null
        var current: MotionEventBean? = null
        fun addEvent(event: MotionEvent) {
            if (current != null) {
                moveCurrentToLast()
                current!!.updateDatas(event)
            } else {
                current = MotionEventBean(event.actionMasked, event.x, event.y)
            }
        }

        private fun moveCurrentToLast() {
            if (last == null) {
                last = MotionEventBean(current!!.type, current!!.x, current!!.y)
            } else {
                last!!.updateDatas(current!!)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (isReleaseing) {
                    currentState = STATEDRAGING
                    dragHelper.processTouchEvent(event)
                } else {
                    eventLog.addEvent(event)
                    isReleaseing = false
                    mainView!!.dispatchTouchEvent(event)
                    dragHelper.processTouchEvent(event)
                    currentState = STATEDIRECTION
                    logd("down")
                }
            }
            MotionEvent.ACTION_MOVE -> {
                eventLog.addEvent(event)
                getDirection()
                if (isDirectionChanged && currentState != STATEDRAGING) {
                    isDirectionPermited()
                    when (currentState) {
                        STATEDRAGING -> {
                            dragHelper.processTouchEvent(event)
                        }
                        STATENOTDRAG -> {
                            mainView!!.dispatchTouchEvent(event)
                        }
                        else -> {
                        }
                    }
                } else if (currentState == STATEDRAGING) {
                    dragHelper.processTouchEvent(event)
                } else if (currentState == STATENOTDRAG) {
                    mainView!!.dispatchTouchEvent(event)
                }
                isReleaseing = false
                logd("move")
            }
            MotionEvent.ACTION_UP -> {
                if (currentState == STATEDRAGING) {
                    dragHelper.processTouchEvent(event)
                } else if (currentState == STATENOTDRAG) {
                    mainView!!.dispatchTouchEvent(event)
                }else{
                    mainView!!.dispatchTouchEvent(event)
                }
                currentState = STATEIDLE
                direction = DIR_NONE
                logd("up")
            }
            else -> {
                logd("else")
            }
        }
        return true
    }

    private var isDirectionChanged: Boolean = false

    private fun getDirection() {
        if (eventLog.last != null) {
            var newDirection = DIR_NONE
            val abs = Math.abs(eventLog.current!!.y - eventLog.last!!.y)
            if (abs == 0.0f) {
                newDirection = DIR_NONE
            }
            val fl = Math.abs(eventLog.current!!.x - eventLog.last!!.x) / abs

            if (fl <= 0.5f) {
                if (eventLog.last!!.y > eventLog.current!!.y) {
                    newDirection = DIR_UP
                } else {
                    newDirection = DIR_DOWN
                }
            } else {
                newDirection = DIR_NONE
            }
            if (direction != newDirection) {
                isDirectionChanged = true
                direction = newDirection
            } else {
                isDirectionChanged = false
            }
        }
    }

    private var topViewHeight: Int = 0

    private var bottomViewHeight: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        if (topRefrashView != null) {
            topViewHeight = topRefrashView!!.measuredHeight
        }
        if (bottomView != null) {
            bottomViewHeight = bottomView!!.measuredHeight
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (topRefrashView != null) {
            topRefrashView!!.layout(0, -topViewHeight, measuredWidth, 0)
        }
        if (mainView != null) {
            mainView!!.layout(0, 0, measuredWidth, measuredHeight)
        }
        if (bottomView != null) {
            bottomView!!.layout(0, measuredHeight, measuredWidth, measuredHeight + bottomViewHeight)
        }
    }

    override fun onFinishInflate() {
        if (childCount > 0) {
            for (i in 0..childCount - 1) {
                when (getChildAt(i).id) {
                    -1 -> {
                        throw IllegalArgumentException("child with NOID is not supported")
                    }
                    topRefrashViewId -> {
                        topRefrashView = getChildAt(i)
                    }
                    bottomRefrashViewId -> {
                        bottomView = getChildAt(i)
                    }
                    mainViewId -> {
                        mainView = getChildAt(i)
                    }
                    else -> {
                    }
                }
            }
        }
        super.onFinishInflate()

    }
}

interface OnRefrashListener {
    fun onTopRefrash()
    fun onBottomRefrash()
    fun onTopViewMove(transY:Int)
    fun onBottomViewMove(transY:Int)
}

abstract class OnRefrashAdapter:OnRefrashListener{
    override fun onBottomViewMove(transY: Int) {
    }

    override fun onTopViewMove(transY: Int) {
    }
}

data class MotionEventBean(var type: Int, var x: Float, var y: Float) : Serializable {
    fun updateDatas(event: MotionEvent) {
        type = event.actionMasked
        x = event.x
        y = event.y
    }

    fun updateDatas(event: MotionEventBean) {
        type = event.type
        x = event.x
        y = event.y
    }
}