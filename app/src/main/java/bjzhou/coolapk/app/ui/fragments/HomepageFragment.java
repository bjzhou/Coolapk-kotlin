package bjzhou.coolapk.app.ui.fragments;

import android.graphics.Rect;
import android.os.Bundle;
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
import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.net.ApiManager;
import bjzhou.coolapk.app.ui.adapters.ApkListAdapter;
import bjzhou.coolapk.app.widget.LoadMoreDecoration;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by bjzhou on 14-7-29.
 */
public class HomepageFragment extends Fragment implements Observer<List<Apk>> {

    private static final String TAG = "HomepageFragment";
    private String mQuery = null;
    private List<Apk> mApkList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    private ApkListAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int mPage = 0;

    private LinearLayoutManager mLayoutManager;
    private int mInsets;
    private LoadMoreDecoration mLoadMoreDecoration;

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
        mLoadMoreDecoration = new LoadMoreDecoration();
        mLoadMoreDecoration.setListener(new LoadMoreDecoration.Listener() {
            @Override
            public void onLoadMore() {
                obtainApkList(++mPage);
            }
        });
        mRecyclerView.addItemDecoration(mLoadMoreDecoration);

        mAdapter = new ApkListAdapter(getActivity(), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.theme_default_primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadMoreDecoration.reset();
                mPage = 0;
                obtainApkList(++mPage);
            }
        });

        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPage = 0;
                obtainApkList(++mPage);
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
            ApiManager.getInstance().obtainHomepageApkList(page).subscribeWith(this);
        } else {
            ApiManager.getInstance().obtainSearchApkList(URLEncoder.encode(mQuery), page).subscribeWith(this);
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(List<Apk> apks) {
        if (mPage == 1) {
            mApkList = apks;
            mAdapter.setApkList(mApkList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mPage++;
            if (apks == null || apks.size() == 0) {
                mLoadMoreDecoration.loadComplete();
                return;
            }
            mApkList.addAll(apks);
            mAdapter.setApkList(mApkList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        onComplete();
    }

    @Override
    public void onComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
