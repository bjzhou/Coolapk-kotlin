package bjzhou.coolapk.app.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by zhoubinjia on 2016/12/7.
 */

class FitWidthImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        if (drawable != null) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = Math.ceil((width.toFloat() * drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth.toFloat()).toDouble()).toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
