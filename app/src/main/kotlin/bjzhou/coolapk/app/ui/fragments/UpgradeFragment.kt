package bjzhou.coolapk.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.UpgradeApkExtend
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.ui.adapters.UpgradeAdapter
import java.util.*

/**
 * Created by bjzhou on 14-8-13.
 */
class UpgradeFragment : Fragment() {

    private lateinit var mAdapter: UpgradeAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var mUpgradeList: List<UpgradeApkExtend> = ArrayList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_upgrade, container, false)

        mRecyclerView = rootView.findViewById(R.id.apkList) as RecyclerView
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()

        mAdapter = UpgradeAdapter(activity)
        mAdapter.setUpgradeList(mUpgradeList)
        mRecyclerView.adapter = mAdapter

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh) as SwipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeResources(R.color.theme_default_primary)
        mSwipeRefreshLayout.setOnRefreshListener { obtainUpgradeVersions() }
        return rootView
    }

    private fun obtainUpgradeVersions() {
        ApiManager.instance.obtainUpgradeVersions(activity).doFinally { mSwipeRefreshLayout.isRefreshing = false }.subscribe { upgradeApkExtends ->
            mUpgradeList = upgradeApkExtends
            mAdapter.setUpgradeList(mUpgradeList)
            mRecyclerView.adapter = mAdapter
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        obtainUpgradeVersions()
        mSwipeRefreshLayout.isRefreshing = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == INSTALL_REQUEST_CODE) {
            Log.d(TAG, "resultCode:" + resultCode)
            obtainUpgradeVersions()
        }
    }

    companion object {

        val INSTALL_REQUEST_CODE = 101
        private val TAG = "UpgradeFragment"

        fun newInstance(): UpgradeFragment {
            return UpgradeFragment()
        }
    }
}
