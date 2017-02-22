package bjzhou.coolapk.app.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.ui.adapters.ApkListAdapter
import bjzhou.coolapk.app.widget.LoadMoreDecoration
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.net.URLEncoder
import java.util.*

/**
 * Created by bjzhou on 14-7-29.
 */
class HomepageFragment : Fragment(), Observer<List<Apk>> {
    private var mQuery: String? = null
    private var mApkList: MutableList<Apk> = ArrayList()
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mAdapter: ApkListAdapter

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private var mPage = 0

    private lateinit var mLayoutManager: LinearLayoutManager
    private var mInsets: Int = 0
    private lateinit var mLoadMoreDecoration: LoadMoreDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            mQuery = args.getString("query")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        mInsets = resources.getDimension(R.dimen.card_insets).toInt()

        mRecyclerView = rootView.findViewById(R.id.apkList) as RecyclerView
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                outRect.set(mInsets, mInsets, mInsets, mInsets)
            }
        })
        mLoadMoreDecoration = LoadMoreDecoration()
        mLoadMoreDecoration.setListener { obtainApkList(++mPage) }
        mRecyclerView.addItemDecoration(mLoadMoreDecoration)

        mAdapter = ApkListAdapter(activity)
        mRecyclerView.adapter = mAdapter

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh) as SwipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeResources(R.color.theme_default_primary)
        mSwipeRefreshLayout.setOnRefreshListener {
            mLoadMoreDecoration.reset()
            mPage = 0
            obtainApkList(++mPage)
        }

        rootView.postDelayed({
            mPage = 0
            obtainApkList(++mPage)
            mSwipeRefreshLayout.isRefreshing = true
        }, 100)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun obtainApkList(page: Int) {
        if (mQuery == null) {
            ApiManager.instance.obtainHomepageApkList(page).subscribeWith(this)
        } else {
            ApiManager.instance.obtainSearchApkList(URLEncoder.encode(mQuery), page).subscribeWith(this)
        }
    }

    override fun onSubscribe(d: Disposable) {}

    override fun onNext(apks: List<Apk>?) {
        if (mPage == 1) {
            mApkList = (apks ?: emptyList<Apk>()) as MutableList<Apk>
            mAdapter.setApkList(mApkList)
            mRecyclerView.adapter = mAdapter
        } else {
            mPage++
            if (apks == null || apks.isEmpty()) {
                mLoadMoreDecoration.loadComplete()
                return
            }
            mApkList.addAll(apks)
            mAdapter.setApkList(mApkList)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        onComplete()
    }

    override fun onComplete() {
        mSwipeRefreshLayout.isRefreshing = false
    }

    companion object {

        private val TAG = "HomepageFragment"

        fun newInstance(): HomepageFragment {
            return HomepageFragment()
        }

        fun newInstance(query: String): HomepageFragment {
            val fragment = HomepageFragment()
            val args = Bundle()
            args.putString("query", query)
            fragment.arguments = args
            return fragment
        }
    }
}
