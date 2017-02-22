package bjzhou.coolapk.app.ui.adapters

import android.app.DownloadManager
import android.net.Uri
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import bjzhou.coolapk.app.App
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.model.UpgradeApkExtend
import bjzhou.coolapk.app.net.ApkDownloader
import bjzhou.coolapk.app.net.DownloadStatus
import bjzhou.coolapk.app.util.Settings
import bjzhou.coolapk.app.util.Utils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.list_item_upgrade_app.view.*
import java.io.File
import java.util.*

/**
 * Created by bjzhou on 14-8-13.
 */
class UpgradeAdapter(private val mActivity: FragmentActivity) : RecyclerView.Adapter<UpgradeAdapter.ViewHolder>() {
    private var mUpgradeList: List<UpgradeApkExtend> = ArrayList()
    private var mObserveMap = HashMap<Apk, Disposable>()
    private val mButtonMap = WeakHashMap<Apk, Button>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UpgradeAdapter.ViewHolder {
        val convertView = mActivity.layoutInflater.inflate(R.layout.list_item_upgrade_app, viewGroup, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: UpgradeAdapter.ViewHolder, position: Int) {
        val upgradeApk = mUpgradeList[position]
        holder.titleView.text = upgradeApk.title ?: upgradeApk.apk.title ?: "null"
        holder.infoView.text = upgradeApk.info ?: upgradeApk.apk.info ?: "null"
        holder.logoView.setImageDrawable(upgradeApk.logo)
        val changelog = upgradeApk.apk.changelog
        if (!TextUtils.isEmpty(changelog)) {
            holder.changelogView.text = changelog
            holder.changelogView.visibility = View.VISIBLE
        } else {
            holder.changelogView.visibility = View.GONE
        }
        holder.upgradeButton.tag = 0
        holder.upgradeButton.text = "升级"
        holder.upgradeButton.tag = upgradeApk.apk
        mButtonMap.put(upgradeApk.apk, holder.upgradeButton)
    }

    override fun getItemId(position: Int): Long {
        return mUpgradeList[position].apk.id.toLong()
    }

    override fun getItemCount(): Int {
        return mUpgradeList.size
    }

    fun setUpgradeList(upgradeList: List<UpgradeApkExtend>) {
        mUpgradeList = upgradeList
        for (upgradeApk in mUpgradeList) {
            mObserveMap.put(upgradeApk.apk, observeApk(upgradeApk.apk))
        }
    }

    private fun observeApk(apk: Apk): Disposable {
        return ApkDownloader.instance.observeDownloadStatus(apk)
                .subscribe {
                    val button = mButtonMap[apk]
                    if (button != null && button.tag as Apk == apk) {
                        button.post {
                            if (it.status == DownloadStatus.STATUS_NOT_STARTED) {
                                button.text = "升级"
                            } else if (it.status == DownloadManager.STATUS_FAILED) {
                                button.text = "下载失败,点击重试"
                            } else if (it.status == DownloadManager.STATUS_SUCCESSFUL) {
                                button.text = "下载完成"
                            } else {
                                button.text = it.percent.toString() + "%"
                            }
                        }
                    }
                }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var logoView = itemView.list_item_icon
        internal var titleView = itemView.list_item_title
        internal var infoView = itemView.list_item_info
        internal var changelogView = itemView.list_item_description
        internal var upgradeButton = itemView.list_item_upgrade

        init {
            upgradeButton.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                val apkExtend = mUpgradeList[adapterPosition]
                val status = ApkDownloader.instance.getDownloadStatus(apkExtend.apk)
                if (status.status == DownloadManager.STATUS_SUCCESSFUL) {
                    val file = File(Settings.instance.downloadDir, apkExtend.apk.filename)
                    val pkgInfo = App.context.packageManager.getPackageArchiveInfo(file.absolutePath, 0)
                    if (file.exists() && pkgInfo != null) {
                        Utils.installApk(Uri.fromFile(file))
                    }
                } else if (status.status == DownloadStatus.STATUS_NOT_STARTED || status.status == DownloadManager.STATUS_FAILED) {
                    ApkDownloader.instance.download(apkExtend.apk)
                    mObserveMap.put(apkExtend.apk, observeApk(apkExtend.apk))
                }
            }
        }
    }

    companion object {

        private val TAG = "UpgradeAdapter"
    }
}
