package bjzhou.coolapk.app.ui.fragments

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
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_upgrade.view.*
import java.util.concurrent.TimeUnit

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

    override fun onResume() {
        super.onResume()

        view?.postDelayed({
            obtainUpgradeVersions()
            view?.swipeRefresh?.isRefreshing = true
        }, 300)
    }

    private fun obtainUpgradeVersions() {
        ApiManager.instance.obtainUpgradeVersions(activity)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    view?.swipeRefresh?.isRefreshing = false
                }
                .subscribe { upgradeApkExtends ->
                    mAdapter.setUpgradeList(upgradeApkExtends)
                    mAdapter.notifyDataSetChanged()
                }
    }

    companion object {

        private val TAG = "UpgradeFragment"

        fun newInstance(): UpgradeFragment {
            return UpgradeFragment()
        }
    }
}
