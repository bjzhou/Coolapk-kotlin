package bjzhou.coolapk.app.adapter;

import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.Comment;
import bjzhou.coolapk.app.model.Reply;
import bjzhou.coolapk.app.util.TimeUtility;

/**
 * Created by bjzhou on 14-8-8.
 */
public class CommentAdapter extends BaseExpandableListAdapter {

    private final FragmentActivity mActivity;
    private final ExpandableListView mListView;
    private List<Comment> mCommentList;

    public CommentAdapter(FragmentActivity activity, ExpandableListView listView) {
        mActivity = activity;
        mListView = listView;
    }

    public void setCommentList(List<Comment> commentList) {
        mCommentList = commentList;
    }

    @Override
    public int getGroupCount() {
        return mCommentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mCommentList.get(groupPosition).getSubrows() == null) {
            return 0;
        }
        return mCommentList.get(groupPosition).getSubrows().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCommentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCommentList.get(groupPosition).getSubrows().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mCommentList.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mCommentList.get(groupPosition).getSubrows().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, final View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            holder = new ViewHolder();
            view = mActivity.getLayoutInflater().inflate(R.layout.list_item_comment, parent, false);
            holder.userIconView = (ImageView) view.findViewById(R.id.list_item_icon);
            holder.titleView = (TextView) view.findViewById(R.id.list_item_title);
            holder.timeView = (TextView) view.findViewById(R.id.list_item_time);
            holder.msgView = (TextView) view.findViewById(R.id.list_item_message);
            //holder.infoView = (TextView) convertView.findViewById(R.id.list_item_info);
            holder.replyNumView = (TextView) view.findViewById(R.id.list_item_reply_num);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(mActivity)
                .load(mCommentList.get(groupPosition).getUseravatar())
                .placeholder(R.drawable.ic_default_avatar)
                .into(holder.userIconView);
        //holder.userIconView.setImageUrl(mCommentList.get(position).getUseravatar(), HttpHelper.getInstance(mActivity).getImageLoader());
        String title = mCommentList.get(groupPosition).getTitle();
        title = title.split("来自")[0];
        holder.titleView.setText(title);
        holder.timeView.setText(TimeUtility.getTime(mCommentList.get(groupPosition).getLastupdate()));
        String msg = mCommentList.get(groupPosition).getMessage();
        holder.msgView.setText(Html.fromHtml(msg));
        holder.msgView.setMovementMethod(LinkMovementMethod.getInstance());
        //holder.msgView.setMovementMethod(LinkMovementMethod.getInstance());
        //holder.infoView.setText(mCommentList.get(position).getInfo());
        holder.replyNumView.setText(mCommentList.get(groupPosition).getReplynum() + "");
        holder.replyNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.performItemClick(convertView, groupPosition, mCommentList.get(groupPosition).getId());
            }
        });


        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (mCommentList.get(groupPosition).getSubrows() != null && mCommentList.get(groupPosition).getSubrows().size() > 0) {
            ReplyViewHolder holder;
            View view = convertView;
            if (view == null) {
                holder = new ReplyViewHolder();
                view = mActivity.getLayoutInflater().inflate(R.layout.list_item_comment_reply, parent, false);
                holder.userIconView = (ImageView) view.findViewById(R.id.list_item_icon);
                holder.msgView = (TextView) view.findViewById(R.id.list_item_message);

                view.setTag(holder);
            } else {
                holder = (ReplyViewHolder) view.getTag();
            }

            Reply reply = mCommentList.get(groupPosition).getSubrows().get(childPosition);
            Picasso.with(mActivity)
                    .load(reply.getUseravatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(holder.userIconView);
            holder.msgView.setText(Html.fromHtml(reply.getMessage()));
            holder.msgView.setMovementMethod(LinkMovementMethod.getInstance());

            return view;
        }
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class ViewHolder {
        ImageView userIconView;
        TextView titleView;
        TextView timeView;
        TextView msgView;
        //TextView infoView;
        TextView replyNumView;
    }

    static class ReplyViewHolder {
        ImageView userIconView;
        TextView msgView;
    }
}
