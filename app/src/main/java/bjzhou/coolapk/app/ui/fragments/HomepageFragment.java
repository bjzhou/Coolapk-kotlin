package bjzhou.coolapk.app.ui.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.adapter.ApkListAdapter;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.util.Constant;

/**
 * Created by bjzhou on 14-7-29.
 */
public class HomepageFragment extends Fragment implements Handler.Callback {

    private static final String TAG = "HomepageFragment";
    private String mQuery = null;
    private List<Apk> mApkList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    private ApkListAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int mPage = 1;

    private Handler mHandler = new Handler(this);
    private LinearLayoutManager mLayoutManager;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItem;
    private boolean loading = true;
    private int previousTotal;
    private int visibleThreshold = 5;
    private int mInsets;

    public HomepageFragment() {
    }

    public static HomepageFragment newInstance() {
        return new HomepageFragment();
    }

    public static HomepageFragment newInstance(String query) {
        HomepageFragment fragment = new HomepageFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mQuery = args.getString("query");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mInsets = (int) getResources().getDimension(R.dimen.card_insets);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.apkList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(mInsets,mInsets,mInsets,mInsets);
            }
        });

        mAdapter = new ApkListAdapter(getActivity(), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading
                        && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    mSwipeRefreshLayout.setRefreshing(true);
                    obtainApkList(mPage + 1);
                    loading = true;
                }
            }
        });
        
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.theme_default_primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtainApkList(1);
            }
        });

        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                obtainApkList(1);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }, 100);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void obtainApkList(int page) {
        if (mQuery == null) {
            HttpHelper.getInstance(getActivity()).obtainHomepageApkList(page, mHandler);
        } else {
            HttpHelper.getInstance(getActivity()).obtainSearchApkList(URLEncoder.encode(mQuery), page, mHandler);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        mSwipeRefreshLayout.setRefreshing(false);
        switch (msg.what) {
            case Constant.MSG_OBTAIN_COMPLETE:
                mApkList = (List<Apk>) msg.obj;
                mAdapter.setApkList(mApkList);
                mRecyclerView.setAdapter(mAdapter);
                mPage = 1;
                return true;
            case Constant.MSG_OBTAIN_MORE_COMPLETE:
                mPage++;
                List<Apk> obj = (List<Apk>) msg.obj;
                if (obj == null || obj.size() == 0) {
                    Toast.makeText(getActivity(), "没有了", Toast.LENGTH_SHORT).show();
                }
                mApkList.addAll(((List<Apk>) msg.obj));
                mAdapter.setApkList(mApkList);
                mAdapter.notifyDataSetChanged();
                return true;
            case Constant.MSG_OBTAIN_FAILED:
                Toast.makeText(getActivity(), "Please Check Network!!!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
    }
}
