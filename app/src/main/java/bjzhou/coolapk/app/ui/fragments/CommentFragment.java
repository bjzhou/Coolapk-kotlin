package bjzhou.coolapk.app.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.adapter.CommentAdapter;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.Comment;
import bjzhou.coolapk.app.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjzhou on 14-8-5.
 */
public class CommentFragment extends Fragment implements AbsListView.OnScrollListener {

    private int mId;
    private ExpandableListView mListView;
    private List<Comment> mCommentList = new ArrayList<Comment>();
    private int mPage = 1;
    private CommentAdapter mAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_OBTAIN_COMPLETE:
                    mCommentList = (List<Comment>) msg.obj;
                    mAdapter.setCommentList(mCommentList);
                    mListView.setAdapter(mAdapter);
                    mPage = 1;
                    break;
                case Constant.MSG_OBTAIN_MORE_COMPLETE:
                    mPage++;
                    List<Comment> obj = (List<Comment>) msg.obj;
                    if (obj == null || obj.size() == 0) {
                        Toast.makeText(getActivity(), "没有了", Toast.LENGTH_SHORT).show();
                    }
                    mCommentList.addAll(((List<Comment>) msg.obj));
                    mAdapter.setCommentList(mCommentList);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

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
        HttpHelper.getInstance(getActivity()).obtainCommentList(mId, 1, mHandler);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    // reached the end of the view
                    //TODO show loading status
                    HttpHelper.getInstance(getActivity()).obtainCommentList(mId, mPage + 1, mHandler);
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
}
