package bjzhou.coolapk.app.ui.adapters

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.UpgradeApkExtend
import bjzhou.coolapk.app.net.ApkDownloader
import bjzhou.coolapk.app.net.DownloadMonitor
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by bjzhou on 14-8-13.
 */
class UpgradeAdapter(private val mActivity: FragmentActivity) : RecyclerView.Adapter<UpgradeAdapter.ViewHolder>() {
    private var mUpgradeList: List<UpgradeApkExtend> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UpgradeAdapter.ViewHolder {
        val convertView = mActivity.layoutInflater.inflate(R.layout.list_item_upgrade_app, viewGroup, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: UpgradeAdapter.ViewHolder, position: Int) {
        holder.titleView.text = mUpgradeList[position].title
        holder.infoView.text = mUpgradeList[position].info
        holder.logoView.setImageDrawable(mUpgradeList[position].logo)
        val changelog = mUpgradeList[position].apk.changelog
        if (!TextUtils.isEmpty(changelog)) {
            holder.changelogView.text = changelog
            holder.changelogView.visibility = View.VISIBLE
        }
        holder.upgradeButton.tag = 0
        holder.upgradeButton.text = "升级"
        val id = mUpgradeList[position].apk.id
        if (ApkDownloader.instance.isDownloading(id)) {
            ApkDownloader.instance.setListener(id, DownloadListener(holder.upgradeButton, id))
        }
    }

    private class DownloadListener(button: Button, private val id: Int) : DownloadMonitor.DownloadListener {

        init {
            button.tag = id
            sButtonMap.put(id, WeakReference(button))
        }

        private val validButton: Button?
            get() {
                val buttonRef = sButtonMap.get(id)
                if (buttonRef != null) {
                    val button = buttonRef.get()
                    if (button != null && button.tag as Int == id) {
                        return button
                    }
                }
                return null
            }

        override fun onDownloading(percent: Int) {
            val button = validButton
            if (button != null) {
                button.text = percent.toString() + "%"
            }
        }

        override fun onFailure(errCode: Int, vararg err: String) {
            val button = validButton
            if (button != null) {
                button.text = "下载失败,点击重试"
            }
        }

        override fun onDownloaded() {
            val button = validButton
            if (button != null) {
                button.text = "正在安装"
            }
        }

        override fun onComplete() {
            val button = validButton
            if (button != null) {
                button.text = "安装成功"
            }
        }

        companion object {

            private val sButtonMap = SparseArray<WeakReference<Button>>()
        }
    }

    override fun getItemId(position: Int): Long {
        return mUpgradeList[position].apk.id.toLong()
    }

    override fun getItemCount(): Int {
        return mUpgradeList.size
    }

    fun setUpgradeList(upgradeList: List<UpgradeApkExtend>) {
        mUpgradeList = upgradeList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var logoView: ImageView = itemView.findViewById(R.id.list_item_icon) as ImageView
        internal var titleView: TextView = itemView.findViewById(R.id.list_item_title) as TextView
        internal var infoView: TextView = itemView.findViewById(R.id.list_item_info) as TextView
        internal var changelogView: TextView = itemView.findViewById(R.id.list_item_description) as TextView
        internal var upgradeButton: Button = itemView.findViewById(R.id.list_item_upgrade) as Button

        init {
            upgradeButton.setOnClickListener {
                val apkExtend = mUpgradeList[adapterPosition]
                if (!ApkDownloader.instance.isDownloading(apkExtend.apk.id)) {
                    upgradeButton.text = "正在准备下载"
                    apkExtend.apk.title = apkExtend.title ?: ""
                    ApkDownloader.instance.downloadAndInstall(mActivity, apkExtend.apk,
                            DownloadListener(upgradeButton, apkExtend.apk.id))
                }
            }
        }
    }

    companion object {

        private val TAG = "UpgradeAdapter"
    }
}
