package bjzhou.coolapk.app.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ExpandableListView
import android.widget.Toast
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.Comment
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.ui.adapters.CommentAdapter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by bjzhou on 14-8-5.
 */
class CommentFragment : Fragment(), AbsListView.OnScrollListener, Observer<List<Comment>> {

    private var mId: Int = 0
    private lateinit var mListView: ExpandableListView
    private var mCommentList: MutableList<Comment> = ArrayList()
    private var mPage = 0
    private lateinit var mAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        mId = args.getInt("id")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_comment, container, false)

        mListView = rootView.findViewById(R.id.comment_listView)
        mListView.setOnScrollListener(this)
        mAdapter = CommentAdapter(activity, mListView)
        mAdapter.setCommentList(mCommentList)
        mListView.setAdapter(mAdapter)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPage = 0
        ApiManager.instance.obtainCommentList(mId, ++mPage).subscribeWith(this)
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        when (scrollState) {
            AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> if (view.lastVisiblePosition == view.count - 1) {
                // reached the end of the view
                //TODO show loading status
                ApiManager.instance.obtainCommentList(mId, ++mPage).subscribeWith(this)
            }
            AbsListView.OnScrollListener.SCROLL_STATE_FLING -> {
            }
            AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {
            }
        }
    }

    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}

    override fun onSubscribe(d: Disposable) {}

    override fun onNext(comments: List<Comment>?) {
        if (mPage == 1) {
            mCommentList = (comments ?: emptyList()) as MutableList<Comment>
            mAdapter.setCommentList(mCommentList)
            mListView.setAdapter(mAdapter)
        } else {
            if (comments == null || comments.isEmpty()) {
                Toast.makeText(activity, "没有了", Toast.LENGTH_SHORT).show()
                return
            }
            mCommentList.addAll(comments)
            mAdapter.setCommentList(mCommentList)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onError(e: Throwable) {
        Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
        onComplete()
    }

    override fun onComplete() {}

    companion object {

        fun newInstance(id: Int): CommentFragment {
            val fragment = CommentFragment()
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }
}
