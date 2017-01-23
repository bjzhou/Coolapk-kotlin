package bjzhou.coolapk.app.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.Comment;
import bjzhou.coolapk.app.net.ApiManager;
import bjzhou.coolapk.app.ui.adapters.CommentAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by bjzhou on 14-8-5.
 */
public class CommentFragment extends Fragment implements AbsListView.OnScrollListener, Observer<List<Comment>> {

    private int mId;
    private ExpandableListView mListView;
    private List<Comment> mCommentList = new ArrayList<Comment>();
    private int mPage = 0;
    private CommentAdapter mAdapter;

    public CommentFragment() {
    }

    public static CommentFragment newInstance(int id) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mId = args.getInt("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        mListView = (ExpandableListView) rootView.findViewById(R.id.comment_listView);
        mListView.setOnScrollListener(this);
        mAdapter = new CommentAdapter(getActivity(), mListView);
        mAdapter.setCommentList(mCommentList);
        mListView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPage = 0;
        ApiManager.getInstance().obtainCommentList(mId, ++mPage).subscribeWith(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    // reached the end of the view
                    //TODO show loading status
                    ApiManager.getInstance().obtainCommentList(mId, ++mPage).subscribeWith(this);
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

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(List<Comment> comments) {
        if (mPage == 1) {
            mCommentList = comments;
            mAdapter.setCommentList(mCommentList);
            mListView.setAdapter(mAdapter);
        } else {
            if (comments == null || comments.size() == 0) {
                Toast.makeText(getActivity(), "没有了", Toast.LENGTH_SHORT).show();
                return;
            }
            mCommentList.addAll(comments);
            mAdapter.setCommentList(mCommentList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        onComplete();
    }

    @Override
    public void onComplete() {
    }
}
