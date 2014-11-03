package bjzhou.coolapk.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.adapter.UpgradeAdapter;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeFragment extends Fragment {

    public static final int INSTALL_REQUEST_CODE = 101;
    private static final String TAG = "UpgradeFragment";

    private UpgradeAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<UpgradeApkExtend> mUpgradeList = new ArrayList<UpgradeApkExtend>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_OBTAIN_COMPLETE:
                    mUpgradeList = (List<UpgradeApkExtend>) msg.obj;
                    mAdapter.setUpgradeList(mUpgradeList);
                    mRecyclerView.setAdapter(mAdapter);
                    break;
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public UpgradeFragment() {
    }

    public static UpgradeFragment newInstance() {
        return new UpgradeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upgrade, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.apkList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new UpgradeAdapter(getActivity(), mRecyclerView);
        mAdapter.setUpgradeList(mUpgradeList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.theme_default_primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpHelper.getInstance(getActivity()).obtainUpgradeVersions(mHandler);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        HttpHelper.getInstance(getActivity()).obtainUpgradeVersions(mHandler);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSTALL_REQUEST_CODE) {
            Log.d(TAG, "resultCode:" + resultCode);
            HttpHelper.getInstance(getActivity()).obtainUpgradeVersions(mHandler);
        }
    }
}
