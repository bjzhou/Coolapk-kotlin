package bjzhou.coolapk.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.adapter.UpgradeAdapter;
import bjzhou.coolapk.app.custom.PullToRefreshFragment;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeFragment extends PullToRefreshFragment {

    public static final int INSTALL_REQUEST_CODE = 101;
    private static final String TAG = "UpgradeFragment";

    private ListView mListView;
    private UpgradeAdapter mAdapter;
    private List<UpgradeApkExtend> mUpgradeList = new ArrayList<UpgradeApkExtend>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_OBTAIN_COMPLETE:
                    mUpgradeList = (List<UpgradeApkExtend>) msg.obj;
                    mAdapter.setUpgradeList(mUpgradeList);
                    mListView.setAdapter(mAdapter);
                    break;
            }

            getPullToRefreshLayout().setRefreshComplete();
        }
    };

    public UpgradeFragment() {
    }

    public static UpgradeFragment newInstance() {
        return new UpgradeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upgrade, null);
        mListView = (ListView) rootView.findViewById(R.id.apkList);
        mAdapter = new UpgradeAdapter(getActivity());
        mAdapter.setUpgradeList(mUpgradeList);
        mListView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(R.string.title_section2);


        HttpHelper.getInstance(getActivity()).obtainUpgradeVersions(mHandler);
        getPullToRefreshLayout().setRefreshing(true);
    }

    @Override
    protected void onActionBarClick() {
        mListView.smoothScrollToPosition(0);
    }

    @Override
    public void onRefreshStarted(View view) {
        HttpHelper.getInstance(getActivity()).obtainUpgradeVersions(mHandler);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSTALL_REQUEST_CODE) {
            Log.d(TAG, "resultCode:" + resultCode);
            HttpHelper.getInstance(getActivity()).obtainUpgradeVersions(mHandler);
        }
    }
}
