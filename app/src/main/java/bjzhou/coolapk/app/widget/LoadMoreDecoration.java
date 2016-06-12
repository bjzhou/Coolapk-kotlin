package bjzhou.coolapk.app.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bjzhou.coolapk.app.R;

/**
 * Created by zhoubinjia on 16/6/12.
 */
public class LoadMoreDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = LoadMoreDecoration.class.getSimpleName();
    private View mView;
    private boolean mViewInited = false;
    private Listener mListener;
    private boolean mLoadComplete = false;

    public LoadMoreDecoration() {
    }

    public LoadMoreDecoration(View customView) {
        mView = customView;
    }

    private void initLoadMoreView(RecyclerView parent) {
        if (mView == null) {
            mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_load_more, null);
        }
        mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mView.measure(widthMeasureSpec, heightMeasureSpec);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        Log.d(TAG, "LoadMoreDecoration: width height: " + mView.getMeasuredWidth() + " " + mView.getMeasuredHeight());
        mViewInited = true;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (layoutManager.findFirstVisibleItemPosition() == 0 && layoutManager.findLastVisibleItemPosition() + 1 == parent.getAdapter().getItemCount()) {
            return;
        }
        if (mLoadComplete) {
            parent.getAdapter().notifyDataSetChanged();
            return;
        }
        if (!mViewInited) {
            initLoadMoreView(parent);
        }
        if (parent.getChildCount() < 1) return;
        View childView = parent.getChildAt(parent.getChildCount() - 1);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
        int dx = parent.getPaddingLeft();
        int dy = Math.max(parent.getPaddingTop(), childView.getBottom() + params.bottomMargin);
        c.save();
        c.translate(dx, dy);
        mView.draw(c);
        c.restore();
        ViewCompat.postInvalidateOnAnimation(parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (layoutManager.findFirstVisibleItemPosition() == 0 && layoutManager.findLastVisibleItemPosition() + 1 == parent.getAdapter().getItemCount()) {
            return;
        }
        if (!mLoadComplete && parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            Log.d(TAG, "getItemOffsets: last view");
            outRect.set(0, 0, 0, mView.getMeasuredHeight());
            mListener.onLoadMore();
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void loadComplete() {
        mLoadComplete = true;
    }

    public void reset() {
        mLoadComplete = false;
    }

    public interface Listener {
        void onLoadMore();
    }
}
