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
class StickRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView(context, attrs, defStyle) {
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
        val findFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val findFirstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        var floatView: View? = null
        var nowShow = false
        var translateY = 0F

        if (adapter.isFloatType(findFirstCompletelyVisibleItemPosition) && adapter.isFloatMembers(findFirstVisibleItemPosition)) {
            nowShow = true
            floatView = getLastFloat(adapter, findFirstVisibleItemPosition)
            val memberCount = findFirstCompletelyVisibleItemPosition - findFirstVisibleItemPosition
            val nextfloat = getChildAt(memberCount)
            val nextFloatTop = nextfloat.top
            val nextFloatHeight = nextfloat.height
            if (nextFloatTop <= nextFloatHeight) {
                translateY = nextFloatTop - nextFloatHeight + 0.0F
            } else {
                translateY = 0.0F
            }
        } else if (adapter.isFloatMembers(findFirstCompletelyVisibleItemPosition) || adapter.isFloatMembers(findFirstVisibleItemPosition)) {
            nowShow = true
            translateY = 0.0F
            floatView = getLastFloat(adapter, findFirstVisibleItemPosition)
        } else {
            nowShow = false
        }


        val parentGroup = parent as ViewGroup
        val childCount = parentGroup.childCount
        var wrap: StickWrapFrameLayout? = null
        for (i in 0 until childCount) {
            val childAt = parentGroup.getChildAt(i)
            if (childAt is StickWrapFrameLayout) {
                wrap = childAt
                break
            }
        }

        if (nowShow) {
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
                floatView.translationY = translateY
                wrap.removeAllViews()
                wrap.addView(floatView)
            }

        } else {
            if (wrap != null) {
                wrap.removeAllViews()
                wrap.visibility = View.GONE
            }
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

class StickWrapFrameLayout(context: Context, attrs: AttributeSet?, defStyle: Int) : FrameLayout(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
}