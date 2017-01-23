package bjzhou.coolapk.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.net.ApiManager;
import bjzhou.coolapk.app.ui.adapters.UpgradeAdapter;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeFragment extends Fragment {

    public static final int INSTALL_REQUEST_CODE = 101;
    private static final String TAG = "UpgradeFragment";

    private UpgradeAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<UpgradeApkExtend> mUpgradeList = new ArrayList<UpgradeApkExtend>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public UpgradeFragment() {
    }

    public static UpgradeFragment newInstance() {
        return new UpgradeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upgrade, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.apkList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new UpgradeAdapter(getActivity());
        mAdapter.setUpgradeList(mUpgradeList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.theme_default_primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtainUpgradeVersions();
            }
        });
        return rootView;
    }

    private void obtainUpgradeVersions() {
        ApiManager.getInstance().obtainUpgradeVersions(getActivity()).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).subscribe(new Consumer<List<UpgradeApkExtend>>() {
            @Override
            public void accept(List<UpgradeApkExtend> upgradeApkExtends) throws Exception {
                mUpgradeList = upgradeApkExtends;
                mAdapter.setUpgradeList(mUpgradeList);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainUpgradeVersions();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSTALL_REQUEST_CODE) {
            Log.d(TAG, "resultCode:" + resultCode);
            obtainUpgradeVersions();
        }
    }
}
