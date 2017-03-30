package bjzhou.coolapk.app.ui.adapters

import android.support.v4.app.FragmentActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.Comment
import bjzhou.coolapk.app.model.Reply
import bjzhou.coolapk.app.util.TimeUtility
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by bjzhou on 14-8-8.
 */
class CommentAdapter(private val mActivity: FragmentActivity, private val mListView: ExpandableListView) : BaseExpandableListAdapter() {
    private var mCommentList: List<Comment> = ArrayList()

    fun setCommentList(commentList: List<Comment>) {
        mCommentList = commentList
    }

    override fun getGroupCount(): Int {
        return mCommentList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        if (mCommentList[groupPosition].subrows == null) {
            return 0
        }
        return mCommentList[groupPosition].subrows?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return mCommentList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Reply? {
        return mCommentList[groupPosition].subrows?.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return mCommentList[groupPosition].id
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return mCommentList[groupPosition].subrows?.get(childPosition)?.id ?: 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View? {
        val holder: ViewHolder
        var view: View? = convertView
        if (view == null) {
            holder = ViewHolder()
            view = mActivity.layoutInflater.inflate(R.layout.list_item_comment, parent, false)
            holder.userIconView = view.findViewById(R.id.list_item_icon) as ImageView
            holder.titleView = view.findViewById(R.id.list_item_title) as TextView
            holder.timeView = view.findViewById(R.id.list_item_time) as TextView
            holder.msgView = view.findViewById(R.id.list_item_message) as TextView
            //holder.infoView = (TextView) convertView.findViewById(R.id.list_item_info);
            holder.replyNumView = view.findViewById(R.id.list_item_reply_num) as TextView

            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        Picasso.with(mActivity)
                .load(mCommentList[groupPosition].useravatar)
                .placeholder(R.drawable.ic_default_avatar)
                .into(holder.userIconView)
        //holder.userIconView.setImageUrl(mCommentList.get(position).getUseravatar(), ApiManager.getInstance(mActivity).getImageLoader());
        var title = mCommentList[groupPosition].title
        title = title.split("来自".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()[0]
        holder.titleView?.text = title
        holder.timeView?.text = TimeUtility.getTime(mCommentList[groupPosition].lastupdate)
        val msg = mCommentList[groupPosition].message
        holder.msgView?.text = Html.fromHtml(msg)
        holder.msgView?.movementMethod = LinkMovementMethod.getInstance()
        //holder.msgView.setMovementMethod(LinkMovementMethod.getInstance());
        //holder.infoView.setText(mCommentList.get(position).getInfo());
        holder.replyNumView?.text = mCommentList[groupPosition].replynum.toString() + ""
        holder.replyNumView?.setOnClickListener { mListView.performItemClick(convertView, groupPosition, mCommentList[groupPosition].id) }

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        if (mCommentList[groupPosition].subrows != null && mCommentList[groupPosition].subrows?.size ?: 0 > 0) {
            val holder: ReplyViewHolder
            var view: View? = convertView
            if (view == null) {
                holder = ReplyViewHolder()
                view = mActivity.layoutInflater.inflate(R.layout.list_item_comment_reply, parent, false)
                holder.userIconView = view.findViewById(R.id.list_item_icon) as ImageView
                holder.msgView = view.findViewById(R.id.list_item_message) as TextView

                view.tag = holder
            } else {
                holder = view.tag as ReplyViewHolder
            }

            val reply = mCommentList[groupPosition].subrows?.get(childPosition)
            Picasso.with(mActivity)
                    .load(reply?.useravatar)
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(holder.userIconView)
            holder.msgView?.text = Html.fromHtml(reply?.message)
            holder.msgView?.movementMethod = LinkMovementMethod.getInstance()

            return view
        }
        return null
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    internal class ViewHolder {
        var userIconView: ImageView? = null
        var titleView: TextView? = null
        var timeView: TextView? = null
        var msgView: TextView? = null
        //TextView infoView;
        var replyNumView: TextView? = null
    }

    internal class ReplyViewHolder {
        var userIconView: ImageView? = null
        var msgView: TextView? = null
    }
}
