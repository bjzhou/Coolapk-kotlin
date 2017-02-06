package bjzhou.coolapk.app.ui.adapters

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.ui.activities.AppViewActivity
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by bjzhou on 14-7-31.
 */
class ApkListAdapter(activity: FragmentActivity) : RecyclerView.Adapter<ApkListAdapter.ViewHolder>(), View.OnClickListener {
    private val mActivity: Activity = activity
    private var mApkList: List<Apk> = ArrayList()

    fun setApkList(apkList: List<Apk>) {
        mApkList = apkList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ApkListAdapter.ViewHolder {
        val convertView = mActivity.layoutInflater.inflate(R.layout.list_item_app, viewGroup, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ApkListAdapter.ViewHolder, i: Int) {
        holder.titleView.text = mApkList[i].title
        if (mApkList[i].info == null) {
            var apk_info = "<font color=\"#ff35a1d4\">" + mApkList[i].apkversionname + "</font>"
            apk_info += "<font color=\"black\">, " + mApkList[i].apksize + ", </font>"
            if (mApkList[i].updateFlag == "new") {
                apk_info += "<font color=\"red\">New</font>"
            } else {
                apk_info += "<font color=\"black\">Update</font>"
            }
            mApkList[i].info = Html.fromHtml(apk_info)
        }
        holder.infoView.text = mApkList[i].info
        holder.downnumView.text = mApkList[i].downnum.toString()
        holder.logoView.tag = mApkList[i].title
        Picasso.with(mActivity)
                .load(mApkList[i].logo)
                .placeholder(R.drawable.ic_default_thumbnail)
                .into(holder.logoView)
        holder.ratingBar.rating = mApkList[i].score
    }

    override fun getItemId(position: Int): Long {
        return mApkList[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return mApkList.size
    }

    override fun onClick(v: View) {

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var logoView: ImageView = itemView.findViewById(R.id.list_item_icon) as ImageView
        var titleView: TextView = itemView.findViewById(R.id.list_item_title) as TextView
        var infoView: TextView = itemView.findViewById(R.id.list_item_info) as TextView
        var downnumView: TextView = itemView.findViewById(R.id.list_item_downnum) as TextView
        var ratingBar: RatingBar = itemView.findViewById(R.id.list_item_ratingStar) as RatingBar

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val intent = Intent(mActivity, AppViewActivity::class.java)
            intent.putExtra("id", mApkList[adapterPosition].id)
            mActivity.startActivity(intent)
        }
    }

    companion object {

        private val TAG = "ApkListAdapter"
    }
}
