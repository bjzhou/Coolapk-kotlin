package bjzhou.coolapk.app.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.adapter.ApkListAdapter;
import bjzhou.coolapk.app.custom.PullToRefreshFragment;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.util.Constant;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjzhou on 14-7-29.
 */
public class HomepageFragment extends PullToRefreshFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = "HomepageFragment";
    private String mQuery = null;
    private List<Apk> mApkList = new ArrayList<Apk>();
    private ListView mListView;

    private ApkListAdapter mAdapter;

    private int mPage = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_OBTAIN_COMPLETE:
                    mApkList = (List<Apk>) msg.obj;
                    mAdapter.setApkList(mApkList);
                    mListView.setAdapter(mAdapter);
                    mPage = 1;
                    break;
                case Constant.MSG_OBTAIN_MORE_COMPLETE:
                    mPage ++;
                    List<Apk> obj = (List<Apk>) msg.obj;
                    if (obj == null || obj.size() == 0) {
                        Toast.makeText(getActivity(), "没有了", Toast.LENGTH_SHORT).show();
                    }
                    mApkList.addAll(((List<Apk>)msg.obj));
                    mAdapter.setApkList(mApkList);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            getPullToRefreshLayout().setRefreshComplete();
        }
    };


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

    public HomepageFragment() {
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

        mListView = (ListView) rootView.findViewById(R.id.apkList);
        mListView.setOnItemClickListener(this);

        mAdapter = new ApkListAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        mListView.setOnScrollListener(this);

        obtainApkList(1);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(R.string.title_section1);
        getPullToRefreshLayout().setRefreshing(true);
    }

    @Override
    protected void onActionBarClick() {
        mListView.smoothScrollToPosition(0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.container, AppViewFragment.newInstance((int)id), "AppView")
                .addToBackStack("AppView")
                .commit();
    }


    @Override
    public void onRefreshStarted(View view) {
        obtainApkList(1);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    // reached the end of the view
                    getPullToRefreshLayout().setRefreshing(true);
                    obtainApkList(mPage + 1);
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void obtainApkList(int page) {
        if (mQuery == null) {
            HttpHelper.getInstance(getActivity()).obtainHomepageApkList(page, mHandler);
        } else {
            HttpHelper.getInstance(getActivity()).obtainSearchApkList(URLEncoder.encode(mQuery), page, mHandler);
        }
    }
}
