package bjzhou.coolapk.app.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import bjzhou.coolapk.app.R

/**
 * Created by zhoubinjia on 16/6/12.
 */
class LoadMoreDecoration : RecyclerView.ItemDecoration {
    private var mView: View? = null
    private var mViewInited = false
    private var mListener: (() -> Any)? = null
    private var mLoadComplete = false

    constructor() {}

    constructor(customView: View) {
        mView = customView
    }

    private fun initLoadMoreView(parent: RecyclerView) {
        if (mView == null) {
            mView = LayoutInflater.from(parent.context).inflate(R.layout.layout_load_more, null)
        }
        mView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredWidth, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        mView?.measure(widthMeasureSpec, heightMeasureSpec)
        mView?.layout(0, 0, mView?.measuredWidth ?: 0, mView?.measuredHeight ?: 0)
        Log.d(TAG, "LoadMoreDecoration: width height: " + mView?.measuredWidth + " " + mView?.measuredHeight)
        mViewInited = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val layoutManager = parent.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstVisibleItemPosition() == 0 && layoutManager.findLastVisibleItemPosition() + 1 == parent.adapter.itemCount) {
            return
        }
        if (mLoadComplete) {
            parent.adapter.notifyDataSetChanged()
            return
        }
        if (!mViewInited) {
            initLoadMoreView(parent)
        }
        if (parent.childCount < 1) return
        val childView = parent.getChildAt(parent.childCount - 1)
        val params = childView.layoutParams as RecyclerView.LayoutParams
        val dx = parent.paddingLeft
        val dy = Math.max(parent.paddingTop, childView.bottom + params.bottomMargin)
        c.save()
        c.translate(dx.toFloat(), dy.toFloat())
        mView?.draw(c)
        c.restore()
        ViewCompat.postInvalidateOnAnimation(parent)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        val layoutManager = parent.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstVisibleItemPosition() == 0 && layoutManager.findLastVisibleItemPosition() + 1 == parent.adapter.itemCount) {
            return
        }
        if (!mLoadComplete && parent.getChildAdapterPosition(view) == parent.adapter.itemCount - 1) {
            Log.d(TAG, "getItemOffsets: last view")
            outRect.set(0, 0, 0, mView?.measuredHeight ?: 0)
            mListener?.invoke()
        }
    }

    fun setListener(listener: () -> Any) {
        mListener = listener
    }

    fun loadComplete() {
        mLoadComplete = true
    }

    fun reset() {
        mLoadComplete = false
    }

    companion object {
        private val TAG = LoadMoreDecoration::class.java.simpleName
    }
}
