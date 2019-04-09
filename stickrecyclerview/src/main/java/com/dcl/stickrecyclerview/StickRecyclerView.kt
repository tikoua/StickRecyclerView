package com.dcl.stickrecyclerview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout


/**
 * Created by dcl@yuni on 2019/4/8.
 */
class StickRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    RecyclerView(context, attrs, defStyle) {
    private var stick: Boolean

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.StickRecyclerView)
        stick = array.getBoolean(R.styleable.StickRecyclerView_stick, true)
        array.recycle()

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (stick && layoutManager != null && adapter != null) {
                    setFloatView()
                }
            }
        })
    }

    fun setStick(stick: Boolean) {
        this.stick = stick
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
        if (stick && layoutManager != null && adapter != null) {
            setFloatView()
        }
    }

    private fun setFloatView() {
        val layoutManager = layoutManager
        val adapter = adapter
        if (layoutManager !is LinearLayoutManager || adapter !is StickHelper) {
            return
        }
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val showFLoat: Boolean
        var hasLat = false
        for (i in firstCompletelyVisiblePosition - 1 downTo 0) {
            val lastFloatType = adapter.isFloatType(i)
            if (lastFloatType) {
                hasLat = true
                break
            }
        }
        showFLoat = hasLat
        val parentGroup = parent as ViewGroup
        val parentChildCount = parentGroup.childCount
        var wrap: StickWrapFrameLayout? = null
        for (i in 0 until parentChildCount) {
            val childAt = parentGroup.getChildAt(i)
            if (childAt is StickWrapFrameLayout) {
                wrap = childAt
                break
            }
        }
        if (showFLoat) {
            val floatView = getLastFloat(adapter, firstVisiblePosition)
            var translateY = 0F
            var firstInScreenFloat: View? = null
            for (i in 0 until layoutManager.childCount) {
                val isFloat = adapter.isFloatType(firstVisiblePosition + i)
                if (isFloat) {
                    firstInScreenFloat = getChildAt(i)
                    break
                }
            }

            if (wrap != null) {
                wrap.visibility = View.VISIBLE
            } else {
                wrap = StickWrapFrameLayout(context)
                val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                layoutParams.topMargin = top
                wrap.layoutParams = layoutParams
                wrap.y = top.toFloat()
                parentGroup.addView(wrap)
            }
            floatView?.let {
                wrap.removeAllViews()
                wrap.addView(floatView)
            }
            if (firstInScreenFloat == null) {
                translateY = 0F
            } else {
                floatView?.let {
                    val top = firstInScreenFloat.top
                    val widthSpec = View.MeasureSpec.makeMeasureSpec(((1 shl 30) - 1), MeasureSpec.AT_MOST)
                    val heightSpec = View.MeasureSpec.makeMeasureSpec(((1 shl 30) - 1), MeasureSpec.AT_MOST)
                    wrap.measure(widthSpec, heightSpec)
                    val floatViewHeight = wrap.measuredHeight
                    if (top in 1..(floatViewHeight - 1)) {
                        translateY = (top - floatViewHeight).toFloat()
                    }
                }
            }
            floatView?.translationY = translateY
        } else {
            wrap?.visibility = View.GONE
        }

    }

    /**
     * 生成上一个用来悬浮显示的item
     */
    private fun getLastFloat(adapter: Adapter<ViewHolder>, findFirstVisibleItemPosition: Int): View? {
        val floatAdapter = adapter as StickHelper
        for (i in findFirstVisibleItemPosition downTo 0) {
            if (floatAdapter.isFloatType(i)) {
                val viewHolder: ViewHolder = adapter.createViewHolder(this, adapter.getItemViewType(i))
                adapter.onBindViewHolder(viewHolder, i)
                return viewHolder.itemView
            }
        }
        return null
    }

}


class StickWrapFrameLayout(context: Context, attrs: AttributeSet?, defStyle: Int) :
    FrameLayout(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
}