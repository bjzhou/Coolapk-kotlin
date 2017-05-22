package bjzhou.coolapk.app.ui.fragments

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.ui.adapters.ApkListAdapter
import bjzhou.coolapk.app.viewmodel.HomepageViewModel
import bjzhou.coolapk.app.widget.LoadMoreDecoration

/**
 * Created by bjzhou on 14-7-29.
 */
class HomepageFragment : LifecycleFragment() {
    private lateinit var mViewModel: HomepageViewModel
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mAdapter: ApkListAdapter

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout


    private lateinit var mLayoutManager: LinearLayoutManager
    private var mInsets: Int = 0
    private lateinit var mLoadMoreDecoration: LoadMoreDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders.of(this)[HomepageViewModel::class.java]
        val args = arguments
        if (args != null) {
            mViewModel.mQuery = args.getString("query")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        mInsets = resources.getDimension(R.dimen.card_insets).toInt()

        mRecyclerView = rootView.findViewById(R.id.apkList)
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                outRect.set(mInsets, mInsets, mInsets, mInsets)
            }
        })
        mLoadMoreDecoration = LoadMoreDecoration()
        mLoadMoreDecoration.setListener { mViewModel.obtainApkList() }
        mRecyclerView.addItemDecoration(mLoadMoreDecoration)

        mAdapter = ApkListAdapter(activity)
        mRecyclerView.adapter = mAdapter

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.theme_default_primary)
        mSwipeRefreshLayout.setOnRefreshListener {
            mLoadMoreDecoration.reset()
            mViewModel.mPage = 0
            mViewModel.obtainApkList()
        }

        rootView.postDelayed({
            mViewModel.mPage = 0
            mViewModel.obtainApkList()
            mSwipeRefreshLayout.isRefreshing = true
        }, 100)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.mApkList.observe(this, android.arch.lifecycle.Observer { list ->
            mAdapter.setApkList(list ?: emptyList())
            if (mViewModel.mPage == 1) {
                mRecyclerView.adapter = mAdapter
            } else {
                mAdapter.notifyDataSetChanged()
            }
        })

        mViewModel.mStatus.observe(this, android.arch.lifecycle.Observer { status ->
            when (status) {
                0 -> mLoadMoreDecoration.loadComplete()
                1 -> mSwipeRefreshLayout.isRefreshing = true
                2 -> mSwipeRefreshLayout.isRefreshing = false
            }
        })
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
