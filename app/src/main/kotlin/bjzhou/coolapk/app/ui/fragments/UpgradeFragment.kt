package bjzhou.coolapk.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.ui.adapters.UpgradeAdapter
import kotlinx.android.synthetic.main.fragment_upgrade.view.*

/**
 * Created by bjzhou on 14-8-13.
 */
class UpgradeFragment : Fragment() {

    private lateinit var mAdapter: UpgradeAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_upgrade, container, false)

        mLayoutManager = LinearLayoutManager(activity)
        rootView.apkList.layoutManager = mLayoutManager
        rootView.apkList.itemAnimator = DefaultItemAnimator()

        mAdapter = UpgradeAdapter(activity)
        mAdapter.setUpgradeList(emptyList())
        rootView.apkList.adapter = mAdapter

        rootView.swipeRefresh.setColorSchemeResources(R.color.theme_default_primary)
        rootView.swipeRefresh.setOnRefreshListener { obtainUpgradeVersions() }
        return rootView
    }

    private fun obtainUpgradeVersions() {
        ApiManager.instance.obtainUpgradeVersions(activity).doFinally { view?.swipeRefresh?.isRefreshing = false }.subscribe { upgradeApkExtends ->
            mAdapter.setUpgradeList(upgradeApkExtends)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        obtainUpgradeVersions()
        view.swipeRefresh.isRefreshing = true
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
